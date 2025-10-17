package bitc.full502.lostandfound.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import bitc.full502.lostandfound.databinding.ActivityMainBinding
import bitc.full502.lostandfound.map.MapHelper
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.ui.SearchActivity.Companion.EXTRA_BOARD_ID
import bitc.full502.lostandfound.ui.SearchActivity.Companion.EXTRA_BOARD_TYPE
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.ItemAdapter
import bitc.full502.lostandfound.util.MyApplication
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val idleHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var idleRunnable: Runnable? = null
    private fun distanceMeters(a: LatLng, b: LatLng): Float {
        val r = FloatArray(1)
        android.location.Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, r)
        return r[0]
    }
    private var lastSortCenter: LatLng? = null
    private val sortThresholdMeters = 100f

    private lateinit var mapHelper: MapHelper
    private val tokenManager: TokenManager by lazy { TokenManager(this) }
    private val scalarApi by lazy { ApiClient.createScalarService(Constants.BASE_URL, AuthService::class.java) }
    private val jsonApi by lazy { ApiClient.createJsonService(Constants.BASE_URL, AuthService::class.java) }
    private val api by lazy { ApiClient.createJsonService(Constants.BASE_URL, BoardService::class.java) }

    private lateinit var adapter: ItemAdapter
    private val allData = mutableListOf<BoardData>()
    private val displayData = mutableListOf<ItemData>()
    private var currentPage = 0
    private val pageSize = 10
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var isBoxesVisible = false
    private lateinit var behavior: BottomSheetBehavior<*>

    private var naverMap: NaverMap? = null
    private val markerList = mutableListOf<Marker>()

    private var filterType: String? = null
    private var selectedFilter: String? = null
    private var selectedMarker: Marker? = null
    private var selectedBoard: BoardData? = null
    private val NORMAL_MARKER_W = 48
    private val NORMAL_MARKER_H = 68
    private val SELECTED_SCALE = 1.8f
    private val SELECTED_MARKER_W get() = (NORMAL_MARKER_W * SELECTED_SCALE).toInt()
    private val SELECTED_MARKER_H get() = (NORMAL_MARKER_H * SELECTED_SCALE).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupBottomSheet()
        setupFilterButtons()

        mapHelper = MapHelper(
            activity = this,
            fragmentManager = supportFragmentManager,
            mapContainerId = R.id.map_fragment,
            ncpApiKey = "wokyo79tgl"
        )

        mapHelper.init { map ->
            naverMap = map
            map.uiSettings.isCompassEnabled = true
            map.uiSettings.isScaleBarEnabled = true
            map.setOnMapClickListener { _, _ -> clearMarkerSelection() }
            updateMarkersByFilter()
            lastSortCenter = map.cameraPosition.target

            map.addOnCameraIdleListener {
                // 직전 예약 콜백 취소
                idleRunnable?.let { idleHandler.removeCallbacks(it) }

                // 새 작업 생성
                val r = Runnable {
                    val center = map.cameraPosition.target
                    val last = lastSortCenter
                    if (last == null || distanceMeters(center, last) > sortThresholdMeters) {
                        lastSortCenter = center
                        applyNearbyOrderFrom(center)
                    }
                }
                idleRunnable = r
                idleHandler.postDelayed(r, 250) // 250ms 디바운스
            }
        }

        // 자동 로그인
        if (!intent.getBooleanExtra("isLoggedIn", false)) {
            tokenManager.getToken()?.let {
                scalarApi.validateToken("Bearer $it").enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if (response.isSuccessful) {
                            if (response.body() == Constants.SUCCESS) {
                                Log.d("**fullstack502**", "자동 로그인 성공")
                                (application as MyApplication).saveFcmToken(it)
                            } else {
                                Log.d("**fullstack502**", "자동 로그인 실패: ${response.body()}")
                                tokenManager.clearToken()
                            }
                            refreshDrawerMenu()
                        }
                    }
                    override fun onFailure(call: Call<String?>, t: Throwable) {}
                })
            }
        } else refreshDrawerMenu()

        NaverMapSdk.getInstance(this).client = NaverMapSdk.NcpKeyClient("wokyo79tgl")

        binding.menuBtn.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.btnWrite.setOnClickListener { if (isBoxesVisible) hideBoxes() else showBoxes() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })


        fetchBoardData()

        binding.btnLostWrite.setOnClickListener { handleWriteButtonClick(LostWriteActivity::class.java) }
        binding.btnFoundWrite.setOnClickListener { handleWriteButtonClick(FoundWriteActivity::class.java) }

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

        binding.searchBar.setOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }
    }

    private fun handleWriteButtonClick(targetActivity: Class<*>) {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        if (isBoxesVisible) hideBoxes()
        startActivity(Intent(this, targetActivity))
    }

    private fun setupBottomSheet() {
        val root = findViewById<View>(R.id.main)
        val topControls = findViewById<View>(R.id.top_controls)

        root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val screenH = root.height
                val searchBottom = topControls.bottom
                val available = screenH - searchBottom
                val targetCover = (available * 0.90f).toInt()
                val expandedOffset = searchBottom + (available - targetCover)

                behavior = BottomSheetBehavior.from(binding.bottomSheet)
                behavior.isFitToContents = false
                behavior.expandedOffset = expandedOffset.coerceAtLeast(0)
                behavior.skipCollapsed = false
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })
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

        binding.recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItem == totalItemCount - 1) loadNextPage()
            }
        })
    }

    private fun fetchBoardData() {
        api.getAllBoardList().enqueue(object : Callback<List<BoardData>> {
            override fun onResponse(call: Call<List<BoardData>>, response: Response<List<BoardData>>) {
                if (response.isSuccessful) {
                    response.body()?.let { boardList ->
                        allData.clear()
                        allData.addAll(boardList)

                        val center = naverMap?.cameraPosition?.target
                        if (center != null) {
                            lastSortCenter = center
                            applyNearbyOrderFrom(center)
                        } else {
                            // 지도 준비 전이면 기존 로딩
                            displayData.clear()
                            currentPage = 0
                            loadNextPage()
                        }
//                        displayData.clear()
//                        currentPage = 0
//                        loadNextPage()
                        updateMarkersByFilter()
                    }
                }
            }
            override fun onFailure(call: Call<List<BoardData>>, t: Throwable) {}
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
            adapter.notifyItemRangeInserted(startIndex, nextItems.size)
            currentPage++
        }
    }

    private fun setupFilterButtons() {
        binding.btnLostFilter.setOnClickListener {
            selectedFilter = if (selectedFilter == "LOST") null else "LOST"
            filterType = selectedFilter
            updateFilterButtonAppearance()
            updateMarkersByFilter()
        }
        binding.btnFoundFilter.setOnClickListener {
            selectedFilter = if (selectedFilter == "FOUND") null else "FOUND"
            filterType = selectedFilter
            updateFilterButtonAppearance()
            updateMarkersByFilter()
        }
    }

    private fun updateFilterButtonAppearance() {
        binding.btnLostFilter.apply {
            isSelected = selectedFilter == "LOST"
            setTextColor(if (isSelected) getColor(R.color.white) else getColor(R.color.red))
            setBackgroundColor(if (isSelected) getColor(R.color.red) else getColor(android.R.color.transparent))
        }
        binding.btnFoundFilter.apply {
            isSelected = selectedFilter == "FOUND"
            setTextColor(if (isSelected) getColor(R.color.white) else getColor(R.color.blue))
            setBackgroundColor(if (isSelected) getColor(R.color.blue) else getColor(android.R.color.transparent))
        }
    }

    private fun updateMarkersByFilter() {
        var map = naverMap ?: return

        // 기존 마커 제거 + 선택 해제
        markerList.forEach { it.map = null }
        markerList.clear()
        clearMarkerSelection()

        allData.forEach { board ->
            val lat = board.eventLat
            val lng = board.eventLng

            // 필터 체크
            if (filterType == null || board.type == filterType) {
                val resId = when (board.type) {
                    "LOST"  -> com.naver.maps.map.R.drawable.navermap_default_marker_icon_red
                    "FOUND" -> com.naver.maps.map.R.drawable.navermap_default_marker_icon_blue
                    else    -> R.drawable.marker_found
                }

                val marker = Marker().apply {
                    position = LatLng(lat, lng)
                    captionText = board.title
                    icon = OverlayImage.fromResource(resId)
                    width = NORMAL_MARKER_W
                    height = NORMAL_MARKER_H
                    this.map = map

                    setOnClickListener {
                        // 같은 마커를 다시 탭 → 상세로 이동
                        if (selectedMarker === this) {
                            startActivity(Intent(this@MainActivity, LostDetailActivity::class.java).apply {
                                putExtra(EXTRA_BOARD_ID, board.idx)
                                putExtra(EXTRA_BOARD_TYPE, board.type)
                            })
                        } else {
                            // 첫 탭 → 선택 + 확대 + 카메라 이동
                            selectMarker(this, board)
                        }
                        true
                    }
                }
                markerList.add(marker)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (isBoxesVisible) {
            val boxRect = Rect()
            binding.boxContainer.getGlobalVisibleRect(boxRect)
            if (!boxRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) hideBoxes()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun showBoxes() {
        binding.boxContainer.alpha = 0f
        binding.boxContainer.visibility = View.VISIBLE
        binding.boxContainer.animate().alpha(1f).setDuration(200).start()
        isBoxesVisible = true
    }

    private fun hideBoxes() {
        binding.boxContainer.animate().alpha(0f).setDuration(200)
            .withEndAction { binding.boxContainer.visibility = View.GONE }.start()
        isBoxesVisible = false
    }

    private fun refreshDrawerMenu() {
        var isLoggedIn = false
        val token = tokenManager.getToken()?.let { isLoggedIn = true; it }
        val menu = binding.navView.menu
        menu.setGroupVisible(R.id.group_logged_out, !isLoggedIn)
        menu.setGroupVisible(R.id.group_logged_in, isLoggedIn)

        if (menu.findItem(R.id.nav_user_id).isVisible && token != null) {
            jsonApi.getUserInfo("Bearer $token").enqueue(object : Callback<UserData> {
                override fun onResponse(call: Call<UserData?>, response: Response<UserData?>) {
                    response.body()?.let { user ->
                        menu.findItem(R.id.nav_user_id).title = user.userName
                    }
                }
                override fun onFailure(call: Call<UserData?>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "유저 정보 불러오기 실패, 다시 로그인 해주세요", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun logoutUser() {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) return

        scalarApi.logout("Bearer $token").enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() == "SUCCESS") {
                    tokenManager.clearToken()
                    refreshDrawerMenu() // 메뉴 상태 갱신
                    Toast.makeText(this@MainActivity, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                    // 필요하면 로그인 화면 이동
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                } else {
                    Log.e("MainActivity", "로그아웃 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("MainActivity", "로그아웃 통신 실패: ${t.message}")
            }
        })
    }
    private fun selectMarker(marker: Marker, board: BoardData) {
        // 기존 선택 원복
        if (selectedMarker != null && selectedMarker !== marker) {
            resetMarkerSize(selectedMarker!!)
        }

        selectedMarker = marker
        selectedBoard = board

        // 크기 확대
        marker.width = SELECTED_MARKER_W
        marker.height = SELECTED_MARKER_H

        // 카메라 살짝 확대 + 이동 (원하면 16.0 고정 줌)
        val cameraUpdate = com.naver.maps.map.CameraUpdate
            .scrollAndZoomTo(marker.position, 16.0)
            .animate(CameraAnimation.Fly, 400L)
        naverMap?.moveCamera(cameraUpdate)
    }



    private fun resetMarkerSize(m: Marker) {
        m.width = NORMAL_MARKER_W
        m.height = NORMAL_MARKER_H
    }

    private fun clearMarkerSelection() {
        selectedMarker?.let { resetMarkerSize(it) }
        selectedMarker = null
        selectedBoard = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mapHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun applyNearbyOrderFrom(center: LatLng) {
        val sorted = allData.sortedBy { board ->
            val lat = board.eventLat ?: 0.0
            val lng = board.eventLng ?: 0.0
            distanceMeters(center, LatLng(lat, lng))
        }
        allData.clear()
        allData.addAll(sorted)
        val oldSize = displayData.size
        if (oldSize > 0) {
            displayData.clear()
            adapter.notifyItemRangeRemoved(0, oldSize)
        }
        currentPage = 0
        loadNextPage()
    }
}

