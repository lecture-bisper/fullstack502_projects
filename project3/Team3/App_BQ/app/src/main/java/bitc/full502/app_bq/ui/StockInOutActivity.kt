package bitc.full502.app_bq.ui

import InputFilterMinMax
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.ItemDto
import bitc.full502.app_bq.data.model.ItemSearchDto
import bitc.full502.app_bq.data.model.StockDto
import bitc.full502.app_bq.data.model.StockRequestDto
import bitc.full502.app_bq.data.model.UserDto
import bitc.full502.app_bq.data.model.WarehouseDto
import bitc.full502.app_bq.databinding.ActivityStockInOutBinding
import bitc.full502.app_bq.utill.Constants.BASE_URL
import bitc.full502.lostandfound.storage.TokenManager
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.String

enum class StockMode { IN, OUT }
private enum class ScanStep { WAREHOUSE, ITEM }

private var scanStep = ScanStep.WAREHOUSE

class StockInOutActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStockInOutBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService

    companion object {
        private const val EXTRA_MODE = "stock_mode"
        fun newIntent(context: Context, mode: StockMode) =
            Intent(context, StockInOutActivity::class.java)
                .putExtra(EXTRA_MODE, mode.name)
    }

    private val mode: StockMode by lazy {
        val name = intent.getStringExtra(EXTRA_MODE)
            ?: error("Missing Intent extra: $EXTRA_MODE")
        runCatching { enumValueOf<StockMode>(name) }
            .getOrElse { throw IllegalArgumentException("Invalid $EXTRA_MODE: $name", it) }
    }

    // CameraX/ML Kit
    private val cameraExecutor by lazy { Executors.newSingleThreadExecutor() }
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var preview: Preview? = null

    // 스캔 on/off 플래그
    private val isScanning = AtomicBoolean(false)

    // 디바운스: 동일 QR 연타 방지
    private var lastValue: String? = null
    private var lastTs: Long = 0L

    // ML Kit 스캐너 (QR만)
    private val barcodeScanner by lazy {
        val opt = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(opt)
    }

    // 권한 launcher
    private val reqCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            bindCameraUseCases()
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }
    private lateinit var stockCode: String
    private var stockId: Long = 0

    //    창고 보유 수량 저장
    private var maxQuantity: Long = 0

    private lateinit var currentItem: StockDto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        tokenManager = TokenManager(applicationContext)

        apiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getModeUi()


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
        // 메뉴 초기 업데이트
        updateNavMenu()

        binding.btnScanStock.setOnClickListener {
            setStepUi(ScanStep.WAREHOUSE)
            startScanning()
        }
        binding.btnScanItem.setOnClickListener {
            setStepUi(ScanStep.ITEM)
            startScanning()
        }

