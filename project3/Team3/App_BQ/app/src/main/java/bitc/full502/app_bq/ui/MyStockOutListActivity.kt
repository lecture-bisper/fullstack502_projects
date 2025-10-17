package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.StockLogDto
import bitc.full502.app_bq.data.model.StockLogSearchDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.databinding.ActivityMyStockOutListBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.app_bq.utill.MyStockOutAdapter
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyStockOutListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMyStockOutListBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService
    private lateinit var adapter: MyStockOutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        tokenManager = TokenManager(applicationContext)
        apiService = ApiClient.createJsonService(Constants.BASE_URL, ApiService::class.java)

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

        binding.itemSearchBtn.setOnClickListener {
            val keyword = binding.stockSearch.text.toString().trim()
            val startDate: String = binding.startDateEditText.text.toString()
            val endDate: String = binding.endDateEditText.text.toString()

            loadAllStockLogs(
                StockLogSearchDto(
                    keyword = keyword,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        }


        setupWindowInsets()
        updateNavMenu()
        setupRecyclerView()
        loadAllStockLogs(StockLogSearchDto())
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

    private fun setupRecyclerView() {
        adapter = MyStockOutAdapter(emptyList())
        binding.itemRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.itemRecyclerView.adapter = adapter
    }

    /**
     * 로그인한 사용자 empCode와 type == "OUT"인 데이터만 가져오기
     */
    private fun loadAllStockLogs(filter: StockLogSearchDto) {
        val token = tokenManager.getToken() ?: return

        apiService.getMyStockLogs("Bearer $token", filter)
            .enqueue(object : Callback<List<StockLogDto>> {
                override fun onResponse(
                    call: Call<List<StockLogDto>?>,
                    response: Response<List<StockLogDto>?>
                ) {
                    if (response.isSuccessful) {
                        val stockLogs = response.body().orEmpty()
                        adapter.updateList(stockLogs)
                        binding.itemRecyclerView.visibility =
                            if (stockLogs.isNotEmpty()) View.VISIBLE else View.GONE
                    } else {
                        binding.itemRecyclerView.visibility = View.GONE
                    }
                }

                override fun onFailure(
                    call: Call<List<StockLogDto>?>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    binding.itemRecyclerView.visibility = View.GONE
                }

            })
    }


}
