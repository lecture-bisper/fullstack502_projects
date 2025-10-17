package bitc.full502.lostandfound.data.api

import bitc.full502.lostandfound.data.model.ChatData
import bitc.full502.lostandfound.data.model.ChatRoomData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface ChatService {

    // 전체 채팅방 조회
    @GET("chat/rooms")
    fun getAllRooms(
        @Header("Authorization") token: String
    ): Call<List<ChatRoomData>>

    // 단일 채팅방 조회
    @GET("chat/rooms/{boardIdx}")
    fun getChatRoom(
        @Header("Authorization") token: String,
        @Path("boardIdx") boardIdx: Long
    ): Call<ChatRoomData>

    // 유저1과 유저2의 채팅방 존재 여부 확인 후 없으면 생성
    @POST("chat/rooms")
    @FormUrlEncoded
    fun getOrCreateChatRoom(
        @Header("Authorization") token: String,
        @Field("otherUserId") otherUserId: String,
        @Field("boardIdx") boardIdx: Long
    ): Call<List<ChatData>>

    // 채팅 저장
    @POST("chat/rooms/{roomIdx}")
    fun insertChat(
        @Header("Authorization") token: String,
        @Path("roomIdx") roomIdx: Long,
        @Body chatData: ChatData,
    ): Call<String>
}