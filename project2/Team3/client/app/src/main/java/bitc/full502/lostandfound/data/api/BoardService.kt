package bitc.full502.lostandfound.data.api

import bitc.full502.lostandfound.data.model.BoardData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BoardService {
    @GET("board/list")
    fun getAllBoardList(): Call<List<BoardData>>

    //    controller 부분이 멀티파트라서 꼭 파트로 보내야함
    @Multipart
    @POST("board/insert")
    fun insertBoard(
        @Header("Authorization") token: String,
        @Part("dto") dtoJson: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<BoardData>

    @GET("board/{id}")
    fun getBoardDetail(
        @Path("id") id: Long
    ): Call<BoardData>


    @Multipart
    @PUT("board/update")
    fun updateBoard(
        @Part("dto") dto: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<BoardData>

    @PUT("board/{id}/complete")
    fun completeBoard(
        @Path("id") id: Long,
    ):Call<Unit>

    @GET("board/search")
    fun searchBoard (
        @Query("keyword") keyword: String?,
        @Query("categoryId") categoryId: Int?,
        @Query("type") type: String?,
        @Query("fromDate") fromDate: String?,
        @Query("toDate") toDate: String?
    ): Call<List<BoardData>>

}
