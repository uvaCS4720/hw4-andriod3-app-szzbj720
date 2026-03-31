package edu.nd.pmcburne.hello.data.api

import com.squareup.moshi.Json
import retrofit2.http.GET

data class ApiPlacemark(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "tag_list") val tagList: List<String>,
    @Json(name = "description") val description: String,
    @Json(name = "visual_center") val visualCenter: ApiVisualCenter
)

data class ApiVisualCenter(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double
)

interface PlacemarkService {
    @GET("~wxt4gm/placemarks.json")
    suspend fun getPlacemarks(): List<ApiPlacemark>
}