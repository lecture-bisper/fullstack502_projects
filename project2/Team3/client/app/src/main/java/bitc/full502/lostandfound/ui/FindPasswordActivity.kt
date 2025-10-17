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
import bitc.full502.lostandfound.databinding.ActivityFindPasswordBinding
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.util.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

class FindPasswordActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFindPasswordBinding.inflate(layoutInflater) }
    private val api by lazy { ApiClient.createJsonService(Constants.BASE_URL,AuthService::class.java) }
    private val tokenManager by lazy { TokenManager(this) }

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
        // 비밀번호 변경 버튼 클릭
        binding.changeBtn.setOnClickListener {
            val oldPw = binding.oldPw.text?.toString() ?: ""
            val newPw = binding.newPw.text?.toString() ?: ""
            val newPwConfirm = binding.newPwConfirm.text?.toString() ?: ""

            if (oldPw.isEmpty() || newPw.isEmpty() || newPwConfirm.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPw != newPwConfirm) {
                Toast.makeText(this, "새 비밀번호와 확인이 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 서버 호출: 토큰 기반 비밀번호 변경
            val myToken = tokenManager.getToken()
            val token = "Bearer ${myToken}" // 로그인 후 가져온 토큰
            api.changePassword(token, oldPw, newPw, newPwConfirm)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val result = response.body()?.string() // ResponseBody → String
                            if (result == "SUCCESS") {
                                Toast.makeText(this@FindPasswordActivity, "비밀번호 변경 성공!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@FindPasswordActivity, MyPageActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@FindPasswordActivity, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show()
                                Log.d("LAFDATA", "$oldPw, $newPw, $newPwConfirm")
                            }
                        } else {
                            Toast.makeText(this@FindPasswordActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@FindPasswordActivity, "통신 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.d("LAFDATA", "${t.message}")
                    }
                })

        }
    }
}
