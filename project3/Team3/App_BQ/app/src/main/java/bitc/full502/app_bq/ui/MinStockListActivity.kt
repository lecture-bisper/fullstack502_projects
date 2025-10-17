package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.ItemDto
import bitc.full502.app_bq.data.model.MinStockDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.databinding.ActivityMinStockListBinding
import bitc.full502.app_bq.databinding.ActivityMyStockOutListBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.filter

class MinStockListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMinStockListBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService
    private lateinit var adapter: MinStockAdapter
    private var allMinStocks: List<MinStockDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        tokenManager = TokenManager(applicationContext)
        apiService = ApiClient.createJsonService(Constants.BASE_URL, ApiService::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // RecyclerView 세팅
        adapter = MinStockAdapter(this)
        binding.itemRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.itemRecyclerView.adapter = adapter

        binding.menuBtn.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_login -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_mypage -> startActivity(Intent(this, MyPageActivity::class.java))
                R.id.nav_my_list -> startActivity(Intent(this, MyStockOutListActivity::class.java))
                R.id.nav_logout -> logoutUser()
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        updateNavMenu()

        // 서버에서 "low" 재고 조회
        loadLowMinStocks()


        // 검색 버튼 클릭
        binding.itemSearchBtn.setOnClickListener {
            val keyword = binding.stockSearch.text.toString().trim()
            filterStocks(keyword)
        }
    }

    private fun loadLowMinStocks() {
        val token = tokenManager.getToken() ?: return
        apiService.getMinStockByStatus("Bearer $token", "low")
            .enqueue(object : Callback<List<MinStockDto>> {
                override fun onResponse(
                    call: Call<List<MinStockDto>>,
                    response: Response<List<MinStockDto>>
                ) {
                    if (response.isSuccessful) {
                        allMinStocks = response.body() ?: emptyList()
                        adapter.updateList(allMinStocks)
                    } else {
                        Toast.makeText(this@MinStockListActivity, "재고 조회 실패", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<List<MinStockDto>>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(this@MinStockListActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filterStocks(keyword: String) {
        val filtered = if (keyword.isEmpty()) {
            allMinStocks
        } else {
            allMinStocks.filter {
                it.itemName.contains(
                    keyword,
                    ignoreCase = true
                ) || it.itemCode.contains(keyword, ignoreCase = true)
            }
        }
        adapter.updateList(filtered)
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
                    }
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) = t.printStackTrace()
            })
        }
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
