package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.OrderRequestDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.databinding.ActivityOrderingBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class OrderingActivity : AppCompatActivity() {

    private val binding by lazy { ActivityOrderingBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        apiService = ApiClient.createJsonService(Constants.BASE_URL, ApiService::class.java)

        // intent에서 데이터 가져오기
        val itemId = intent.getLongExtra("itemId", 0)
        val itemName = intent.getStringExtra("itemName")
        val itemCode = intent.getStringExtra("itemCode")
        val itemManufacturer = intent.getStringExtra("itemManufacturer")
        val categoryKrName = intent.getStringExtra("categoryKrName")
        val orderQty = intent.getLongExtra("orderQty", 0)
        val unitPrice = intent.getLongExtra("itemPrice", 0)
        val priceFormat = NumberFormat.getNumberInstance(Locale.KOREA)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 메뉴 버튼
        binding.menuBtn.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }

        // 메뉴 초기 업데이트
        updateNavMenu()

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

        // 아이템 정보 표시
        binding.itemName.setText(itemName)
        binding.itemCode.setText(itemCode)
        binding.itemManufacturer.setText(itemManufacturer)
        binding.itemCategoryId.setText(categoryKrName)
        binding.itemCount.setText(orderQty.toString())
        binding.orderingUnitPrice.setText("${unitPrice}")
        binding.itemPrice.setText("${priceFormat.format(unitPrice * orderQty)} 원")

        // 수량 변경 시 가격 업데이트
        binding.itemCount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val qty = s?.toString()?.toLongOrNull() ?: 0L
                val total = unitPrice * qty
                binding.itemPrice.setText("${priceFormat.format(total)} 원")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 발주 요청 버튼
        binding.orderBtn.setOnClickListener {
            val qty = binding.itemCount.text.toString().toLongOrNull() ?: 0L
            if (qty <= 0) {
                Toast.makeText(this, "발주 수량을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 요청자 정보
            val requestUser = tokenManager.getUserId() ?: ""

            // DTO 생성
            val orderDto = OrderRequestDto(
                itemId = itemId,
                name = binding.itemName.text.toString(),
                code = binding.itemCode.text.toString(),
                manufacturer = binding.itemManufacturer.text.toString(),
                category = binding.itemCategoryId.text.toString(),
                requestQty = qty,
                price = unitPrice,
                requestUser = requestUser
            )

            // API 호출
            apiService.createOrder("Bearer $token", orderDto)
                .enqueue(object : Callback<OrderRequestDto> {
                    override fun onResponse(call: Call<OrderRequestDto>, response: Response<OrderRequestDto>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@OrderingActivity, "발주 요청이 완료되었습니다.", Toast.LENGTH_SHORT).show()


                            val intent = Intent(this@OrderingActivity, MinStockListActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("OrderingActivity", "발주 요청 실패: ${response.code()} ${response.errorBody()?.string()}")
                            Toast.makeText(this@OrderingActivity, "발주 요청 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<OrderRequestDto>, t: Throwable) {
                        Log.e("OrderingActivity", "서버 연결 실패", t)
                        Toast.makeText(this@OrderingActivity, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    // 메뉴 상태 업데이트
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
}
