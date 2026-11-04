package com.example.model

enum class WeatherCondition {
  SUNNY,
  PARTLY_CLOUDY,
  CLOUDY,
  RAINY,
  THUNDERSTORM,
  SNOWY
}

data class WeatherInfo(
  val temperature: String = "21°C",
  val condition: WeatherCondition = WeatherCondition.PARTLY_CLOUDY,
  val locationName: String = "Berlin"
)
