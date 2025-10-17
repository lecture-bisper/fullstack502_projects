package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.ResponseDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.data.model.UserPwdDto
import bitc.full502.app_bq.databinding.ActivityChangePasswordBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

class ChangePasswordActivity : AppCompatActivity() {

    private val binding by lazy{ ActivityChangePasswordBinding.inflate(layoutInflater) }

    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tokenManager = TokenManager(applicationContext)

        apiService = ApiClient.createJsonService(Constants.BASE_URL, ApiService::class.java)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.changePwBtn.setOnClickListener {
            val curPw = binding.inputOldPw.text.toString().trim()
            val newPw = binding.inputNewPw.text.toString().trim()
            val newPwRe = binding.inputNewPwRe.text.toString().trim()

            if (curPw.isEmpty() || newPw.isEmpty() || newPwRe.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPw != newPwRe) {
                Toast.makeText(this, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updatePassword(curPw, newPw)
        }

        // Drawer 버튼 클릭
        binding.menuBtn.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }

        // Navigation 클릭 이벤트
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

    private fun updatePassword(curPassword: String, newPassword: String) {
        val token = tokenManager.getToken() ?: return
        val body = UserPwdDto(curPassword, newPassword)

        apiService.updateMyPassword("Bearer $token", body)
            .enqueue(object : Callback<ResponseDto> {
                override fun onResponse(call: Call<ResponseDto>, response: Response<ResponseDto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ChangePasswordActivity, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("ChangePassword", "비밀번호 변경 성공")
                        val intent = Intent(this@ChangePasswordActivity, MyPageActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "현재 비밀번호가 일치하지 않습니다."
                        Log.e("ChangePassword", "비밀번호 변경 실패: $errorMsg")
                    }
                }

                override fun onFailure(call: Call<ResponseDto>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ChangePassword", "네트워크 오류", t)
                }
            })
    }
}