package bitc.full502.app_bq.ui

import ItemAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiClient
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.ItemDto
import bitc.full502.app_bq.data.model.ItemSearchDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.databinding.ActivityItemListBinding
import bitc.full502.app_bq.utill.Constants
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast

class ItemListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityItemListBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        tokenManager = TokenManager(applicationContext)
        apiService = ApiClient.createJsonService(Constants.BASE_URL, ApiService::class.java)

        // WindowInsets 처리
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // Drawer 메뉴
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

        // RecyclerView
        setupRecyclerView()

        // 검색 버튼 클릭
        binding.itemSearchBtn.setOnClickListener {
            val keyword = binding.itemSearch.text.toString().trim()

            apiService.searchItems(
                name = keyword,
                manufacturer = null,
                code = null,
                categoryId = null,
                minPrice = null,
                maxPrice = null,
                status = "ACTIVE",
            ).enqueue(object : Callback<List<ItemDto>> {
                override fun onResponse(call: Call<List<ItemDto>>, response: Response<List<ItemDto>>) {
                    if (response.isSuccessful) {
                        val list = response.body().orEmpty()
                            .filter { it.status == "ACTIVE" || it.status == "INACTIVE" }
                        itemAdapter.updateList(list)
                    } else {
                        Log.d("ItemListActivity", "검색 실패: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<ItemDto>>, t: Throwable) {
                    Log.d("ItemListActivity", "검색 실패: ${t.message}")
                }
            })
        }


        // 처음 목록 불러오기
        fetchItemList()
        updateNavMenu()
    }


    private fun setupRecyclerView() {
        itemAdapter = ItemAdapter { item ->
            // 상세 페이지로 이동
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("itemId", item.code)
            startActivity(intent)
        }

        binding.itemRecyclerView.apply {
            adapter = itemAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ItemListActivity)
        }
    }

    private fun fetchItemList() {
        apiService.getAllItemList().enqueue(object : Callback<List<ItemDto>> {
            override fun onResponse(call: Call<List<ItemDto>>, response: Response<List<ItemDto>>) {
                if (response.isSuccessful) {
                    response.body().orEmpty().forEach {
                        Log.d("StatusCheck", "id=${it.id}, status='${it.status}'")
                    }
                    val list = response.body().orEmpty()
                        .filter { it.status == "ACTIVE"}
                    itemAdapter.updateList(list)
                } else {
                    Toast.makeText(this@ItemListActivity, "검색된 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("ItemListActivity", "아이템 조회 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<ItemDto>>, t: Throwable) {
                Log.d("ItemListActivity", "서버 오류: ${t.message}")
            }
        })
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
            apiService.getMyInfo("Bearer $token").enqueue(object : retrofit2.Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: retrofit2.Response<UserDto>) {
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
}
