package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.ItemDto
import bitc.full502.app_bq.data.model.StockDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.databinding.ActivityItemDetailBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.orEmpty

class ItemDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityItemDetailBinding.inflate(layoutInflater) }

    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        apiService = ApiClient.createJsonService(Constants.BASE_URL, ApiService::class.java)


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val itemCode = intent.getStringExtra("itemCode")
        if (itemCode != null) {
            fetchItemDetail(itemCode)
        } else {
            Log.d("ItemDetailActivity", "Item code가 전달되지 않았습니다.")
        }


        binding.joinBtn.setOnClickListener{ startActivity(StockInOutActivity.newIntent(this, StockMode.OUT)) }
        // 메뉴 버튼
        binding.menuBtn.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        // Navigation 클릭 이벤트
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_login -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_mypage -> startActivity(Intent(this, MyPageActivity::class.java))
                R.id.nav_my_list -> startActivity(Intent(this, MyStockOutListActivity::class.java))
                R.id.nav_logout -> {
                    tokenManager.clearToken()
                    binding.drawerLayout.closeDrawers()
                    binding.drawerLayout.post {
                        updateNavMenu()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // 메뉴 초기 업데이트
        updateNavMenu()



    }

    private fun updateNavMenu() {
        val token = tokenManager.getToken()
        val navMenu = binding.navView.menu

        if (token.isNullOrEmpty()) {
            navMenu.setGroupVisible(R.id.group_logged_out, true)
            navMenu.setGroupVisible(R.id.group_logged_in, false)
            navMenu.findItem(R.id.nav_user_id)?.title = "userId"
        } else {
            navMenu.setGroupVisible(R.id.group_logged_out, false)
            navMenu.setGroupVisible(R.id.group_logged_in, true)

            apiService.getMyInfo("Bearer $token").enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            navMenu.findItem(R.id.nav_user_id)?.title = "${user.empName}님"
                        }
                    } else {
                        tokenManager.clearToken()
                        navMenu.setGroupVisible(R.id.group_logged_out, true)
                        navMenu.setGroupVisible(R.id.group_logged_in, false)
                        navMenu.findItem(R.id.nav_user_id)?.title = "userId"
                    }
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    private fun fetchItemDetail(itemCode: String) {
        apiService.getAllItemList().enqueue(object : Callback<List<ItemDto>> {
            override fun onResponse(call: Call<List<ItemDto>>, response: Response<List<ItemDto>>) {
                if (response.isSuccessful) {
                    val itemList = response.body().orEmpty()
                    val selectedItem = itemList.find { it.code == itemCode }

                    if (selectedItem != null) {
                        updateUI(selectedItem)
                        fetchStockDetail(itemCode)
                    } else {
                        Log.d("ItemDetailActivity", "해당 코드의 아이템을 찾을 수 없습니다.")
                    }
                } else {
                    Log.d("ItemDetailActivity", "서버 오류: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<ItemDto>>, t: Throwable) {
                Log.d("ItemDetailActivity", "서버 연결 실패: ${t}")
            }
        })
    }

    private fun fetchStockDetail(itemCode: String) {
        apiService.stockByItemForApp(itemCode).enqueue(object : Callback<List<StockDto>> {
            override fun onResponse(
                call: Call<List<StockDto>>,
                response: Response<List<StockDto>>
            ) {
                if (response.isSuccessful) {
                    val stockList = response.body().orEmpty()

                    // 총 재고 수량
                    val totalQty = stockList.sumOf { it.quantity }
                    binding.itemTotal.setText("${totalQty}개")

                    // 창고별 재고 현황
                    stockList.forEach { stock ->
                        when (stock.warehouseName) {
                            "wh_a" -> {
                                binding.itemAWarehouse.setText(stock.quantity.toString())
                            }
                            "wh_b" -> {
                                binding.itemBWarehouse.setText(stock.quantity.toString())
                            }
                            else -> {
                                binding.itemCWarehouse.setText(stock.quantity.toString())
                            }
                        }
                    }
                } else {
                    Log.e("ItemDetailActivity", "재고 조회 실패")
                }
            }

            override fun onFailure(call: Call<List<StockDto>>, t: Throwable) {
                Log.e("ItemDetailActivity", "재고 서버 연결 실패: ${t.message}")
            }
        })
    }


    private fun updateUI(item: ItemDto) {
        binding.itemName.setText(item.name)
        binding.itemCode.setText(item.code)
        binding.itemManufacturer.setText(item.manufacturer)
        val formattedPrice = NumberFormat.getNumberInstance(Locale.KOREA).format(item.price)
        binding.itemPrice.setText("${formattedPrice} 원")

        val categoryName = when (item.categoryId) {
            1L -> "사무용품"
            2L -> "전자기기"
            3L -> "가구/사무환경"
            4L -> "소모품"
            5L -> "안전/보안"
            6L -> "커피/간식/편의용품"
            7L -> "기타"
            else -> "알수없음"
        }
        binding.itemCategoryId.setText(categoryName)

        // 가격, 수량, 창고 정보는 나중 DTO에서 받아오기 때문에 일단 생략
    }

    private fun logoutUser() {
        tokenManager.clearToken()
        binding.drawerLayout.closeDrawers()
        binding.drawerLayout.post {
            updateNavMenu()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}