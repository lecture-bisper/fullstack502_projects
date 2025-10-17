package bitc.full502.lostandfound.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import bitc.full502.lostandfound.R
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.BoardService
import bitc.full502.lostandfound.data.api.GeoService
import bitc.full502.lostandfound.data.api.ReverseGeoService
import bitc.full502.lostandfound.data.model.BoardData
import bitc.full502.lostandfound.databinding.ActivityLostWriteBinding
import bitc.full502.lostandfound.map.MapHelper // ✅ 사용
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.Constants.EXTRA_BOARD_DATA
import bitc.full502.lostandfound.util.Constants.EXTRA_BOARD_ID
import bitc.full502.lostandfound.util.Constants.EXTRA_MODE
import bitc.full502.lostandfound.util.Constants.MODE_EDIT
import bitc.full502.lostandfound.util.Formatter
import bitc.full502.lostandfound.util.GeocodingConstants
import bitc.full502.lostandfound.util.ReverseGeocoder
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.Locale

class LostWriteActivity : AppCompatActivity() {

    private var isEditMode = false
    private var editingBoard: BoardData? = null
    private var editingBoardId: Long = -1L
    private val REQ_LOCATION = 2001
    private lateinit var mapHelper: MapHelper
    private val tokenManager: TokenManager by lazy { TokenManager(this) }
    private val binding by lazy { ActivityLostWriteBinding.inflate(layoutInflater) }
    private val reverseGeo by lazy { ApiClient.createJsonService(GeocodingConstants.REVERSE_GEOCODE_URL, ReverseGeoService::class.java) }
    private val geo by lazy { ApiClient.createJsonService(GeocodingConstants.GEOCODE_URL, GeoService::class.java) }
    private val api by lazy { ApiClient.createJsonService(Constants.BASE_URL, BoardService::class.java) }
    private lateinit var addressLauncher: ActivityResultLauncher<Intent>
    private val fileProviderAuthority by lazy { "$packageName.fileprovider" }
    private var pendingCameraImageUri: Uri? = null
    private var uploadImageUri: Uri? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) showImage(uri)
    }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            pendingCameraImageUri?.let { showImage(it) }
        } else {
            cleanupPendingCameraFile()
        }
    }
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) launchCamera()
            else Toast.makeText(this, "카메라 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    private var naverMap: NaverMap? = null
    private val mapMarker by lazy { Marker() }
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // WindowInsets 적용
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.category_array, R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.selectCategory.adapter = adapter

        setupUi()

        mapHelper = MapHelper(
            activity = this,
            fragmentManager = supportFragmentManager,
            mapContainerId = R.id.map_fragment,
            ncpApiKey = GeocodingConstants.NCP_KEY_ID, // 🔁 지도 SDK용 Client ID로 교체
            locationRequestCode = REQ_LOCATION
        )

        mapHelper.init { map ->
            naverMap = map
            setupMapListeners(map) // 지도 준비 후 리스너 연결
            editingBoard?.let { fillFormFromBoard(it) }
        }

        addressLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val road = data?.getStringExtra("roadAddress")
                val zonecode = data?.getStringExtra("zonecode")
                binding.etAddress.setText("$road ($zonecode)")
                geocodeAddress("$road ($zonecode)")
            }
        }

        val mode = intent.getStringExtra(EXTRA_MODE)
        if (mode == MODE_EDIT) {
            val board = intent.getParcelableExtra<BoardData>(EXTRA_BOARD_DATA)
            val boardId = intent.getLongExtra(EXTRA_BOARD_ID, -1L)
            if (board != null && boardId > 0) {
                editingBoard = board
                editingBoardId = boardId
                isEditMode = true
                enterEditMode(board, boardId)
            }
        }
    }

    // ============================================================
    // UI 리스너 및 초기화 모음
    // ============================================================
    private fun setupUi() {
        binding.topBar.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.imageRegistSlot.setOnClickListener { showPickOrCameraSheet() }
        binding.imagePlaceholder.setOnClickListener { showPickOrCameraSheet() }
        binding.selectCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, id: Long) {}
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.lostDate.setOnClickListener { showDateTimePicker() }
        binding.tilAddress.setEndIconOnClickListener {
            val intent = Intent(this, AddressSearchActivity::class.java)
            addressLauncher.launch(intent)
        }
        // 등록
        binding.incRegistBar.btnRegister.setOnClickListener { onClickRegister() }
    }

    // ============================================================
    // 등록 버튼 클릭 핸들러
    // ============================================================
    private fun onClickRegister() {
        val lostDateRaw = binding.lostDate.text?.toString()?.trim().orEmpty()
        if (lostDateRaw.isBlank()) {
            Toast.makeText(this, "날짜를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val point = selectedLatLng ?: run {
            Toast.makeText(this, "지도를 길게 눌러 위치를 선택하세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val lostDateStr = Formatter.displayToIsoSecondsOrNull(lostDateRaw)
        if (lostDateStr == null) {
            Toast.makeText(this, "날짜 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val categoryCode = binding.selectCategory.selectedItemPosition
        val lostProductName = binding.tvLostProductName.text?.toString()?.trim().orEmpty()
        val lostPerson = binding.lostPersonName.text?.toString()?.trim().orEmpty()
        val lat = point.latitude
        val lng = point.longitude
        val fullAddress = listOf(
            binding.etAddress.text?.toString()?.trim().orEmpty(),
            binding.detailAddress.text?.toString()?.trim().orEmpty()
        ).filter { it.isNotBlank() }.joinToString(" ")
        val detailAddress =  binding.detailAddress.text?.toString()?.trim().orEmpty()
        val contentText = binding.comments.text?.toString()?.trim().orEmpty()
        run {
            var ok = true
            if (lostProductName.isBlank()) {
                ok = false; Toast.makeText(this, "분실물명을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            if (fullAddress.isBlank()) {
                ok = false; Toast.makeText(this, "주소를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            if (!ok) return
        }
        val filePart: MultipartBody.Part? = try {
            uploadImageUri?.let { uri ->
                val mime = contentResolver.getType(uri) ?: "application/octet-stream"
                val rb = object : RequestBody() {
                    override fun contentType() = mime.toMediaType()
                    override fun writeTo(sink: okio.BufferedSink) {
                        contentResolver.openInputStream(uri)?.use { input ->
                            input.copyTo(sink.outputStream())
                        } ?: throw IllegalStateException("이미지 스트림을 열 수 없습니다.")
                    }
                }
                MultipartBody.Part.createFormData("file", "upload.jpg", rb)
            }
        } catch (e: Throwable) {
            Toast.makeText(this, "사진 준비 중 오류: ", Toast.LENGTH_SHORT).show()
            null
        }

        val dto = BoardData(
            idx = 0L,
            userId = "",
            categoryId = categoryCode,
            title = lostProductName,
            imgUrl = "",
            ownerName = lostPerson.ifBlank { "이름없음" },
            description = contentText,
            eventDate = lostDateStr,
            eventLat = lat,
            eventLng = lng,
            eventDetail = detailAddress,
            storageLocation = "미보관상태 - 분실물",
            type = "LOST",
            status = "PENDING",
            createDate = null
        )
        val dtoPart = Gson().toJson(dto).toRequestBody("text/plain; charset=UTF-8".toMediaType())

        val token = tokenManager.getToken().orEmpty()
        if (token.isBlank()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        api.insertBoard("Bearer $token", dtoPart, filePart)
            .enqueue(object : Callback<BoardData> {
                override fun onResponse(call: Call<BoardData>, resp: Response<BoardData>) {
                    val body = resp.body()
                    val ok = resp.isSuccessful && body != null && (body.idx ?: 0L) > 0L
                    if (ok) {
                        Toast.makeText(this@LostWriteActivity, "등록 성공!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@LostWriteActivity, "등록 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BoardData>, t: Throwable) {
                    Toast.makeText(this@LostWriteActivity, "통신 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ============================================================
    // 사진 선택/촬영
    // ============================================================
    private fun showPickOrCameraSheet() {
        val items = if (isEditMode) {
            arrayOf("사진 선택", "카메라 촬영") // ← 수정 모드: 2번 항목 제거
        } else {
            arrayOf("사진 선택", "카메라 촬영", "사진 선택 안함")
        }

        AlertDialog.Builder(this)
            .setTitle(if (isEditMode) "사진 변경" else "사진 등록")
            .setItems(items) { _, which ->
                when (items[which]) { // 인덱스 말고 '텍스트'로 분기
                    "사진 선택" -> launchPhotoPicker()
                    "카메라 촬영" -> ensureCameraPermissionThenLaunch()
                    "사진 선택 안함" -> clearSelectedImage()
                }
            }
            .show()
    }

    // 앨범에서 선택
    private fun launchPhotoPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // 카메라 권한 확인 후 촬영
    private fun ensureCameraPermissionThenLaunch() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    // 사진 선택 해제
    private fun clearSelectedImage() {
        uploadImageUri = null
        binding.ivRegisted.setImageDrawable(null)
        binding.imagePlaceholder.visibility = View.VISIBLE
    }

    // 카메라 실행: 캐시에 임시 파일 생성 후 촬영 결과를 해당 URI에 기록
    private fun launchCamera() {
        val uri = createTempImageUriInCache()
        pendingCameraImageUri = uri
        takePicture.launch(uri)
    }

    // 이미지 선택/촬영 후 미리보기
    private fun showImage(uri: Uri) {
        uploadImageUri = uri
        binding.ivRegisted.setImageURI(uri)
        binding.imagePlaceholder.visibility = View.GONE
    }

    // cache/images/ 에 임시 파일 생성 + FileProvider로 content URI 획득
    private fun createTempImageUriInCache(): Uri {
        val imagesDir = File(cacheDir, "images").apply { mkdirs() }
        val fileName = "LostFound_${timestamp()}.jpg"
        val file = File(imagesDir, fileName).apply {
            if (exists()) delete()
            createNewFile()
        }
        return FileProvider.getUriForFile(this, fileProviderAuthority, file)
    }

    // 촬영 취소 시 임시 URI만 초기화
    private fun cleanupPendingCameraFile() {
        pendingCameraImageUri = null
    }

    // 촬영 파일 이름 규격
    private fun timestamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(System.currentTimeMillis())

    // ============================================================
    // 날짜 선택 다이얼로그
    // ============================================================
    private fun showDateTimePicker() {
        // 기준: 현재 시각
        val now = java.util.Calendar.getInstance()
        val y = now.get(java.util.Calendar.YEAR)
        val m = now.get(java.util.Calendar.MONTH)
        val d = now.get(java.util.Calendar.DAY_OF_MONTH)
        val H = now.get(java.util.Calendar.HOUR_OF_DAY)
        val M = now.get(java.util.Calendar.MINUTE)

        // 1) 날짜 선택(스피너 스타일 유지)
        val themedCtx = androidx.appcompat.view.ContextThemeWrapper(this, R.style.SpinnerDatePickerDialog)
        val dateDialog = android.app.DatePickerDialog(
            themedCtx,
            { _, year, monthZero, dayOfMonth ->
                // 2) 날짜 선택 후 시간 선택
                showTimePicker(year, monthZero, dayOfMonth)
            },
            y, m, d
        )
        dateDialog.datePicker.maxDate = now.timeInMillis  // 날짜 자체는 오늘까지
        dateDialog.show()
        dateDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.apply {
            text = "다음"; isAllCaps = false
            setTextColor(ContextCompat.getColor(this@LostWriteActivity, R.color.black))
        }
        dateDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.apply {
            text = "취소"; isAllCaps = false
            setTextColor(ContextCompat.getColor(this@LostWriteActivity, R.color.black))
        }
    }
    private fun showTimePicker(year: Int, monthZero: Int, day: Int) {
        val now = java.util.Calendar.getInstance()
        val initHour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val initMinute = now.get(java.util.Calendar.MINUTE)
        val is24Hour = android.text.format.DateFormat.is24HourFormat(this)
        val timeDialog = android.app.TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // 최종 검증: 오늘 날짜를 선택했고, 시간이 미래면 현재 시각으로 클램프
                val selected = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.YEAR, year)
                    set(java.util.Calendar.MONTH, monthZero)
                    set(java.util.Calendar.DAY_OF_MONTH, day)
                    set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                    set(java.util.Calendar.MINUTE, minute)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                val nowCal = java.util.Calendar.getInstance()
                if (selected.after(nowCal)) {
                    // 미래 불허: 현재 시각으로 조정
                    selected.timeInMillis = nowCal.timeInMillis
                }

                val y = selected.get(java.util.Calendar.YEAR)
                val m = selected.get(java.util.Calendar.MONTH) + 1
                val d = selected.get(java.util.Calendar.DAY_OF_MONTH)
                val H = selected.get(java.util.Calendar.HOUR_OF_DAY)
                val M = selected.get(java.util.Calendar.MINUTE)

                // 입력 필드에는 "yyyy-MM-dd HH:mm"
                val display = String.format(Locale.getDefault(), "%04d년 %02d월 %02d일 %02d시 %02d분", y, m, d, H, M)
                binding.lostDate.setText(display)
            },
            initHour,
            initMinute,
            is24Hour
        )

        timeDialog.setTitle("시간 선택")
        timeDialog.show()
    }


    // ============================================================
    // 지도: 롱클릭 → 마커/주소 반영
    // ============================================================
    private fun setupMapListeners(map: NaverMap) {
        map.setOnMapLongClickListener { _, coord ->
            selectedLatLng = coord
            mapMarker.position = coord
            mapMarker.map = map

            ReverseGeocoder.fetchAddress(reverseGeo, coord.latitude, coord.longitude) { base ->
                val addressLine = base ?: "주소 미확인"
                binding.etAddress.setText(addressLine)
            }

            map.moveCamera(
                CameraUpdate.toCameraPosition(CameraPosition(coord, 16.0))
                    .animate(CameraAnimation.Fly, 800L)
            )
        }
    }

    // 주소 → 좌표
    private fun geocodeAddress(query: String) {
        lifecycleScope.launch {
            try {
                val res = geo.getCoordinateFromAddress(
                    keyId = GeocodingConstants.NCP_KEY_ID,
                    key = GeocodingConstants.NCP_KEY,
                    query = query
                )
                val first = res.addresses.firstOrNull()
                if (first == null) {
                    Toast.makeText(this@LostWriteActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                applyGeocodeResult(first)
            } catch (e: retrofit2.HttpException) {
                val code = e.code()
                val errBody = e.response()?.errorBody()?.string()
                Log.e("NCP-GEOCODE", "HTTP $code body=$errBody", e)
                Toast.makeText(this@LostWriteActivity, "주소 검색 실패", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("NCP-GEOCODE", "onError", e) // UnknownHost/SSLHandshake/Timeout 등
                Toast.makeText(this@LostWriteActivity, "주소 검색 실패:", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 지오코딩 결과 반영
    private fun applyGeocodeResult(addr: bitc.full502.lostandfound.data.model.Address) {
        val lat = addr.latDouble
        val lng = addr.lngDouble
        if (lat == null || lng == null) {
            Toast.makeText(this, "좌표 변환 실패(위도/경도 없음)", Toast.LENGTH_SHORT).show()
            return
        }

        val label = when {
            addr.roadAddress.isNotBlank() -> addr.roadAddress
            addr.jibunAddress.isNotBlank() -> addr.jibunAddress
            else -> "주소 미확인"
        }
        binding.etAddress.setText(label)

        val pos = LatLng(lat, lng)
        selectedLatLng = pos
        mapMarker.position = pos
        mapMarker.map = naverMap

        naverMap?.moveCamera(
            CameraUpdate.toCameraPosition(CameraPosition(pos, 16.0))
                .animate(CameraAnimation.Fly, 800L)
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        mapHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun enterEditMode(board: BoardData, boardId: Long) {
        // 2) 폼을 기존 내용으로 채움
        fillFormFromBoard(board)
        // 3) "등록하기" 클릭 시 PUT 호출로 연결
        binding.incRegistBar.btnRegister.setOnClickListener { submitUpdate(boardId) }
    }

    private fun fillFormFromBoard(b: BoardData) {

        binding.tvLostProductName.setText(b.title.orEmpty())
        binding.lostPersonName.setText(b.ownerName.orEmpty())
        binding.lostDate.setText(Formatter.isoToDisplayOrEmpty(b.eventDate))
//        val (baseAddr, detailAddr) = splitAddress(b.eventDetail)
//        binding.etAddress.setText(b.eventDetail)
        binding.detailAddress.setText(b.eventDetail)
        binding.comments.setText(b.description.orEmpty())
        // 카테고리: 서버 categoryId → 스피너 선택으로 매핑(포지션=코드면 그대로)
        runCatching {
            if (b.categoryId >= 0) binding.selectCategory.setSelection(b.categoryId)
        }
        val lat = b.eventLat
        val lng = b.eventLng
       ReverseGeocoder.fetchAddress(reverseGeo, lat, lng) { base ->
                if (!base.isNullOrBlank() && binding.etAddress.text.isNullOrBlank()) {
                    binding.etAddress.setText(base)
                }
            }
        if (lat != null && lng != null) {
            val pos = LatLng(lat, lng)
            selectedLatLng = pos
            mapMarker.position = pos
            mapMarker.map = naverMap
            naverMap?.moveCamera(
                CameraUpdate.toCameraPosition(CameraPosition(pos, 16.0))
                    .animate(CameraAnimation.Fly, 500L)
            )
        }
        // 이미지
        val url = b.imgUrl?.takeIf { it.isNotBlank() }?.let {
            if (it.startsWith("http")) it else Constants.IMAGE_BASE_URL + it
        }
        if (url != null) {
            // 기존 이미지 보이기 + placeholder 숨김
            binding.imagePlaceholder.visibility = View.GONE
            com.bumptech.glide.Glide.with(this)
                .load(url)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.ivRegisted)
        } else {
            // 이미지가 없으면 기존 로직 그대로
            binding.ivRegisted.setImageDrawable(null)
            binding.imagePlaceholder.visibility = View.VISIBLE
        }
    }


    private fun nonBlankOrNull(s: CharSequence?) =
        s?.toString()?.trim()?.takeIf { it.isNotEmpty() }

    /** 서버 규약: null이면 해당 필드 미교체 */
    private fun buildUpdatePayload(): Pair<RequestBody, MultipartBody.Part?> {
        val origin = editingBoard
        val boardId = editingBoardId

        // 텍스트 필드
        val title       = nonBlankOrNull(binding.tvLostProductName.text)
        val ownerName   = nonBlankOrNull(binding.lostPersonName.text)
        val description = nonBlankOrNull(binding.comments.text)

        // 카테고리: 스피너 포지션 = 코드라고 가정
        // 변경 없으면 null로(미교체), 변경 되었으면 새 값
        val selCat = binding.selectCategory.selectedItemPosition
        val categoryId: Int? =
            if (origin != null && selCat == origin.categoryId) null else selCat

        // 주소(기본 + 상세)
        val base   = nonBlankOrNull(binding.etAddress.text)
        val detail = nonBlankOrNull(binding.detailAddress.text)
        val eventDetail = detail
        // 날짜: 입력 없으면 원본 유지(null 보냄 → 미교체), 입력 있으면 ISO-8601(초)
        val pickedDateRaw = nonBlankOrNull(binding.lostDate.text)
        val eventDate: String? = Formatter.displayToIsoSecondsOrNull(pickedDateRaw)

        // 좌표: 새 선택이 있으면 새 값, 없으면 null(미교체)
        val latLng = selectedLatLng
        val eventLat: Double? = latLng?.latitude
        val eventLng: Double? = latLng?.longitude

        // type/status/storageLocation: 수정 안 한다면 null로 둠
        val type: String? = null
        val status: String? = null
        val storageLocation: String? = null


        // DTO 맵 (null 포함: 서버가 null은 무시)
        val dtoMap = mapOf(
            "idx"             to boardId,
            "title"           to title,
            "ownerName"       to ownerName,
            "description"     to description,
            "categoryId"      to categoryId,
            "eventDate"       to eventDate,
            "eventLat"        to eventLat,
            "eventLng"        to eventLng,
            "eventDetail"     to eventDetail,
            "imgUrl"          to null,
            "type"            to type,
            "status"          to status,
            "storageLocation" to storageLocation
        )

        val json = com.google.gson.Gson().toJson(dtoMap)
        val dtoPart = json.toRequestBody("application/json; charset=UTF-8".toMediaType())

        // 파일 Part (선택)
        val filePart: MultipartBody.Part? = try {
            uploadImageUri?.let { uri ->
                val mime = contentResolver.getType(uri) ?: "application/octet-stream"
                val rb = object : RequestBody() {
                    override fun contentType() = mime.toMediaType()
                    override fun writeTo(sink: okio.BufferedSink) {
                        contentResolver.openInputStream(uri)?.use { input ->
                            input.copyTo(sink.outputStream())
                        } ?: throw IllegalStateException("이미지 스트림을 열 수 없습니다.")
                    }
                }
                MultipartBody.Part.createFormData("file", "upload.jpg", rb)
            }
        } catch (e: Throwable) {
            Toast.makeText(this, "사진 준비 중 오류", Toast.LENGTH_SHORT).show()
            null
        }
        return dtoPart to filePart
    }
    private fun submitUpdate(boardId: Long) {
        Log.d("UPDATETEST" , "수정 버튼 진입")

        if (boardId <= 0) {
            Toast.makeText(this, "잘못된 게시글 ID", Toast.LENGTH_SHORT).show(); return
        }

        val (dtoPart, filePart) = buildUpdatePayload()

        api.updateBoard( dtoPart, filePart)
            .enqueue(object : retrofit2.Callback<BoardData> {
                override fun onResponse(
                    call: retrofit2.Call<BoardData>,
                    res: retrofit2.Response<BoardData>
                ) {
                    if (res.isSuccessful) {
                        val updated = res.body()
                        val data = Intent().apply {
                            putExtra(Constants.EXTRA_BOARD_DATA, updated)
                            putExtra(Constants.EXTRA_BOARD_ID, updated?.idx ?: editingBoardId)
                        }
                        setResult(Activity.RESULT_OK, data)
                        Toast.makeText(this@LostWriteActivity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("UPDATETEST" , "수정완료")
                        finish()

                    } else {
                        Toast.makeText(this@LostWriteActivity, "수정 실패: ${res.code()}", Toast.LENGTH_SHORT).show()
                        Log.d("UPDATETEST" , "수정 실패")
                    }
                }
                override fun onFailure(call: retrofit2.Call<BoardData>, t: Throwable) {
                    Toast.makeText(this@LostWriteActivity, "통신 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("UPDATETEST" , "통신 오류")
                }
            })
    }
}
