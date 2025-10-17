package bitc.full502.lostandfound.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.AuthService
import bitc.full502.lostandfound.data.model.JoinData
import bitc.full502.lostandfound.databinding.ActivityJoinBinding
import bitc.full502.lostandfound.util.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinActivity : AppCompatActivity() {

    private val binding by lazy { ActivityJoinBinding.inflate(layoutInflater) }
    private val api by lazy { ApiClient.createJsonService(Constants.BASE_URL, AuthService::class.java) }
    private lateinit var selectedRole: String

    private var isIdChecked = false
    private var isEmailChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //    ===========================================탑바 이전화면으로 이동==================================================
        binding.topBar.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 기본 선택
        selectedRole = "USER"
        binding.btnUser1.isChecked = true

        val userButtons = listOf(binding.btnUser1, binding.btnUser2)

        userButtons.forEach { button ->
            button.setOnClickListener {
                userButtons.forEach { it.isChecked = false }
                button.isChecked = true
                selectedRole = button.tag.toString()
            }
        }

        // ID 입력값 변경 시 중복확인 초기화
        binding.loginUserId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isIdChecked = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 이메일 입력값 변경 시 중복확인 초기화
        binding.loginUserEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isEmailChecked = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // ID 중복확인
        binding.btnCheckuserId.setOnClickListener {
            val userId = binding.loginUserId.text?.toString() ?: return@setOnClickListener
            api.checkDuplicate("ID", userId).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val result = response.body()?.string()
                    if (result == "NOT_EXIST") {
                        Toast.makeText(this@JoinActivity, "중복되지 않은 아이디입니다.", Toast.LENGTH_SHORT).show()
                        isIdChecked = true
                    } else {
                        Toast.makeText(this@JoinActivity, "중복된 아이디입니다.", Toast.LENGTH_SHORT).show()
                        isIdChecked = false
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@JoinActivity, "서버와 통신 실패", Toast.LENGTH_SHORT).show()
                    isIdChecked = false
                }
            })
        }

        // 이메일 중복확인
        binding.btnCheckuserEmail.setOnClickListener {
            val email = binding.loginUserEmail.text?.toString() ?: return@setOnClickListener
            api.checkDuplicate("EMAIL", email).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val result = response.body()?.string()
                    if (result == "NOT_EXIST") {
                        Toast.makeText(this@JoinActivity, "중복되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show()
                        isEmailChecked = true
                    } else {
                        Toast.makeText(this@JoinActivity, "중복된 이메일입니다.", Toast.LENGTH_SHORT).show()
                        isEmailChecked = false
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@JoinActivity, "서버와 통신 실패", Toast.LENGTH_SHORT).show()
                    isEmailChecked = false
                }
            })
        }

        // 회원가입
        binding.incJoinBar.joinBtn.setOnClickListener {
            if (!isIdChecked) {
                Toast.makeText(this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isEmailChecked) {
                Toast.makeText(this, "이메일 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = binding.loginUserId.text?.toString() ?: ""
            val userPw = binding.loginUserPw.text?.toString() ?: ""
            val userEmail = binding.loginUserEmail.text?.toString() ?: ""
            val userName = binding.loginUserName.text?.toString() ?: ""
            val userPh = binding.loginUserPh.text?.toString() ?: ""

            if (userId.isBlank() || userPw.isBlank() || userEmail.isBlank() || userName.isBlank() || userPh.isBlank()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val joinDTO = JoinData(
                userId = userId,
                password = userPw,
                phone = userPh,
                email = userEmail,
                userName = userName,
                createDate = null,
                role = selectedRole,
                autoLogin = false
            )

            api.registerUser(joinDTO).enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@JoinActivity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@JoinActivity, LoginActivity::class.java))
                    } else {
                        Toast.makeText(this@JoinActivity, "회원가입 실패: 아이디 혹은 이메일 중복", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@JoinActivity, "통신 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
