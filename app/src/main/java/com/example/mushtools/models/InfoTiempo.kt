package com.example.mushtools.models


import com.google.gson.annotations.SerializedName

// Obtingut amb https://transform.tools/json-to-kotlin i modificat per simplificar

data class InfoTiempo(
    val copyright: String,
    val use: String,
    val information: Information,
    val web: String,
    val language: String,
    val locality: Locality,
//    val day: List<DayA>,
    val day1: Dia,
    val day2: Dia,
    val day3: Dia,
    val day4: Dia,
    val day5: Dia,
    val day6: Dia,
    val day7: Dia,

    @SerializedName("hour_hour")
    val hourHour: HourHour,
)

data class Information(
    val temperature: String,
    val wind: String,
    val humidity: String,
    val pressure: String,
)

data class Locality(
    val name: String,
    @SerializedName("url_weather_forecast_15_days")
    val urlWeatherForecast15Days: String,
    @SerializedName("url_hourly_forecast")
    val urlHourlyForecast: String,
    val country: String,
    @SerializedName("url_country")
    val urlCountry: String,
)

data class Dia(
    val date: String,
    @SerializedName("temperature_max")
    val temperatureMax: Double,
    @SerializedName("temperature_min")
    val temperatureMin: Double,
    val icon: String,
    val text: String,
    val humidity: Long,
    val wind: Long,
    @SerializedName("wind_direction")
    val windDirection: String,
    @SerializedName("icon_wind")
    val iconWind: String,
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    @SerializedName("moon_phases_icon")
    val moonPhasesIcon: String,
)

data class HourHour(
    val hour1: HourA,
    val hour2: HourA,
    val hour3: HourA,
    val hour4: HourA,
    val hour5: HourA,
    val hour6: HourA,
    val hour7: HourA,
    val hour8: HourA,
    val hour9: HourA,
    val hour10: HourA,
    val hour11: HourA,
    val hour12: HourA,
    val hour13: HourA,
    val hour14: HourA,
    val hour15: HourA,
    val hour16: HourA,
    val hour17: HourA,
    val hour18: HourA,
    val hour19: HourA,
    val hour20: HourA,
    val hour21: HourA,
    val hour22: HourA,
    val hour23: HourA,
    val hour24: HourA,
    val hour25: HourA,
)

data class HourA(
    val date: String,
    @SerializedName("hour_data")
    val hourData: String,
    val temperature: Double,
    val text: String,
    val humidity: Long,
    val pressure: Long,
    val icon: String,
    val wind: Long,
    @SerializedName("wind_direction")
    val windDirection: String,
    @SerializedName("icon_wind")
    val iconWind: String,
)
