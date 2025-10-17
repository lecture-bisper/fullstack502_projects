package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.AuthDto
import bitc.full502.app_bq.data.model.ResponseDto
import bitc.full502.app_bq.databinding.ActivityLoginBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.text.isNullOrEmpty

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        apiService = ApiClient.createJsonService(Constants.BASE_URL, ApiService::class.java)

        // UI edge insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginBtn.setOnClickListener {
            val empCode = binding.inputId.text.toString()
            val password = binding.inputPw.text.toString()
            val autoLogin = binding.chkAutologin.isChecked

            if (empCode.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "사원번호와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()

                Log.d("LoginActivity", "사원번호와 비밀번호를 입력해주세요.")
                return@setOnClickListener
            }

            login(empCode, password, autoLogin)
        }

        autoLoginIfTokenExists()
    }

    private fun login(empCode: String, password: String, autoLogin: Boolean) {
        val authDto = AuthDto(empCode, password)
        apiService.login(authDto, autoLogin).enqueue(object : Callback<ResponseDto> {
            override fun onResponse(
                call: Call<ResponseDto?>,
                response: Response<ResponseDto?>
            ) {
                if(response.isSuccessful) {
                    val token = response.body()?.data
                    if (!token.isNullOrEmpty()) {
                        tokenManager.saveToken(token)
                        tokenManager.saveEmpCode(empCode)
                        Log.d("LoginActivity", "로그인 성공 ${response.body()}")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    Log.d("LoginActivity","로그인성공 ${response.body()?.data}")
                }else{
                    Toast.makeText(this@LoginActivity, "사원번호 또는 비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("LoginActivity","로그인실패 ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(
                call: Call<ResponseDto?>,
                t: Throwable
            ) {
                Log.d("LoginActivity","서버오류 ${t.message}")
            }

        })
    }

    private fun autoLoginIfTokenExists() {
        val savedToken = tokenManager.getToken()
        val isAutoLogin = tokenManager.isAutoLogin()

        if (!savedToken.isNullOrEmpty() && isAutoLogin) {
            apiService.autoLogin("Bearer $savedToken")
                .enqueue(object : Callback<ResponseDto> {
                    override fun onResponse(
                        call: Call<ResponseDto?>,
                        response: Response<ResponseDto?>
                    ) {
                        if (response.isSuccessful) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            Log.d("LoginActivity", "자동 로그인 성공: ${response.body()}")
                            finish()
                        } else {
                            tokenManager.clearToken()
                            Log.d("LoginActivity", "자동 로그인 실패: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseDto?>, t: Throwable) {
                        tokenManager.clearToken()
                        Log.d("LoginActivity", "자동 로그인 서버 실패: ${t.message}")
                    }
                })
        }
    }

}
