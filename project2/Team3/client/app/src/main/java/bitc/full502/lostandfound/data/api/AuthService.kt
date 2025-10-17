package bitc.full502.lostandfound.data.api

import bitc.full502.lostandfound.data.model.JoinData
import bitc.full502.lostandfound.data.model.UserData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthService {

    @POST("auth/login")
    @FormUrlEncoded
    fun login(
        @Field("userId") userId: String,
        @Field("password") password: String,
        @Field("role") role: String,
        @Field("isAutoLogin") isAutoLogin: Boolean
    ): Call<String>

    @POST("auth/validate")
    fun validateToken(
        @Header("Authorization") token: String
    ): Call<String>

    //    추가됨
    @GET("user/info")
    fun getUserInfo(
        @Header("Authorization") token: String
    ): Call<UserData>

    //    추가됨
    @POST("auth/register")
    fun registerUser(@Body user: JoinData): Call<Void>

    //    추가됨
    @GET("auth/check")
    fun checkDuplicate(
        @Query("checkType") checkType: String,
        @Query("userData") userData: String
    ): Call<ResponseBody>

    //    추가됨
    @PUT("auth/password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Query("oldPassword") oldPassword: String,
        @Query("newPassword") newPassword: String,
        @Query("newPasswordConfirm") newPasswordConfirm: String
    ): Call<ResponseBody>

    @POST("auth/logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<String>

    // 회원탈퇴
    @DELETE("auth/delete")
    fun deleteUser(
        @Header("Authorization") token: String
    ): Call<String>
}