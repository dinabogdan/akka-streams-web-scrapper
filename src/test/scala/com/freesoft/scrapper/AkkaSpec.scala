package com.freesoft.scrapper

import akka.actor.{ActorRefFactory, ActorSystem}
import akka.stream.Materializer
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AkkaSpec extends ScalaFutures with BeforeAndAfterAll {
  this: Suite =>

  implicit protected val system = ActorSystem()
  implicit protected val actorRefFactory: ActorRefFactory = system
  implicit protected val materializer = Materializer(system)
  implicit protected val executionContext: ExecutionContext = system.dispatcher

  override implicit def patienceConfig: PatienceConfig = super.patienceConfig.copy(timeout = 5.seconds)

  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }


}
