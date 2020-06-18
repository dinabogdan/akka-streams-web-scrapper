package com.freesoft.scrapper.infrastructure

import com.freesoft.scrapper.domain.SurfaceUnitOfMeasure.SquaredMeters
import com.freesoft.scrapper.domain.{AgricultureArea, Apartment, BuildingArea, Country, Currency, Estate, EstateId, EstateLink, EstatePlace, EstatePrice, EstateProvider, EstateSeller, EstateSurface, EstateTypes, House, RoomNumbers, SellerType, SurfaceUnitOfMeasure}
import org.jsoup.nodes.Element
import com.freesoft.scrapper.infrastructure.implicits._

import scala.collection.immutable

class ElementTransformerDispatcher(val transformers: immutable.Map[EstateTypes.EstateType, ElementTransformer[Estate]]) {

  val receiveAndTransform: Element => Estate = {
    element =>
      val estateType = elementAttrSelector(element)("data-name").toEstateType
      estateType match {
        case EstateTypes.Apartment => transformers(EstateTypes.Apartment)(element)
        case EstateTypes.House => transformers(EstateTypes.House).apply(element)
        case EstateTypes.AgricultureArea => transformers(EstateTypes.AgricultureArea).apply(element)
        case EstateTypes.BuildingArea => transformers(EstateTypes.BuildingArea).apply(element)
      }
  }

}

sealed abstract class ElementTransformer[+T <: Estate] {

  def apply(element: Element): T

}

case class ApartmentTransformer() extends ElementTransformer[Apartment] {

  override def apply(element: Element): Apartment = {

    val selectedElement: Selector => AttributeValue = elementAttrSelector(element)
    val roomNumbers = selectedElement("data-camere").toInt
    val county = selectedElement("data-judet")
    val place = selectedElement("data-zona")
    val price = selectedElement("data-price").toDouble
    val surface = selectedElement("data-surface").toDouble
    val sellerType = selectedElement("data-ssellertype")
    val sellerName = selectedElement("data-ssellername")
    val link = (elementSelector(element) andThen elementsAttrSelector) (".visible-xs mobile-container-url")("href")

    Apartment(
      id = EstateId.anEstateId(),
      price = EstatePrice(value = BigDecimal.valueOf(price), currency = Currency.EUR),
      place = EstatePlace(country = Some(Country.RO), county = Some(county), zone = Some(place)),
      seller = EstateSeller(name = Some(sellerName), sType = Some(SellerType.asInstaceOf(sellerType))),
      estateType = EstateTypes.Apartment,
      surface = EstateSurface(value = surface, unitOfMeasure = SurfaceUnitOfMeasure.SquaredMeters),
      provider = EstateProvider.ImobiliareRO,
      roomNumbers = RoomNumbers(roomNumbers),
      link = EstateLink(link)
    )
  }
}

case class HouseTransformer() extends ElementTransformer[House] {

  override def apply(element: Element): House = {

    val selectedElement: Selector => AttributeValue = elementAttrSelector(element)
    val roomNumbers = selectedElement("data-camere").toInt
    val county = selectedElement("data-judet")
    val place = selectedElement("data-zona")
    val price = selectedElement("data-price").toDouble
    val surface = selectedElement("data-surface").toDouble
    val sellerType = selectedElement("data-ssellertype")
    val sellerName = selectedElement("data-ssellername")
    val link = (elementSelector(element) andThen elementsAttrSelector) (".visible-xs mobile-container-url")("href")

    House(
      id = EstateId.anEstateId(),
      price = EstatePrice(value = BigDecimal.valueOf(price), currency = Currency.EUR),
      place = EstatePlace(country = Some(Country.RO), county = Some(county), zone = Some(place)),
      seller = EstateSeller(name = Some(sellerName), sType = Some(SellerType.asInstaceOf(sellerType))),
      estateType = EstateTypes.House,
      surface = EstateSurface(value = surface, unitOfMeasure = SquaredMeters),
      provider = EstateProvider.ImobiliareRO,
      link = EstateLink(link),
      roomNumbers = RoomNumbers(roomNumbers)
    )
  }
}

case class BuildingAreaTransformer() extends ElementTransformer[BuildingArea] {

  override def apply(element: Element): BuildingArea = {
    val selectedElement: Selector => AttributeValue = elementAttrSelector(element)
    val county = selectedElement("data-judet")
    val place = selectedElement("data-zona")
    val price = selectedElement("data-price").toDouble
    val surface = selectedElement("data-surface").toDouble
    val sellerType = selectedElement("data-ssellertype")
    val sellerName = selectedElement("data-ssellername")
    val link = (elementSelector(element) andThen elementsAttrSelector) (".visible-xs mobile-container-url")("href")

    BuildingArea(
      id = EstateId.anEstateId(),
      price = EstatePrice(price.toBigDecimal, Currency.EUR),
      place = EstatePlace(country = Some(Country.RO), county = Some(county), zone = Some(place)),
      seller = EstateSeller(name = Some(sellerName), sType = Some(SellerType.asInstaceOf(sellerType))),
      estateType = EstateTypes.BuildingArea,
      surface = EstateSurface(value = surface, unitOfMeasure = SquaredMeters),
      provider = EstateProvider.ImobiliareRO,
      link = EstateLink(link)
    )
  }
}

case class AgricultureAreaTransformer() extends ElementTransformer[AgricultureArea] {

  override def apply(element: Element): AgricultureArea = {
    val selectedElement: Selector => AttributeValue = elementAttrSelector(element)
    val county = selectedElement("data-judet")
    val place = selectedElement("data-zona")
    val price = selectedElement("data-price").toDouble
    val surface = selectedElement("data-surface").toDouble
    val sellerType = selectedElement("data-ssellertype")
    val sellerName = selectedElement("data-ssellername")
    val link = (elementSelector(element) andThen elementsAttrSelector) (".visible-xs mobile-container-url")("href")

    AgricultureArea(
      id = EstateId.anEstateId(),
      price = EstatePrice(price.toBigDecimal, Currency.EUR),
      place = EstatePlace(country = Some(Country.RO), county = Some(county), zone = Some(place)),
      seller = EstateSeller(name = Some(sellerName), sType = Some(SellerType.asInstaceOf(sellerType))),
      estateType = EstateTypes.AgricultureArea,
      surface = EstateSurface(value = surface, unitOfMeasure = SquaredMeters),
      provider = EstateProvider.ImobiliareRO,
      link = EstateLink(link)
    )
  }
}

