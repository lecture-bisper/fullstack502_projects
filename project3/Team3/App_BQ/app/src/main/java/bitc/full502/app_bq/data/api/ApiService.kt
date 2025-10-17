package bitc.full502.app_bq.data.api

import bitc.full502.app_bq.data.model.AuthDto
import bitc.full502.app_bq.data.model.ItemDto
import bitc.full502.app_bq.data.model.ItemSearchDto
import bitc.full502.app_bq.data.model.MinStockDto
import bitc.full502.app_bq.data.model.OrderRequestDto
import bitc.full502.app_bq.data.model.ResponseDto
import bitc.full502.app_bq.data.model.StockDto
import bitc.full502.app_bq.data.model.StockLogDto
import bitc.full502.app_bq.data.model.StockLogSearchDto
import bitc.full502.app_bq.data.model.StockRequestDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.data.model.UserInfoDto
import bitc.full502.app_bq.data.model.UserPwdDto
import bitc.full502.app_bq.data.model.WarehouseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // 로그인
    @POST("auth/login")
    fun login(
        @Body authDto: AuthDto,
        @Query("autoLogin") autoLogin: Boolean = false
    ): Call<ResponseDto>

    // 자동로그인
    @POST("auth/auto-login")
    fun autoLogin(
        @Header("Authorization") token: String
    ): Call<ResponseDto>

    // 내 정보 불러오기
    @GET("users/me")
    fun getMyInfo(
        @Header("Authorization") token: String
    ): Call<UserDto>

    // 내 정보 수정 (이메일, 전화번호)
    @PUT("users/me")
    fun updateMyInfo(
        @Header("Authorization") token: String,
        @Body userInfo: UserInfoDto
    ): Call<ResponseDto>

    // 비밀번호 수정
    @PUT("users/me/pwd")
    fun updateMyPassword(
        @Header("Authorization") token: String,
        @Body body: UserPwdDto
    ): Call<ResponseDto>

    // 비품 리스트 정보 불러오기
    @GET("items/app")
    fun getAllItemList(): Call<List<ItemDto>>

    // 비품 리스트 검색
    @POST("items/search")
    fun searchItemList(@Body filter: ItemSearchDto): Call<List<ItemDto>>

    // 비품 검색 기능
    @GET("items")
    fun searchItems(
        @Query("name") name: String?,
        @Query("manufacturer") manufacturer: String?,
        @Query("code") code: String?,
        @Query("categoryId") categoryId: Long?,
        @Query("minPrice") minPrice: Long?,
        @Query("maxPrice") maxPrice: Long?,
        @Query("status") status: String?,
    ): Call<List<ItemDto>>

    @GET("items/{code}")
    fun getItemDetail(
        @Path("code") code: String
    ): Call<ItemDto>

    // 비품별 재고 조회
    @GET("stocks/app/{code}")
    fun stockByItemForApp(
        @Path("code") code: String
    ): Call<List<StockDto>>

    // 내 출고 로그 조회
    @POST("stock-logs/search/me")
    fun getMyStockLogs(
        @Header("Authorization") token: String,
        @Body filter: StockLogSearchDto
    ): Call<List<StockLogDto>>

    @POST("stock-logs/search")
    fun getAllStockLogs(
        @Header("Authorization") token: String,
        @Body filter: StockLogSearchDto
    ): Call<List<StockLogDto>>

    // 적정재고 상태 값 조회
    @GET("min-stocks/{status}")
    fun getMinStockByStatus(
        @Header("Authorization") authorization: String,
        @Path("status") status: String
    ): Call<List<MinStockDto>>

    // 발주 요청
    @POST("orders")
    fun createOrder(
        @Header("Authorization") token: String,
        @Body dto: OrderRequestDto
    ): Call<OrderRequestDto>

    @GET("warehouse")
    fun getAllWarehouse(): Call<List<WarehouseDto>>

    @POST("stock/in")
    fun stockIn(
        @Header("Authorization") authorizationHeader: String,
        @Body request: StockRequestDto
    ): Call<StockRequestDto>

    @POST("stock/out")
    fun stockOut(
        @Header("Authorization") authorizationHeader: String,
        @Body request: StockRequestDto
    ): Call<StockRequestDto>



}