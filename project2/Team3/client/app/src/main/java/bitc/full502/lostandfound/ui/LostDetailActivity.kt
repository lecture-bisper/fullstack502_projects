package bitc.full502.lostandfound.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.lostandfound.R
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.AuthService
import bitc.full502.lostandfound.data.api.BoardService
import bitc.full502.lostandfound.data.api.ReverseGeoService
import bitc.full502.lostandfound.data.model.BoardData
import bitc.full502.lostandfound.data.model.ReverseGeoData
import bitc.full502.lostandfound.data.model.UserData
import bitc.full502.lostandfound.databinding.ActivityLostDetailBinding
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.ui.FoundWriteActivity
import bitc.full502.lostandfound.ui.LostWriteActivity
import bitc.full502.lostandfound.ui.SearchActivity
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.Constants.EXTRA_BOARD_DATA
import bitc.full502.lostandfound.util.Constants.EXTRA_BOARD_ID
import bitc.full502.lostandfound.util.Constants.EXTRA_MODE
import bitc.full502.lostandfound.util.Constants.MODE_EDIT
import bitc.full502.lostandfound.util.Formatter
import bitc.full502.lostandfound.util.GeocodingConstants
import bitc.full502.lostandfound.util.ReverseGeocoder
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LostDetailActivity : AppCompatActivity() {


    private var pendingLatLng: LatLng? = null
    private val reverseGeo by lazy {
        ApiClient.createJsonService(GeocodingConstants.REVERSE_GEOCODE_URL, ReverseGeoService::class.java)
    }
    private val binding by lazy { ActivityLostDetailBinding.inflate(layoutInflater) }
    private val api by lazy { ApiClient.createJsonService(Constants.BASE_URL, BoardService::class.java) }
    private var naverMap: NaverMap? = null
    private val detailMarker by lazy { Marker() }
    private val tokenManager: TokenManager by lazy { TokenManager(this) }
    private val auth by lazy { ApiClient.createJsonService(Constants.BASE_URL, AuthService::class.java) }
    private var meUserId: String? = null
    private var detailData: BoardData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // 시스템 인셋 적용
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.buttonsRow.visibility = View.GONE
        binding.buttonsRowLimit.visibility = View.GONE

        val fm = supportFragmentManager
        val mapFragment = (fm.findFragmentById(R.id.map_fragment) as? MapFragment)
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().replace(R.id.map_fragment, it).commitNow()
            }
        mapFragment.getMapAsync { map ->
            naverMap = map
//             맵 준비 직후, 대기 좌표가 있으면 즉시 마커 배치
            pendingLatLng?.let { placeMarker(it) }
        }
        // 뒤로가기
        binding.topBar.btnBack.setOnClickListener { finish() }

        val id = intent.getLongExtra(SearchActivity.EXTRA_BOARD_ID, -1)
        val type = (intent.getStringExtra(SearchActivity.EXTRA_BOARD_TYPE) ?: Constants.TYPE_LOST).uppercase()
        if (id <= 0) {
            Toast.makeText(this, "잘못된 게시글 ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 타입별 라벨/보관장소 표시
        applyTypeLabels(type)
        // 상세 조회
        fetchDetail(id, type)
        fetchCurrentUser(type)
    }

    override fun onResume() {
        super.onResume()

        // intent에서 boardId와 type 가져오기
        val id = intent.getLongExtra(SearchActivity.EXTRA_BOARD_ID, -1)
        val type = (intent.getStringExtra(SearchActivity.EXTRA_BOARD_TYPE) ?: Constants.TYPE_LOST).uppercase()
        if (id > 0) {
            fetchDetail(id, type)
            fetchCurrentUser(type)
        }
    }

    /** LOST/FOUND 타입에 맞춰 라벨과 보관장소 가시성 조정 */
    private fun applyTypeLabels(type: String) {
        val isLost = type == Constants.TYPE_LOST

        binding.lblItemName.setText(if (isLost) R.string.lost_item_name else R.string.found_item_name)
        binding.lblPerson.setText(R.string.lost_person) // 분실자는 양쪽 공통 라벨
        binding.lblDate.setText(if (isLost) R.string.lost_date else R.string.found_date)
        binding.lblAddr.setText(if (isLost) R.string.lost_addr else R.string.found_addr)

        // 보관장소: FOUND만 노출
        val storageVisibility = if (isLost) View.GONE else View.VISIBLE
        binding.lblStorage.visibility = storageVisibility
        binding.tilStorage.visibility = storageVisibility
    }

    /** 상세 API 호출 후 UI에 바인딩 */
    private fun fetchDetail(id: Long, type: String) {
        api.getBoardDetail(id).enqueue(object : Callback<BoardData> {
            override fun onResponse(call: Call<BoardData>, resp: Response<BoardData>) {
                if (!resp.isSuccessful) {
                    Toast.makeText(
                        this@LostDetailActivity,
                        "상세 조회 실패(${resp.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val b = resp.body() ?: run {
                    Toast.makeText(this@LostDetailActivity, "데이터 없음", Toast.LENGTH_SHORT).show()
                    return
                }
                detailData = b
                tryResolveBottomBar(type)
                // 텍스트 바인딩
                binding.tvItemName.setText(b.title.orEmpty())
                binding.tvLostPerson.setText(b.ownerName.orEmpty())
                binding.tvLostDate.setText(Formatter.formatKoreanDateTime(b.eventDate))
//                주소 일단 상세주소로 바인딩하고 ReverseGeocoding 호출 성공시 덮어씌우기
                binding.tvAddress.setText(b.eventDetail.orEmpty())
                // 보관장소(FOUND만 노출되도록 applyTypeLabels에서 제어됨)
                binding.tvStorage.setText(b.storageLocation.orEmpty())
                binding.tvContent.setText(b.description.orEmpty())
                // 이미지 로드 (상대경로면 BASE 합치기)
                val url = b.imgUrl?.takeIf { it.isNotBlank() }?.let {
                    if (it.startsWith("http")) it else Constants.IMAGE_BASE_URL + it
                }
                val glide = Glide.with(this@LostDetailActivity)
                if (url != null) {
                    glide.load(url)
                        .placeholder(R.drawable.img_placeholder)  // 로딩 중
                        .error(R.drawable.img_placeholder)              // 실패/없음
                        .into(binding.ivPhoto)
                } else {
                    glide.load(R.drawable.img_placeholder).into(binding.ivPhoto)
                }
                val lat = b.eventLat
                val lng = b.eventLng
                if (lat != null && lng != null) {
                    val pos = LatLng(lat, lng)
                    pendingLatLng = pos
                    if (naverMap != null) placeMarker(pos)

                    // 먼저 상세주소를 보여두고, 역지오코딩 성공 시 합쳐서 갱신
                    ReverseGeocoder.fetchAddress(reverseGeo, lat, lng) { base ->
                        val full = ReverseGeocoder.mergeBaseAndDetail(base, b.eventDetail)
                        binding.tvAddress.setText(full.ifEmpty { "" })
                    }
                }
            }

            override fun onFailure(call: Call<BoardData>, t: Throwable) {
                Toast.makeText(this@LostDetailActivity, "통신 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchCurrentUser(type: String) {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            meUserId = null           // 비로그인 취급
            tryResolveBottomBar(type)     // 상세가 이미 도착했다면 바로 채팅 버튼 노출
            return
        }

        auth.getUserInfo("Bearer $token").enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, resp: Response<UserData>) {
                if (resp.isSuccessful) {
                    meUserId = resp.body()?.userId    // ← UserData의 실제 필드명에 맞게 수정
                } else {
                    meUserId = null              // 조회 실패 → 비로그인 취급
                }
                tryResolveBottomBar(type)
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                meUserId = null
                tryResolveBottomBar(type)
            }
        })
    }

    private fun updateBottomBar(isOwner: Boolean, isLoggedIn: Boolean) {
        val b = detailData ?: return
        val status = b.status == "COMPLETE"

        if(status){
            binding.buttonsRow.visibility = View.GONE
            binding.buttonsRowLimit.visibility = View.GONE
            binding.buttonAfterComplete.visibility = View.VISIBLE
            return
        }
        binding.buttonAfterComplete.visibility = View.GONE
        if (isOwner) {
            // 본인 글: 수정/처리완료 노출
            binding.buttonsRow.visibility = View.VISIBLE
            binding.buttonsRowLimit.visibility = View.GONE
        } else {
            // 본인 글 아님 or 비로그인: 채팅하기 노출
            binding.buttonsRow.visibility = View.GONE
            binding.buttonsRowLimit.visibility = View.VISIBLE
        }
    }

    private fun tryResolveBottomBar(type: String) {

        val b = detailData ?: return
        val typeLost = type == Constants.TYPE_LOST
        val typeFound = type == Constants.TYPE_FOUND

        val isLoggedIn = meUserId != null
        val writerId: String? = b.userId          // ← nullable로 선언
        val isOwner = isLoggedIn && writerId != null && writerId == meUserId

        updateBottomBar(isOwner, isLoggedIn)

        if (isOwner) {
            if (typeLost) {
                binding.btnUpdate.setOnClickListener {
                    val intent = Intent(this, LostWriteActivity::class.java).apply {
                        putExtra(EXTRA_MODE, MODE_EDIT)
                        putExtra(EXTRA_BOARD_ID, detailData?.idx ?: -1L)
                        detailData?.let { putExtra(EXTRA_BOARD_DATA, it) }
                    }
                    startActivity(intent)
                }
            } else if (typeFound) {
                binding.btnUpdate.setOnClickListener {
                    val intent = Intent(this, FoundWriteActivity::class.java).apply {
                        putExtra(EXTRA_MODE, MODE_EDIT)
                        putExtra(EXTRA_BOARD_ID, detailData?.idx ?: -1L)
                        detailData?.let { putExtra(EXTRA_BOARD_DATA, it) }
                    }
                    startActivity(intent)
                }
            }
            binding.btnComplete.setOnClickListener {
                showCompleteComfirmationDialog()
            }
        } else {
            binding.btnChat.setOnClickListener {
                handleChattingButtonClick(ChatActivity::class.java)
            }
        }
    }

    fun showCompleteComfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("처리 완료")
            .setMessage("물품의 처리가 완료되었습니까?\n 완료시 더이상 게시글이 보이지 않습니다")
            .setPositiveButton("완료") { _, _ -> completeBoard() }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun placeMarker(pos: LatLng) {
        val map = naverMap ?: return
        // 중복 부착 방지
        detailMarker.map = null
        detailMarker.position = pos
        detailMarker.map = map
        map.moveCamera(
            CameraUpdate.toCameraPosition(CameraPosition(pos, 16.0))
        )
    }

    fun completeBoard() {
        val id = intent.getLongExtra(SearchActivity.EXTRA_BOARD_ID, -1)
        api.completeBoard(id = id).enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit?>,
                response: Response<Unit?>
            ) {
                if (response.isSuccessful) {
                    Log.d("COMPLETE", "처리완료")
                    val intent = Intent(this@LostDetailActivity, SearchActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
                    Log.d("COMPLETE", "통신완료 , 처리미완")
                }
            }

            override fun onFailure(call: Call<Unit?>, t: Throwable) {
                Log.d("COMPLETE", "통신실패")
            }
        })
    }


    private fun handleChattingButtonClick(targetActivity: Class<*>) {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        val targetUserId: String = detailData?.userId ?: return
        val boardIdx: Long = detailData?.idx ?: return

        val intent = Intent(this, targetActivity).apply {
            putExtra("otherUserId", targetUserId)
            putExtra("boardIdx", boardIdx)
        }
        startActivity(intent)
    }

}