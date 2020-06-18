package com.freesoft.scrapper.infrastructure

import com.freesoft.scrapper.domain.EstateTypes
import com.freesoft.scrapper.domain.EstateTypes.EstateType


object implicits {

  implicit class StringWrapper(value: String) {
    def toEstateType: EstateType = value match {
      case EstateTypes.Apartment.value => EstateTypes.Apartment
      case EstateTypes.House.value => EstateTypes.House
      case EstateTypes.AgricultureArea.value => EstateTypes.AgricultureArea
      case EstateTypes.BuildingArea.value => EstateTypes.BuildingArea
      case _ => throw new IllegalArgumentException(s"The provided $value is not a well known estate type")
    }
  }

  implicit class DoubleWrapper(value: Double) {
    def toBigDecimal: BigDecimal = BigDecimal.valueOf(value)
  }

}
