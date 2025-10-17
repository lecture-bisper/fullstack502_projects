package bitc.full502.lostandfound.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import bitc.full502.lostandfound.R
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.AuthService
import bitc.full502.lostandfound.data.api.BoardService
import bitc.full502.lostandfound.data.model.BoardData
import bitc.full502.lostandfound.data.model.ItemData
import bitc.full502.lostandfound.data.model.UserData
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.ItemAdapter
import bitc.full502.lostandfound.databinding.ActivitySearchBinding
import bitc.full502.lostandfound.storage.TokenManager
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {

    private val jsonApi by lazy { ApiClient.createJsonService(Constants.BASE_URL, AuthService::class.java) }
    private val tokenManager: TokenManager by lazy { TokenManager(this) }
    private val scalarApi by lazy { ApiClient.createScalarService(Constants.BASE_URL, AuthService::class.java) }
    private val api by lazy { ApiClient.createJsonService(Constants.BASE_URL, BoardService::class.java) }

    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }

    private lateinit var adapter: ItemAdapter
    private val allData = mutableListOf<BoardData>()
    private val displayData = mutableListOf<ItemData>()
    private var currentPage = 0
    private val pageSize = 10

    private var selectedKeyword: String? = null
    private var selectedCategoryId: Int? = null
    private var selectedType: String? = null
    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null

    companion object {
        const val EXTRA_BOARD_ID = "extra_board_id"
        const val EXTRA_BOARD_TYPE = "extra_board_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 네비게이션 메뉴 초기화
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_login -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_mypage -> startActivity(Intent(this, MyPageActivity::class.java))
                R.id.nav_mywrite -> startActivity(Intent(this, MyWriteListActivity::class.java))
                R.id.nav_chatting -> startActivity(Intent(this, ChatRoomActivity::class.java))
                R.id.nav_logout -> logoutUser()
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        setupRecyclerView()
        setupTypeButtons()
        setupCategoryButtons()
        setupListeners()
        fetchBoardData()
        refreshDrawerMenu()
    }

    override fun onResume() {
        super.onResume()
        // 서버 호출 후 전체 새로고침
        fetchBoardData()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(displayData) { item ->
            startActivity(Intent(this, LostDetailActivity::class.java).apply {
                putExtra(EXTRA_BOARD_ID, item.boardId)
                putExtra(EXTRA_BOARD_TYPE, item.type)
            })
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItem == totalItemCount - 1) loadNextPage()
            }
        })

        binding.btnSelectDateRange.setOnClickListener { showDateRangePicker() }
        binding.topBar.btnBack.setOnClickListener { finish() }
        binding.menuBtn.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }

        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                selectedKeyword = binding.searchBar.text.toString()
                searchBoardData()
                true
            } else false
        }
        binding.searchBar.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                selectedKeyword = binding.searchBar.text.toString()
                searchBoardData()
                true
            } else false
        }
        binding.tilAddress.setEndIconOnClickListener {
            selectedKeyword = binding.searchBar.text.toString()
            searchBoardData()
        }

        binding.btnReset.setOnClickListener {
            selectedKeyword = null
            selectedCategoryId = null
            selectedType = null
            selectedStartDate = null
            selectedEndDate = null

            binding.searchBar.text?.clear()
            binding.tvSelectedDate.text = "선택된 날짜 없음"

            val categoryButtons = listOf(
                binding.btnCategory0, binding.btnCategory1, binding.btnCategory2, binding.btnCategory3,
                binding.btnCategory4, binding.btnCategory5, binding.btnCategory6, binding.btnCategory7
            )
            categoryButtons.forEach { it.isChecked = false }

            val typeButtons = listOf(binding.btnFound, binding.btnLost)
            typeButtons.forEach { it.isChecked = false }

            fetchBoardData()
        }
    }

    private fun fetchBoardData() {
        api.getAllBoardList().enqueue(object : Callback<List<BoardData>> {
            override fun onResponse(call: Call<List<BoardData>>, response: Response<List<BoardData>>) {
                if (response.isSuccessful) {
                    response.body()?.let { boardList ->
                        allData.clear()
                        allData.addAll(boardList)

                        displayData.clear()
                        displayData.addAll(allData.map {
                            ItemData(it.idx, it.title, it.ownerName, it.eventDate, it.status, it.type, it.userId, it.createDate)
                        })
                        adapter.notifyDataSetChanged() // 전체 새로고침
                        currentPage = 1
                    }
                }
            }

            override fun onFailure(call: Call<List<BoardData>>, t: Throwable) {
                Log.e("SearchActivity", "서버 호출 실패", t)
            }
        })
    }

    private fun loadNextPage() {
        val startIndex = currentPage * pageSize
        val endIndex = minOf(startIndex + pageSize, allData.size)
        if (startIndex < allData.size) {
            val nextItems = allData.subList(startIndex, endIndex).map {
                ItemData(it.idx, it.title, it.ownerName, it.eventDate, it.status, it.type, it.userId, it.createDate)
            }
            displayData.addAll(nextItems)
            adapter.notifyItemRangeInserted(displayData.size - nextItems.size, nextItems.size)
            currentPage++
        }
    }

    private fun setupCategoryButtons() {
        val categoryButtons = listOf(
            binding.btnCategory0, binding.btnCategory1, binding.btnCategory2, binding.btnCategory3,
            binding.btnCategory4, binding.btnCategory5, binding.btnCategory6, binding.btnCategory7
        )
        categoryButtons.forEach { btn ->
            btn.setOnClickListener {
                val clickedId = btn.tag.toString().toIntOrNull()
                if (selectedCategoryId == clickedId) {
                    btn.isChecked = false
                    selectedCategoryId = null
                } else {
                    categoryButtons.forEach { it.isChecked = false }
                    btn.isChecked = true
                    selectedCategoryId = clickedId
                }
                searchBoardData()
            }
        }
    }

    private fun setupTypeButtons() {
        val typeButtons = listOf(binding.btnFound, binding.btnLost)
        typeButtons.forEach { btn ->
            btn.setOnClickListener {
                val clickedType = btn.tag.toString()
                if (selectedType == clickedType) {
                    btn.isChecked = false
                    selectedType = null
                } else {
                    typeButtons.forEach { it.isChecked = false }
                    btn.isChecked = true
                    selectedType = clickedType
                }
                searchBoardData()
            }
        }
    }

    private fun showDateRangePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("날짜 범위 선택")
            .build()
        picker.show(supportFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            selectedStartDate = sdf.format(Date(selection.first))
            selectedEndDate = sdf.format(Date(selection.second))
            binding.tvSelectedDate.text = "$selectedStartDate ~ $selectedEndDate"
            searchBoardData()
        }
    }

    private fun searchBoardData() {
        api.searchBoard(
            keyword = selectedKeyword,
            categoryId = selectedCategoryId,
            type = selectedType,
            fromDate = selectedStartDate,
            toDate = selectedEndDate
        ).enqueue(object : Callback<List<BoardData>> {
            override fun onResponse(call: Call<List<BoardData>>, response: Response<List<BoardData>>) {
                if (response.isSuccessful) {
                    response.body()?.let { boardList ->
                        allData.clear()
                        allData.addAll(boardList)

                        displayData.clear()
                        displayData.addAll(allData.map {
                            ItemData(it.idx, it.title, it.ownerName, it.eventDate, it.status, it.type, it.userId, it.createDate)
                        })
                        adapter.notifyDataSetChanged()
                        currentPage = 1
                    }
                }
            }

            override fun onFailure(call: Call<List<BoardData>>, t: Throwable) {
                Log.e("SearchActivity", "검색 실패", t)
            }
        })
    }

    private fun refreshDrawerMenu() {
        val token = tokenManager.getToken()
        val isLoggedIn = !token.isNullOrEmpty()
        val menu = binding.navView.menu
        menu.setGroupVisible(R.id.group_logged_out, !isLoggedIn)
        menu.setGroupVisible(R.id.group_logged_in, isLoggedIn)

        token?.let {
            jsonApi.getUserInfo("Bearer $it").enqueue(object : Callback<UserData> {
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    response.body()?.let { user ->
                        menu.findItem(R.id.nav_user_id).title = user.userName
                    }
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Toast.makeText(this@SearchActivity, "유저 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun logoutUser() {
        val token = tokenManager.getToken() ?: return
        scalarApi.logout("Bearer $token").enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() == "SUCCESS") {
                    tokenManager.clearToken()
                    refreshDrawerMenu()
                    Toast.makeText(this@SearchActivity, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SearchActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                } else {
                    Log.e("SearchActivity", "로그아웃 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("SearchActivity", "로그아웃 통신 실패: ${t.message}")
            }
        })
    }
}