//        뒤로가기 이벤트
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showLeaveConfirmDialog()
                }
            })

        binding.btnRegistStock.setOnClickListener {
            registStock()
        }
    }

    override fun onStart() {
        super.onStart()
        ensurePermissionThen {
            bindCameraUseCases()
        }
    }

    override fun onResume() {
        super.onResume()
        ensurePermissionThen {
            setStepUi(scanStep)
            bindCameraUseCases()
        }
    }

    override fun onPause() {
        super.onPause()
//        stopScanning()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }

    private fun updateNavMenu() {
        val token = tokenManager.getToken()
        val navMenu = binding.navView.menu

        if (token.isNullOrEmpty()) {
            // 로그아웃 상태
            navMenu.setGroupVisible(R.id.group_logged_out, true)
            navMenu.setGroupVisible(R.id.group_logged_in, false)
            navMenu.findItem(R.id.nav_user_id)?.title = "userId"
        } else {
            navMenu.setGroupVisible(R.id.group_logged_out, false)
            navMenu.setGroupVisible(R.id.group_logged_in, true)

            // Retrofit enqueue 방식으로 사용자 정보 가져오기
            apiService.getMyInfo("Bearer $token").enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            navMenu.findItem(R.id.nav_user_id)?.title = "${user.empName}님"
                        }
                    } else {
                        clearTokenAndResetMenu(navMenu)
                    }
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    t.printStackTrace()
                    navMenu.findItem(R.id.nav_user_id)?.title = "userId"
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

    private fun setStepUi(step: ScanStep) {
        scanStep = step
        when (step) {
            ScanStep.WAREHOUSE -> {
                binding.btnScanStock.isEnabled = true
                binding.btnScanStock.alpha = 1.0f

                binding.btnScanItem.isEnabled = false
                binding.btnScanItem.alpha = 0.4f
            }

            ScanStep.ITEM -> {
                binding.scanText.text = "비품 QR을 스캔하세요."
                binding.btnScanStock.isEnabled = false
                binding.btnScanStock.alpha = 0.4f

                binding.btnScanItem.isEnabled = true
                binding.btnScanItem.alpha = 1.0f
            }
        }
    }


    //    입고하기 출고하기 버튼 진입에 따라 텍스트 다르게 보이도록 설정
    fun getModeUi() {
        isScanning.set(false)
        when (mode) {
            StockMode.IN -> {
                binding.scanFrame.visibility = View.GONE
                binding.stockTitle.text = "입고등록"
                binding.stockBtnText.text = "입고 등록하기"
                setStepUi(ScanStep.WAREHOUSE)
            }

            StockMode.OUT -> {
                binding.stockTitle.text = "출고등록"
                binding.stockBtnText.text = "출고 등록하기"
                setStepUi(ScanStep.WAREHOUSE)
                startScanning()
            }
        }
    }

    private fun ensurePermissionThen(block: () -> Unit) {
        val perm = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, perm)
            == PackageManager.PERMISSION_GRANTED
        ) block()
        else reqCameraPermission.launch(perm)
    }

    private fun clearTokenAndResetMenu(navMenu: Menu) {
        tokenManager.clearToken()
        navMenu.setGroupVisible(R.id.group_logged_out, true)
        navMenu.setGroupVisible(R.id.group_logged_in, false)
        navMenu.findItem(R.id.nav_user_id)?.title = "userId"
    }

    //    (imageProxy.image 가 실험기능이라 아래 어노테이션 붙여야함)
    @OptIn(ExperimentalGetImage::class)
    private fun bindCameraUseCases() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            cameraProvider = provider
            // 프리뷰
            preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.cameraPreview.surfaceProvider
            }
            // 이미지 분석 (가장 최신 프레임만)
            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also { analysis ->
                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val media = imageProxy.image
                        if (media == null) {
                            imageProxy.close();
                            return@setAnalyzer
                        }
                        // 스캔 off면 바로 반환
                        if (!isScanning.get()) {
                            imageProxy.close();
                            return@setAnalyzer
                        }
                        val input = InputImage.fromMediaImage(
                            media, imageProxy.imageInfo.rotationDegrees
                        )
                        barcodeScanner.process(input)
                            .addOnSuccessListener { list ->
                                val value =
                                    list.firstOrNull()?.rawValue ?: return@addOnSuccessListener
                                val now = System.currentTimeMillis()
                                if (value == lastValue && now - lastTs < 800) {
                                    return@addOnSuccessListener
                                }
                                when (scanStep) {
                                    ScanStep.WAREHOUSE -> {
                                        if (::stockCode.isInitialized && stockCode == value) {
                                            return@addOnSuccessListener
                                        }
                                        checkWarehouse(value)
                                    }

                                    ScanStep.ITEM -> {
                                        if (value == stockCode) {
                                            Log.d("Scan", "Ignored: same as stockCode ($value)")
                                            return@addOnSuccessListener
                                        }
                                        checkItem(stockCode, value)
                                    }
                                }
                                lastValue = value
                                lastTs = now
                            }
                            .addOnFailureListener { Log.d("readBarcode", "바코드 해석 실패") }
                            .addOnCompleteListener { imageProxy.close() } // 중요
                    }
                }

            // 후면 카메라 선택
            val selector = CameraSelector.DEFAULT_BACK_CAMERA

            // 기존 바인딩 해제 후 재바인딩
            provider.unbindAll()
            provider.bindToLifecycle(
                this, selector,
                preview, imageAnalysis
            )

        }, ContextCompat.getMainExecutor(this))
    }

    //    스캔 시작
    private fun startScanning() {
        isScanning.set(true)
        binding.scanFrame.visibility = View.VISIBLE
    }

    //    알림 전화 등 다른 포커스 이동으로 인하여 스캔 일시중지할 때
    private fun stopScanning() {
        isScanning.set(false)
        binding.scanFrame.visibility = View.GONE

    }

    //    비품 QR 등록 전 창고 검증
    private fun checkWarehouse(value: String) {
        val scannedStock = value
        apiService.getAllWarehouse().enqueue(object : Callback<List<WarehouseDto>> {
            override fun onResponse(
                call: Call<List<WarehouseDto>>,
                response: Response<List<WarehouseDto>>
            ) {
                if (response.isSuccessful) {
                    val warehouses = response.body().orEmpty()
                    val thiswarehouse = warehouses.firstOrNull { it.krName == scannedStock }
                    if (thiswarehouse != null) {
                        stockCode = scannedStock
                        stockId = thiswarehouse.id
                        onBarcode(scannedStock)
                        return
                    } else {
                        Log.d("StockInOut", "창고 코드 $scannedStock 없음")
                        Toast.makeText(this@StockInOutActivity, "존재하지 않는 창고입니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d("StockInOut", "통신성공 연결실패")
                }
            }

            override fun onFailure(call: Call<List<WarehouseDto>>, t: Throwable) {
                t.printStackTrace()
                Log.d("StockInOut", "통신실패")
            }
        })
    }

    private fun onBarcode(value: String) {
        runOnUiThread {
            when (scanStep) {
                ScanStep.WAREHOUSE -> {
                    stockCode = value
                    stopScanning()
                    setStepUi(ScanStep.ITEM)
                    scanStep = ScanStep.ITEM
                    binding.scannedStockName.text = stockCode
                    binding.scannedStockName.visibility = View.VISIBLE
                    binding.btnScanStock.visibility = View.GONE
                }

                ScanStep.ITEM -> {
                    checkItem(stockCode, value)
                }
            }
        }
    }

    private fun finishPreview() {
        isScanning.set(false)
        binding.scanFrame.visibility = View.GONE
    }

    //    스캔한 비품 코드로 비품 정보와 재고수량 가지고 오는 함수
    private fun checkItem(stockCode: String, value: String) {
        apiService.stockByItemForApp(code = value)
            .enqueue(object : Callback<List<StockDto>> {
                override fun onResponse(
                    call: Call<List<StockDto>>,
                    response: Response<List<StockDto>>
                ) {
                    if (!response.isSuccessful) {
                        Log.d("StockInOut", "getStockByItem 처리 실패")
                        Toast.makeText(
                            this@StockInOutActivity,
                            "비품 정보 조회 오류 : 담당부서에 문의하세요",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val stockItems = response.body().orEmpty()
                    val match = stockItems.firstOrNull {
                        it.itemCode == value && it.warehouseKrName == stockCode
                    }

                    when (mode) {
                        StockMode.OUT -> {
                            if (match == null || match.quantity == 0L) {
                                Toast.makeText(
                                    this@StockInOutActivity,
                                    "전산 재고가 없습니다. 담당 부서에 확인해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                            handleOut(stockCode, match)
                        }

                        StockMode.IN -> {
                            // 이미 창고 레코드가 있으면 그대로 사용
                            if (match != null) {
                                handleOut(stockCode, match)
                                return
                            }
                            apiService.searchItems(
                                name = null,
                                manufacturer = null,
                                code = value,
                                categoryId = null,
                                minPrice = null,
                                maxPrice = null,
                                status = "ACTIVE",
                            ).enqueue(object : Callback<List<ItemDto>> {
                                override fun onResponse(call: Call<List<ItemDto>>, response: Response<List<ItemDto>>) {
                                        val item = if (response.isSuccessful)
                                            response.body().orEmpty().firstOrNull { it.code == value }
                                        else null

                                        val safeMatch = StockDto(
                                            id = 0L,
                                            itemCode = value,                              // QR = 비품 코드
                                            itemName = item?.name.orEmpty(),        // ItemDto.name
                                            manufacturer = item?.manufacturer.orEmpty(),
                                            categoryName = item?.categoryName.orEmpty(),
                                            warehouseKrName = stockCode,
                                            quantity = 0L,
                                            warehouseName = stockCode
                                        )
                                        handleOut(stockCode, safeMatch)
                                    }

                                override fun onFailure(call: Call<List<ItemDto>>, t: Throwable) {
                                    Toast.makeText(this@StockInOutActivity , "비품 정보가 없습니다", Toast.LENGTH_SHORT).show()
                                    Log.d("ItemListActivity", "검색 실패: ${t.message}")
                                }
                            })
                        }

                            // 없으면 아이템 마스터 조회 후 StockDto로 변환
//                            apiService.searchItemList(ItemSearchDto(code = value))
//                                .enqueue(object : Callback<List<ItemDto>> {
//                                    override fun onResponse(
//                                        call: Call<List<ItemDto>>,
//                                        resp: Response<List<ItemDto>>
//                                    ) {
//                                        val item = if (resp.isSuccessful)
//                                            resp.body().orEmpty().firstOrNull { it.code == value }
//                                        else null
//
//                                        val safeMatch = StockDto(
//                                            id = 0L,
//                                            itemCode = value,                              // QR = 비품 코드
//                                            itemName = item?.name.orEmpty(),        // ItemDto.name
//                                            manufacturer = item?.manufacturer.orEmpty(),
//                                            categoryName = item?.categoryName.orEmpty(),
//                                            warehouseKrName = stockCode,
//                                            quantity = 0L,
//                                            warehouseName = stockCode
//                                        )
//                                        handleOut(stockCode, safeMatch)
//                                    }
//
//                                    override fun onFailure(
//                                        call: Call<List<ItemDto>>,
//                                        t: Throwable
//                                    ) {
//                                        Log.d("SearchItem", "아이템 검색 api 호출 실패", t)
//                                    }
//                                }

                        }

                    binding.scannedItemCode.text = value
                    binding.scannedItemName.visibility = View.VISIBLE
                    binding.btnScanItem.visibility = View.GONE
                }

                override fun onFailure(call: Call<List<StockDto>>, t: Throwable) {
                    Log.d("StockInOut", "checkItem 통신 실패", t)
                }
            })

        finishPreview()
    }


    //    창고 및 비품 검증 후 view 변경하는 단계
    private fun handleOut(stockCode: String?, match: StockDto) {

        currentItem = match

        binding.scannedStockName.text = stockCode
        binding.scannedItemCode.text = match.itemCode
        binding.btnScanStock.visibility = View.GONE
        binding.btnScanItem.visibility = View.GONE
        binding.scannedStockName.visibility = View.VISIBLE
        binding.scannedItemCode.visibility = View.VISIBLE
        binding.scannedItemName.text = match.itemName
        binding.scannedItemCorper.text = match.manufacturer
        binding.scannedItemCategory.text = match.categoryName
//        binding.etQuantity.hint = "창고 보유 수량은 ${maxQuantity}개 입니다."

        if (mode == StockMode.OUT) {
            maxQuantity = match.quantity
            binding.etQuantity.filters = arrayOf(InputFilterMinMax(1, maxQuantity))
        } else {
            maxQuantity = 999999999
            binding.etQuantity.filters = arrayOf(InputFilterMinMax(1, Int.MAX_VALUE.toLong()))
        }
        binding.etQuantity.addTextChangedListener(
            afterTextChanged = {
                val v = it?.toString()?.toIntOrNull() ?: return@addTextChangedListener
                if (v > maxQuantity) {
                    binding.etQuantity.setText(maxQuantity.toString())
                    Toast.makeText(this, "보유수량 이상 입력할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    binding.etQuantity.setSelection(binding.etQuantity.text.length)
                } else if (v < 1L) {
                    binding.etQuantity.setText("1")
                    Toast.makeText(this, "1개 이상 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
                    binding.etQuantity.setSelection(binding.etQuantity.text.length)
                }
            }
        )
    }

    private fun registStock() {
        val token = tokenManager.getToken()
        val quantity = if (binding.etQuantity.text.toString() == "") 0L
        else binding.etQuantity.text.toString().toLong()

        val request = StockRequestDto(
            code = currentItem.itemCode,
            remark = binding.etRemark.text?.toString(),
            warehouseId = stockId,
            quantity = quantity
        )

        if (quantity == 0L) {
            Toast.makeText(this, "수량을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        when (mode) {
            StockMode.IN -> {
                apiService.stockIn("Bearer $token", request)
                    .enqueue(object : Callback<StockRequestDto> {
                        override fun onResponse(
                            call: Call<StockRequestDto?>,
                            response: Response<StockRequestDto?>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@StockInOutActivity,
                                    "입고 처리되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Log.d("Regist", "regist_in_호출성공 처리 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<StockRequestDto?>, t: Throwable
                        ) {
                            Log.d("Regist", "regist_in_호출실패")
                        }
                    })
            }

            StockMode.OUT -> {
                apiService.stockOut("Bearer $token", request)
                    .enqueue(object : Callback<StockRequestDto> {
                        override fun onResponse(
                            call: Call<StockRequestDto?>,
                            response: Response<StockRequestDto?>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@StockInOutActivity,
                                    "출고 처리되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Log.d("Regist", "regist_out_호출성공 처리 실패")
                            }
                        }

                        override fun onFailure(call: Call<StockRequestDto?>, t: Throwable) {
                            Log.d("Regist", "regist_out_호출실패")
                        }

                    })
            }
        }
    }

    //    화면 나갈 때 알림창
    private fun showLeaveConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("등록을 종료하시겠습니까?")
            .setMessage("진행 중인 등록을 종료합니다.")
            .setPositiveButton("나가기") { _, _ ->
                stopScanning()
                finish()
            }
            .setNegativeButton("계속하기", null)
            .show()
    }
}