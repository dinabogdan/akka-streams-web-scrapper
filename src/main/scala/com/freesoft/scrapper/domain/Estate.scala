package com.freesoft.scrapper.domain

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

  sealed trait EnumVal

  case object Agency extends EnumVal

  case object Individual extends EnumVal

}

object EstateTypes {

  sealed trait EstateType {
    val value: String
  }

  case class Apartment(value: String = "apartamente") extends EstateType

  case class House(value: String = "case") extends EstateType

  case class ConstructionLand(value: String = "terenuri constructie") extends EstateType

  case class AgricultureLand(value: String = "terenuri agricole") extends EstateType

}

object SurfaceUnitOfMeasure {

  sealed trait EnumVal

  case object SquaredMeters

  case object SquaredKilometers

}

object EstateProvider {

  sealed trait EnumVal

  case object ImobiliareRO

  case object OLXRO

}

case class EstateSurface(value: Double, unitOfMeasure: SurfaceUnitOfMeasure.EnumVal)

case class EstatePrice(value: BigDecimal, currency: Currency.EnumVal = Currency.EUR)

case class EstatePlace(country: Option[Country.EnumVal] = Some(Country.RO),
                       county: Option[String] = None,
                       zone: Option[String] = None)

case class EstateSeller(name: Option[String] = None,
                        sType: Option[SellerType.EnumVal] = Some(SellerType.Agency))

case class EstateId(value: String)

sealed abstract class Estate {

  val id: EstateId
  val price: EstatePrice
  val place: EstatePlace
  val seller: EstateSeller
  val estateType: EstateTypes.EstateType
  val surface: EstateSurface
  val provider: EstateProvider.EnumVal
}

case class RoomNumbers(value: Int)

case class FloorNumber(value: Int)

case class BuildingYear(value: Int)

case class Apartment(override val id: EstateId,
                     override val price: EstatePrice,
                     override val place: EstatePlace,
                     override val seller: EstateSeller,
                     override val estateType: EstateTypes.Apartment,
                     override val surface: EstateSurface,
                     override val provider: EstateProvider.EnumVal,
                     roomNumbers: RoomNumbers,
                     floorNumber: FloorNumber,
                     buildingYear: BuildingYear
                    ) extends Estate


case class House(override val id: EstateId,
                 override val price: EstatePrice,
                 override val place: EstatePlace,
                 override val seller: EstateSeller,
                 override val estateType: EstateTypes.Apartment,
                 override val surface: EstateSurface,
                 override val provider: EstateProvider.EnumVal,
                 roomNumbers: RoomNumbers,
                 buildingYear: BuildingYear
                ) extends Estate


