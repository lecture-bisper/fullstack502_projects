package bitc.full502.app_bq.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.app_bq.R
import bitc.full502.app_bq.databinding.ActivityJoinBinding

class JoinActivity : AppCompatActivity() {

    private val binding by lazy{ ActivityJoinBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.menuBtn.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_login -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_mypage -> startActivity(Intent(this, MyPageActivity::class.java))
                R.id.nav_my_list -> startActivity(Intent(this, MyStockOutListActivity::class.java))
//                R.id.nav_logout -> logoutUser()
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // 회원가입 버튼 클릭
        binding.joinBtn.setOnClickListener {
            val employeeId = binding.inputId.text.toString().trim()
            val password = binding.inputPw.text.toString().trim()
            val passwordRe = binding.inputPwRe.text.toString().trim()

            // 입력값 체크
            if (employeeId.isEmpty() || password.isEmpty() || passwordRe.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호 확인
            if (password != passwordRe) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 서버 연동 또는 DB 저장 처리
            Toast.makeText(this, "회원가입 완료! 사원번호: $employeeId", Toast.LENGTH_SHORT).show()
        }
    }
}