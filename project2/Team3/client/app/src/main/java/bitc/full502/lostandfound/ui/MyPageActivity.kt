package bitc.full502.lostandfound.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.AuthService
import bitc.full502.lostandfound.data.model.UserData
import bitc.full502.lostandfound.databinding.ActivityFindPasswordBinding
import bitc.full502.lostandfound.databinding.ActivityMyPageBinding
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMyPageBinding.inflate(layoutInflater) }
    private val tokenManager: TokenManager by lazy { TokenManager(this) }
    private val api by lazy { ApiClient.createJsonService(Constants.BASE_URL, AuthService::class.java) }
    private val apiScalar by lazy { ApiClient.createScalarService(Constants.BASE_URL, AuthService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topBar.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        fetchUserInfo()

//        암호변경 버튼
        binding.changePwBtn.setOnClickListener {
            val intent = Intent(this@MyPageActivity, MyPageActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼
        binding.logoutBtn.setOnClickListener { logoutUser() }

        // 회원탈퇴 버튼
        binding.deleteUserBtn.setOnClickListener { showDeleteConfirmationDialog() }
        binding.changePwBtn.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchUserInfo() {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            Log.e("MyPageActivity", "토큰이 비어있음")
            return
        }

        api.getUserInfo("Bearer $token").enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {
                    response.body()?.let { updateUI(it) }
                } else Log.e("MyPageActivity", "응답 실패 코드: ${response.code()}")
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.e("MyPageActivity", "통신 실패: ${t.message}")
            }
        })
    }

    private fun updateUI(user: UserData) {
        binding.loginUserId.setText(user.userId)
        binding.loginUserPw.setText("********")
        binding.loginUserEmail.setText(user.email)
        binding.loginUserName.setText(user.userName)
        binding.loginUserPh.setText(user.phone)

        val roleButtons = listOf(binding.btnUser1, binding.btnUser2)
        roleButtons.forEach { button ->
            button.visibility = if (button.tag.toString() == user.role) {
                android.view.View.VISIBLE
            } else android.view.View.GONE
        }
    }

    private fun logoutUser() {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) return

        apiScalar.logout("Bearer $token").enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() == "SUCCESS") {
                    tokenManager.clearToken()
                    val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else Log.e("MyPageActivity", "로그아웃 실패: ${response.code()}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("MyPageActivity", "로그아웃 통신 실패: ${t.message}")
            }
        })
    }

    /** 회원탈퇴 확인 다이얼로그 */
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("회원탈퇴")
            .setMessage("정말 회원탈퇴 하시겠습니까? 이 작업은 되돌릴 수 없습니다.")
            .setPositiveButton("탈퇴") { _, _ -> deleteUser() }
            .setNegativeButton("취소", null)
            .show()
    }

    /** 회원탈퇴 API 호출 */
    private fun deleteUser() {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) return

        apiScalar.deleteUser("Bearer $token").enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() == "SUCCESS") {
                    tokenManager.clearToken()
                    val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Log.e("MyPageActivity", "회원탈퇴 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("MyPageActivity", "회원탈퇴 통신 실패: ${t.message}")
            }
        })
    }
}
