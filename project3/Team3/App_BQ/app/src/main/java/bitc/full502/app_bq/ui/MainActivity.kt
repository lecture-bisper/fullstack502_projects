package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.databinding.ActivityMainBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService

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

        // 버튼 클릭 이벤트 연결
        setupButtonClickListeners()


    }

    override fun onRestart() {
        super.onRestart()
        updateNavMenu()
    }

    private fun updateNavMenu() {
        val token = tokenManager.getToken()
        val navMenu = binding.navView.menu

        if (token.isNullOrEmpty()) {

            navMenu.setGroupVisible(R.id.group_logged_out, true)
            navMenu.setGroupVisible(R.id.group_logged_in, false)
            navMenu.findItem(R.id.nav_user_id)?.title = "userId"
            setupButtonsBasedOnRole(null)

            //            토큰 없을 경우 메인 화면 진입 불가능하게 설정
            val intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
            finish()   // ★ MainActivity 제거 (뒤로가기 시 재진입 차단)
            return
        } else {
            navMenu.setGroupVisible(R.id.group_logged_out, false)
            navMenu.setGroupVisible(R.id.group_logged_in, true)

            apiService.getMyInfo("Bearer $token").enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            navMenu.findItem(R.id.nav_user_id)?.title = "${user.empName}님"
                            setupButtonsBasedOnRole(user.roleId)
                        } else {
                            clearTokenAndResetMenu(navMenu)
                        }
                    } else {
                        clearTokenAndResetMenu(navMenu)
                    }
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    t.printStackTrace()
                    setupButtonsBasedOnRole(null)
                }
            })
        }
    }

    private fun clearTokenAndResetMenu(navMenu: android.view.Menu) {
        tokenManager.clearToken()
        navMenu.setGroupVisible(R.id.group_logged_out, true)
        navMenu.setGroupVisible(R.id.group_logged_in, false)
        navMenu.findItem(R.id.nav_user_id)?.title = "userId"
        setupButtonsBasedOnRole(null)
    }

    private fun setupButtonClickListeners() {
        binding.btnWarningList.setOnClickListener { navigateOrLogin { startActivity(Intent(this, MinStockListActivity::class.java)) } }
        binding.btnItemIn.setOnClickListener { navigateOrLogin { startActivity(Intent(this, MainActivity::class.java)) } }
        binding.btnItemOut.setOnClickListener { navigateOrLogin { startActivity(Intent(this, MainActivity::class.java)) } }
        binding.btnItemList.setOnClickListener { navigateOrLogin { startActivity(Intent(this, ItemListActivity::class.java)) } }
        binding.btnMyList.setOnClickListener { navigateOrLogin { startActivity(Intent(this, MyStockOutListActivity::class.java)) } }
        binding.btnItemIn.setOnClickListener { navigateOrLogin { startActivity(StockInOutActivity.newIntent(this, StockMode.IN)) } }
        binding.btnItemOut.setOnClickListener { navigateOrLogin { startActivity(StockInOutActivity.newIntent(this, StockMode.OUT)) } }
    }

    private fun navigateOrLogin(action: () -> Unit) {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            action()
        }
    }

    private fun setupButtonsBasedOnRole(roleId: Long?) {
        binding.btnWarningList.visibility = View.GONE
        binding.btnItemIn.visibility = View.GONE
        binding.btnItemOut.visibility = View.VISIBLE
        binding.btnItemList.visibility = View.VISIBLE
        binding.btnMyList.visibility = View.VISIBLE

        roleId?.let {
            when (it) {
                1L -> {

                }
                2L, 3L -> {
//                    초기상태를 보수적으로 잡고, 권한이 있는 경우에만 보이도록 수정함
                    binding.btnWarningList.visibility = View.VISIBLE
                    binding.btnItemIn.visibility = View.VISIBLE
                }
            }
        }
    }
}
