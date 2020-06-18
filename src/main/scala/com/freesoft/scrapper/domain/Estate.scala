package com.freesoft.scrapper.domain

import java.util.UUID

object Currency {

  sealed trait EnumVal

  case object RON extends EnumVal

  case object EUR extends EnumVal

  case object USD extends EnumVal

}

object Country {

  sealed trait EnumVal

  case object RO extends EnumVal

}

object SellerType {

  def asInstaceOf(value: String): SellerType.EnumVal = value match {
    case "agency" => SellerType.Agency
    case "individual" => SellerType.Individual
  }

  sealed trait EnumVal

  case object Agency extends EnumVal

  case object Individual extends EnumVal

}

object EstateTypes {

  sealed abstract class EstateType(val value: String = "")

  case object Apartment extends EstateType {
    override val value: String = "apartament"
  }

  case object House extends EstateType {
    override val value: String = "casavila"
  }

  case object BuildingArea extends EstateType {
    override val value: String = "teren"
  }

  case object AgricultureArea extends EstateType {
    override val value: String = "teren"
  }

}

object SurfaceUnitOfMeasure {

  sealed trait EnumVal

  case object SquaredMeters extends EnumVal

  case object SquaredKilometers extends EnumVal

}

object LengthUnitOfMeasure {

  sealed trait EnumVal

  case object Meters extends EnumVal

}

object EstateProvider {

  sealed trait EnumVal

  case object ImobiliareRO extends EnumVal

  case object OLXRO extends EnumVal

}

case class EstateSurface(value: Double, unitOfMeasure: SurfaceUnitOfMeasure.EnumVal)

case class EstatePrice(value: BigDecimal, currency: Currency.EnumVal = Currency.EUR)

case class EstatePlace(country: Option[Country.EnumVal] = Some(Country.RO),
                       county: Option[String] = None,
                       zone: Option[String] = None)

case class EstateSeller(name: Option[String] = None,
                        sType: Option[SellerType.EnumVal] = Some(SellerType.Agency))

case class EstateId(value: String)

case object EstateId {

  def anEstateId(): EstateId = EstateId(UUID.randomUUID().toString)

}

case class EstateLink(value: String)

sealed abstract class Estate {

  val id: EstateId
  val price: EstatePrice
  val place: EstatePlace
  val seller: EstateSeller
  val estateType: EstateTypes.EstateType
  val surface: EstateSurface
  val provider: EstateProvider.EnumVal
  val link: EstateLink
}

case class RoomNumbers(value: Int)

case class FloorNumber(value: Int)

case class BuildingYear(value: Int)

case class Apartment(override val id: EstateId,
                     override val price: EstatePrice,
                     override val place: EstatePlace,
                     override val seller: EstateSeller,
                     override val estateType: EstateTypes.Apartment.type,
                     override val surface: EstateSurface,
                     override val provider: EstateProvider.EnumVal,
                     override val link: EstateLink,
                     roomNumbers: RoomNumbers,
                     floorNumber: Option[FloorNumber] = None,
                     buildingYear: Option[BuildingYear] = None) extends Estate


case class House(override val id: EstateId,
                 override val price: EstatePrice,
                 override val place: EstatePlace,
                 override val seller: EstateSeller,
                 override val estateType: EstateTypes.House.type,
                 override val surface: EstateSurface,
                 override val provider: EstateProvider.EnumVal,
                 override val link: EstateLink,
                 roomNumbers: RoomNumbers,
                 buildingYear: Option[BuildingYear] = None) extends Estate

object AreaClassification {

  sealed class EnumVal(name: String)

  case class Urban(name: String = "intravilan") extends EnumVal(name)

  case class OutsideUrban(name: String = "extravilan") extends EnumVal(name)

}

case class ConstructionLandStreetOpening(value: Int, unitOfMeasure: LengthUnitOfMeasure.Meters.type)

case class BuildingArea(override val id: EstateId,
                        override val price: EstatePrice,
                        override val place: EstatePlace,
                        override val seller: EstateSeller,
                        override val estateType: EstateTypes.BuildingArea.type,
                        override val surface: EstateSurface,
                        override val provider: EstateProvider.EnumVal,
                        override val link: EstateLink,
                        landClassification: Option[AreaClassification.EnumVal] = None,
                        streetOpening: Option[ConstructionLandStreetOpening] = None) extends Estate

case class AgricultureArea(override val id: EstateId,
                           override val price: EstatePrice,
                           override val place: EstatePlace,
                           override val seller: EstateSeller,
                           override val estateType: EstateTypes.EstateType,
                           override val surface: EstateSurface,
                           override val provider: EstateProvider.EnumVal,
                           override val link: EstateLink,
                           landClassifications: Option[AreaClassification.EnumVal] = None,
                           streetOpening: Option[ConstructionLandStreetOpening] = None) extends Estate



