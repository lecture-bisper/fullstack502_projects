package bitc.full502.lostandfound.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.AuthService
import bitc.full502.lostandfound.data.api.BoardService
import bitc.full502.lostandfound.data.model.BoardData
import bitc.full502.lostandfound.data.model.ItemData
import bitc.full502.lostandfound.data.model.UserData
import bitc.full502.lostandfound.databinding.ActivityMyWriteListBinding
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.ItemAdapter
import bitc.full502.lostandfound.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyWriteListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMyWriteListBinding.inflate(layoutInflater) }

    // 게시글 API
    private val apiBoard by lazy {
        ApiClient.createJsonService(Constants.BASE_URL, BoardService::class.java)
    }

    // 인증 API (로그인 사용자 정보)
    private val apiAuth by lazy {
        ApiClient.createJsonService(Constants.BASE_URL, AuthService::class.java)
    }
    private val tokenManager: TokenManager by lazy { TokenManager(this) }

    private lateinit var adapter: ItemAdapter
    private val allData = mutableListOf<BoardData>()
    private val displayData = mutableListOf<ItemData>()
    private var currentPage = 0
    private val pageSize = 10
    private var currentUserId: String? = null
    private var isLoading = false

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
        binding.topBar.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(displayData) { item ->
            val intent = Intent(this, LostDetailActivity::class.java).apply {
                putExtra(EXTRA_BOARD_ID, item.boardId)
                putExtra(EXTRA_BOARD_TYPE, item.type)
            }
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

        // 로그인한 사용자 정보 가져오기 -> 그 후 게시글 필터링
        fetchCurrentUser()

        binding.recyclerView.addOnScrollListener(object :
            androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItem == totalItemCount - 1) {
                    loadNextPage()
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()
        // 돌아올 때 항상 새로고침(간단)
        currentUserId?.let { refreshBoards(it) } ?: fetchCurrentUser()
    }

    // 로그인 사용자 정보 가져오기
    private fun fetchCurrentUser() {
        val token = tokenManager.getToken() ?: run {
            Log.e("MyWriteListActivity", "토큰이 없습니다.")
            return
        }
        if (isLoading) return
        isLoading = true
        apiAuth.getUserInfo("Bearer $token").enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                isLoading = false
                if (response.isSuccessful) {
                    currentUserId = response.body()?.userId
                    currentUserId?.let { refreshBoards(it) }
                } else {
                    Log.e("MyWriteListActivity", "유저 정보 응답 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
                isLoading = false
                Log.e("MyWriteListActivity", "유저 정보 가져오기 실패", t)
            }
        })
    }
    private fun refreshBoards(uid: String) {
        if (isLoading) return
        isLoading = true
        apiBoard.getAllBoardList().enqueue(object : Callback<List<BoardData>> {
            override fun onResponse(call: Call<List<BoardData>>, response: Response<List<BoardData>>) {
                isLoading = false
                if (!response.isSuccessful) {
                    Log.e("MyWriteListActivity", "게시글 응답 실패: ${response.code()}")
                    return
                }
                val list = response.body().orEmpty()
                val myPosts = list.filter { it.userId == uid }

                // 페이지/어댑터 초기화 + 정합성 알림
                allData.clear()
                allData.addAll(myPosts)

                val prevSize = displayData.size
                displayData.clear()
                if (prevSize > 0) adapter.notifyItemRangeRemoved(0, prevSize)

                currentPage = 0
                loadNextPage() // 내부에서 notifyItemRangeInserted 호출
                Log.d("MyWriteListActivity", "내 글 수: ${allData.size}")
            }

            override fun onFailure(call: Call<List<BoardData>>, t: Throwable) {
                isLoading = false
                Log.e("MyWriteListActivity", "게시글 서버 호출 실패", t)
            }
        })
    }



    // 게시글 가져오기 + 로그인 사용자 글만 필터링
    private fun fetchBoardData(currentUserId: String) {
        apiBoard.getAllBoardList().enqueue(object : Callback<List<BoardData>> {
            override fun onResponse(
                call: Call<List<BoardData>>,
                response: Response<List<BoardData>>
            ) {
                if (response.isSuccessful) {
                    val boardList = response.body()
                    if (boardList != null) {
                        val myPosts = boardList.filter { it.userId == currentUserId }
                        allData.clear()
                        allData.addAll(myPosts)
                        displayData.clear()
                        currentPage = 0
                        loadNextPage()
                        Log.d("MyWriteListActivity", "내 글 수: ${allData.size}")
                    }
                } else {
                    Log.e("MyWriteListActivity", "게시글 응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<BoardData>>, t: Throwable) {
                Log.e("MyWriteListActivity", "게시글 서버 호출 실패", t)
            }
        })
    }

    private fun loadNextPage() {
        val startIndex = currentPage * pageSize
        val endIndex = minOf(startIndex + pageSize, allData.size)

        Log.d(
            "Pagination",
            "loadNextPage 호출 - currentPage: $currentPage, startIndex: $startIndex, endIndex: $endIndex, allData.size: ${allData.size}"
        )

        if (startIndex < allData.size) {
            val nextItems = allData.subList(startIndex, endIndex).map {
                ItemData(
                    it.idx,
                    it.title,
                    it.ownerName,
                    it.eventDate ?: "작성일 없음",
                    it.status,
                    it.type,
                    it.userId,
                    it.createDate
                )
            }
            displayData.addAll(nextItems)
            adapter.notifyItemRangeInserted(startIndex, nextItems.size)

            Log.d(
                "Pagination",
                "불러온 아이템 수: ${nextItems.size}, displayData.size: ${displayData.size}"
            )

            currentPage++
        } else {
            Log.d("Pagination", "더 이상 불러올 데이터 없음")
        }
    }
}
