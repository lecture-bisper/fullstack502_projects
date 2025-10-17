package bitc.full502.lostandfound.data.api

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface FcmService {

    @POST("fcm/token")
    fun saveFcmToken(
        @Header("Authorization") userToken: String,
        @Query("fcmToken") fcmToken: String,
        @Query("deviceId") deviceId: String
    ): Call<String>
}