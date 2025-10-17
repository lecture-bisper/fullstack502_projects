package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.ResponseDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.data.model.UserInfoDto
import bitc.full502.app_bq.databinding.ActivityMyPageBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class MyPageActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMyPageBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // TokenManager 초기화
        tokenManager = TokenManager(applicationContext)

        // Retrofit 초기화
        apiService = ApiClient.createJsonService(Constants.BASE_URL, ApiService::class.java)


        // WindowInsets 처리
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

        // 이메일 변경
        binding.userEmailReset.setOnClickListener {
            toggleEditMode(binding.userEmail, binding.userEmailReset) { newEmail ->
                val currentPhone = binding.userPh.text.toString() // 기존 전화번호 그대로
                updateMyInfo(email = newEmail, phone = currentPhone, binding.userEmail, binding.userEmailReset)
            }
        }

        // 전화번호 변경
        binding.userPhReset.setOnClickListener {
            toggleEditMode(binding.userPh, binding.userPhReset) { newPhone ->
                val currentEmail = binding.userEmail.text.toString() // 기존 이메일 그대로
                updateMyInfo(email = currentEmail, phone = newPhone, binding.userPh, binding.userPhReset)
            }
        }

        binding.userPwReset.setOnClickListener {startActivity(Intent(this, ChangePasswordActivity::class.java))}



        // 메뉴 초기 업데이트
        updateNavMenu()

        // 사용자 정보 EditText 세팅
        setUserInfoToEditText()
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

    private fun setUserInfoToEditText() {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            apiService.getMyInfo("Bearer $token").enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            binding.userId.setText(user.empCode)
                            binding.userPw.setText("*******") // 비밀번호는 나중
                            binding.userName.setText(user.empName)
                            binding.userDepartment.setText(user.deptName ?: "")
                            binding.userEmail.setText(user.empEmail ?: "")
                            binding.userPh.setText(user.empPhone ?: "")
                            binding.userBday.setText(user.empBirthDate ?: "")
                            binding.userJoinDate.setText(user.empHireDate ?: "")
                        }
                    } else {
                        logoutUser()
                    }
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    t.printStackTrace()
                    logoutUser()
                }
            })
        }
    }
//    변경 버튼
// MyPageActivity.kt – 수정 부분
private fun toggleEditMode(editText: EditText, buttonLayout: LinearLayout, onSave: (String) -> Unit) {
    val buttonTextView = buttonLayout.getChildAt(0) as TextView
    if (!editText.isEnabled) {
        // 활성화 모드
        editText.isEnabled = true
        editText.requestFocus()
        buttonTextView.text = "저장"
    } else {
        // 저장 모드
        val input = editText.text.toString().trim()
        if (input.isNotEmpty()) {
            editText.isEnabled = false
            editText.requestFocus()
            buttonTextView.text = "변경"
            onSave(input) // 서버 전송
        } else {
            Toast.makeText(this, "값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
    }
}

    private fun updateMyInfo(email: String, phone: String, editText: EditText, buttonLayout: LinearLayout) {
        val token = tokenManager.getToken() ?: return
        val body = UserInfoDto(email = email, phone = phone)

        apiService.updateMyInfo("Bearer $token", body)
            .enqueue(object : Callback<ResponseDto> {
                override fun onResponse(call: Call<ResponseDto>, response: Response<ResponseDto>) {
                    if (response.isSuccessful) {
                        // 성공 시 EditText 업데이트
                        binding.userEmail.setText(email)
                        binding.userPh.setText(phone)
                        Toast.makeText(this@MyPageActivity, "정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        // 실패 시 다시 저장 모드
                        editText.isEnabled = true
                        val buttonTextView = buttonLayout.getChildAt(0) as TextView
                        buttonTextView.text = "저장"
                        Toast.makeText(this@MyPageActivity, "변경 실패: ${response.errorBody()}", Toast.LENGTH_SHORT).show()
                        Log.d("MyPageActivity","변경 실패: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ResponseDto>, t: Throwable) {
                    editText.isEnabled = true
                    val buttonTextView = buttonLayout.getChildAt(0) as TextView
                    buttonTextView.text = "저장"
                    t.printStackTrace()
                    Toast.makeText(this@MyPageActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
