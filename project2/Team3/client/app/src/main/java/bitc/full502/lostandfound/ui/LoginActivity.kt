package bitc.full502.lostandfound.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.AuthService
import bitc.full502.lostandfound.databinding.ActivityLoginBinding
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val tokenManager: TokenManager by lazy { TokenManager(this) }
    private val api by lazy { ApiClient.createScalarService(Constants.BASE_URL, AuthService::class.java) }
    private lateinit var selectedRole: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topBar.btnBack.setOnClickListener {
//            onBackPressedWhere()
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnJoin.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        selectedRole = Constants.ROLE_USER
        binding.btnUser1.isChecked = true

        val userButtons = listOf(
            binding.btnUser1,
            binding.btnUser2
        )

        val userIdEditText = binding.loginUserId
        val userPwEditText = binding.loginUserPw
        val loginButton = binding.loginBtn

        userButtons.forEach { button ->
            button.setOnClickListener {
                userButtons.forEach { it.isChecked = false }
                button.isChecked = true
                selectedRole = button.tag.toString()
            }
        }
        loginButton.setOnClickListener {
            val userId = userIdEditText.text?.toString().orEmpty()
            val userPw = userPwEditText.text?.toString().orEmpty()
            val isAutoLogin = true


            api.login(userId, userPw, selectedRole, isAutoLogin)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
//                    로그인 디버깅 코드
                        Log.d(
                            "LOGIN",
                            "isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}"
                        )
                        if (response.isSuccessful) {
                            val token = response.body()
                            if (token != Constants.FAILURE) {
                                tokenManager.saveToken(token!!)
                                Toast.makeText(this@LoginActivity, "로그인 되었습니다", Toast.LENGTH_SHORT).show()
                                (application as MyApplication).saveFcmToken(tokenManager.getToken()!!)
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra("isLoggedIn", true)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "아이디 또는 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "네트워크 응답 실패", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "네트워크 연결 실패", Toast.LENGTH_SHORT)
                            .show()
                    }
                })


        }
    }
//    fun onBackPressedWhere(){
//        val fm = supportFragmentManager
//        if (fm.backStackEntryCount > 0) {
//            fm.popBackStack()
//            return
//        }
//        if (!isTaskRoot) {
//            finish()
//            return
//        }
}
