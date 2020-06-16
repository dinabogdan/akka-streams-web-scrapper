package com.freesoft.scrapper.infrastructure

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.{OverflowStrategy, QueueOfferResult}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

class WebScraper(
                  val webScraperConfig: WebScraperConfig
                )(
                  implicit val actorSystem: ActorSystem,
                  implicit val executionContext: ExecutionContext) {

  val queueRequests: Flow[Seq[HttpRequest], Seq[Future[HttpResponse]], NotUsed] = Flow[Seq[HttpRequest]]
    .map(sequence =>
      sequence.map(
        request => queueRequest(request)
      )
    )

  def queueRequest(request: HttpRequest): Future[HttpResponse] = {
    val responsePromise = Promise[HttpResponse]()
    queue.offer(request -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued =>
        responsePromise.future
      case QueueOfferResult.Dropped =>
        Future.failed(new RuntimeException("Queue overflowed. Try again later"))
      case QueueOfferResult.Failure(ex) =>
        Future.failed(ex)
      case QueueOfferResult.QueueClosed =>
        Future.failed(new RuntimeException("Queue was closed (pool shut down) while running the request. Try again later."))
    }
  }

  private val httpResponseHandler: Sink[(Try[HttpResponse], Promise[HttpResponse]), Future[Done]] =
    Sink.foreach[(Try[HttpResponse], Promise[HttpResponse])]({
      case (Success(resp), p) => p.success(resp)
      case (Failure(e), p) => p.failure(e)
    })

  private val poolClientFlow = Http().cachedHostConnectionPoolHttps[Promise[HttpResponse]](webScraperConfig.baseUrl)

  private val queue = Source.queue[(HttpRequest, Promise[HttpResponse])](
    webScraperConfig.httpQueueBufferSize, OverflowStrategy.backpressure
  ).via(poolClientFlow)
    .to(httpResponseHandler)
    .run()


}
