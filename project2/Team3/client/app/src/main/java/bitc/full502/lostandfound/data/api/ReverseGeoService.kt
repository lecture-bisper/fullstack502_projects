package bitc.full502.lostandfound.data.api

import bitc.full502.lostandfound.data.model.ReverseGeoData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReverseGeoService {

    @GET("gc")
    fun getAddressFromCoordinate(
        @Header("x-ncp-apigw-api-key-id") KeyId : String,
        @Header("x-ncp-apigw-api-key") Key : String,
//        좌표부분
        @Query("coords") coords: String,
//        좌표계 , 필요함
        @Query("sourcecrs") sourceCrs: String = "epsg:4326",
//        주소 , 도로명 우선
        @Query("orders") orders: String = "roadaddr,addr",
//        응답결과 포맷
        @Query("output") output: String = "json"
    ): Call<ReverseGeoData>


}