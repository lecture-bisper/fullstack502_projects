package bitc.full502.lostandfound.data.api

import android.R
import bitc.full502.lostandfound.data.model.GeoData
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GeoService {

    @GET("geocode")
    suspend fun getCoordinateFromAddress(
        @Header("X-NCP-APIGW-API-KEY-ID") keyId: String,
        @Header("X-NCP-APIGW-API-KEY") key: String,
        @Query("query") query: String,
        @Query("coordinate") coordinate: String? = null ,
    ): GeoData
}