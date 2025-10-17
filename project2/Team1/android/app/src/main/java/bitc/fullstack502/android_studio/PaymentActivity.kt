package bitc.fullstack502.android_studio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bitc.fullstack502.android_studio.model.Flight
import bitc.fullstack502.android_studio.model.Passenger
import bitc.fullstack502.android_studio.model.BookingRequest
import bitc.fullstack502.android_studio.model.BookingResponse
import bitc.fullstack502.android_studio.network.ApiProvider
import bitc.fullstack502.android_studio.util.AuthManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import bitc.fullstack502.android_studio.ui.ChatListActivity
import bitc.fullstack502.android_studio.ui.MainActivity
import bitc.fullstack502.android_studio.ui.lodging.LodgingSearchActivity
import bitc.fullstack502.android_studio.ui.mypage.LoginActivity
import bitc.fullstack502.android_studio.ui.mypage.MyPageActivity
import bitc.fullstack502.android_studio.ui.post.PostListActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class PaymentActivity : AppCompatActivity() {

    private fun randomSeat(): String {
        val row = (10..45).random()
        val col = ('A'..'F').random()
        return "$row$col"
    }
    private fun randomGate(): String {
        val n = (1..40).random()
        val wing = listOf("A","B","C").random()
        return "${n}${wing}"
    }

    // 1인/편도 기준 단가
    private fun perAdultOneWay(): Int =
        FlightReservationActivity.ADULT_PRICE +
                FlightReservationActivity.FUEL_SURCHARGE +
                FlightReservationActivity.FACILITY_FEE

    private fun perChildOneWay(): Int {
        val childFare = FlightReservationActivity.ADULT_PRICE - 20_000
        return childFare +
                FlightReservationActivity.FUEL_SURCHARGE +
                FlightReservationActivity.FACILITY_FEE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)


        /////////////////////////////////////
        // ✅ Drawer & NavigationView
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        // ✅ 공통 헤더 버튼 세팅
        val header = findViewById<View>(R.id.header)
        val btnBack: ImageButton = header.findViewById(R.id.btnBack)
        val imgLogo: ImageView   = header.findViewById(R.id.imgLogo)
        val btnMenu: ImageButton = header.findViewById(R.id.btnMenu)

        btnBack.setOnClickListener { finish() }  // 뒤로가기
        imgLogo.setOnClickListener {             // 로고 → 메인으로
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        btnMenu.setOnClickListener {             // 햄버거 → Drawer 열기
            drawer.openDrawer(GravityCompat.END)
        }

        // 드로어 헤더 인사말 세팅 (로그인 상태 반영)
        updateHeader(navView)

        // ✅ Drawer 메뉴 클릭 처리
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_hotel -> {
                    startActivity(Intent(this, LodgingSearchActivity::class.java)); true
                }
                R.id.nav_board -> {
                    startActivity(Intent(this, PostListActivity::class.java)); true
                }
                R.id.nav_chat -> {
                    startActivity(Intent(this, ChatListActivity::class.java)); true
                }
                R.id.nav_flight -> {
                    // 현재 FlightReservationActivity니까 따로 이동 안 해도 됨
                    true
                }
                else -> false
            }.also { drawer.closeDrawers() }
        }

        //////////////////////////////////////////////////////////////////////////////////////

        // === View refs ===
        val tvTotal    = findViewById<TextView>(R.id.tvTotalPrice)
        val tvFuel     = findViewById<TextView>(R.id.tvFuel)
        val tvFacility = findViewById<TextView>(R.id.tvFacility)
        val tvBase     = findViewById<TextView>(R.id.tvBaseFare)
        val rbKakao    = findViewById<RadioButton>(R.id.rbKakaoPay)
        val btnPay     = findViewById<Button>(R.id.btnPay)

        // --- 여정/승객 ---
        val outFlight  = intent.getSerializableExtra(FlightReservationActivity.EXTRA_OUTBOUND) as? Flight
        val inFlight   = intent.getSerializableExtra(FlightReservationActivity.EXTRA_INBOUND)  as? Flight
        val isRoundTrip = inFlight != null

        val adults   = intent.getIntExtra(FlightReservationActivity.EXTRA_ADULT, 1)
        val children = intent.getIntExtra(FlightReservationActivity.EXTRA_CHILD, 0)
        val infants  = intent.getIntExtra(FlightReservationActivity.EXTRA_INFANT, 0)
        val paxTotal = adults + children + infants

        @Suppress("UNCHECKED_CAST")
        val passengers = intent.getSerializableExtra("PASSENGERS") as? ArrayList<Passenger>
        val mainPaxName = passengers?.firstOrNull()?.displayName().orEmpty()

        // --- 금액(넘어온 값이 없으면 재계산) ---
        var totalX = intent.getIntExtra("EXTRA_TOTAL", 0)
        var base   = intent.getIntExtra("EXTRA_BASE", 0)
        var fuel   = intent.getIntExtra("EXTRA_FUEL", 0)
        var fac    = intent.getIntExtra("EXTRA_FACILITY", 0)

        val segments = if (isRoundTrip) 2 else 1
        if (totalX == 0 || base == 0 || fuel == 0 || fac == 0) {
            val chargeable = adults + children // 유아 0원
            base = adults * FlightReservationActivity.ADULT_PRICE +
                    children * (FlightReservationActivity.ADULT_PRICE - 20_000)
            fuel = chargeable * FlightReservationActivity.FUEL_SURCHARGE
            fac  = chargeable * FlightReservationActivity.FACILITY_FEE
            base *= segments; fuel *= segments; fac *= segments
            totalX = (adults * perAdultOneWay() + children * perChildOneWay()) * segments
        }

        tvBase.text     = "항공운임: %,d원".format(base)
        tvFuel.text     = "유류할증료: %,d원".format(fuel)
        tvFacility.text = "공항시설사용료: %,d원".format(fac)
        tvTotal.text    = "총 결제금액: %,d원".format(totalX)

        // --- 출발일(가는 편) 안전 수신  ---
        // ※ FlightReservationActivity.EXTRA_DATE 는 존재하지 않으므로 절대 참조하지 말 것
        val outDateYmd: String? =
            intent.getStringExtra(PassengerInputActivity.EXTRA_OUT_DATE) // 권장 키
                ?: intent.getStringExtra("EXTRA_OUT_DATE")               // 예비(같은 문자열)
                ?: intent.getStringExtra("selectedDate")                 // 과거 버전 대비
                ?: intent.getStringExtra("outDate")                      // 과거 버전 대비

        // --- 오는편 수신 ---
        val inDateYmd: String?  = intent.getStringExtra(PassengerInputActivity.EXTRA_IN_DATE)   // 왕복일 때만


        // --- 결제 처리 ---
        btnPay.setOnClickListener {
            Log.d("*** flight *** ", "$outFlight, $inFlight, outDateYmd : $outDateYmd")

            if (!AuthManager.isLoggedIn()) {
                Toast.makeText(this, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!rbKakao.isChecked) {
                Toast.makeText(this, "결제수단을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (outFlight == null) {
                Toast.makeText(this, "가는 편 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (outDateYmd.isNullOrBlank()) {
                Toast.makeText(this, "출발날짜를 받아오지 못했습니다. 이전 화면에서 날짜를 다시 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // DB 저장(예약 생성)
            val req = BookingRequest(
                userId = AuthManager.id(),
                outFlId = outFlight.id,
                depDate = outDateYmd,
                inFlId = inFlight?.id,
                retDate = inFlight?.let { inDateYmd },
                seatCnt = adults + children,
                adult = adults,
                child = children,
                totalPrice = totalX.toLong()
            )

            ApiProvider.api.createFlightBooking(req).enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    if (!response.isSuccessful) {
                        val msg = when (response.code()) {
                            409 -> "잔여좌석이 부족합니다. 다른 항공편을 선택해주세요."
                            400 -> "예약 정보가 올바르지 않습니다."
                            else -> "예약 저장 실패(${response.code()})"
                        }
                        Toast.makeText(this@PaymentActivity, msg, Toast.LENGTH_SHORT).show()
                        return
                    }

                    // ✅ 서버가 반환한 예약 결과에서 bookingId 확보
                    val booked = response.body()
                    if (booked == null) {
                        Toast.makeText(this@PaymentActivity, "예약 결과를 받지 못했습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // 결제 성공 화면 이동
                    startActivity(Intent(this@PaymentActivity, TicketSuccessActivity::class.java).apply {
                        putExtra("EXTRA_BOOKING_ID", booked.bookingId)
                        putExtra("EXTRA_ROUNDTRIP", isRoundTrip)
                        // 가는 편
                        putExtra("EXTRA_DEP", outFlight.dep)
                        putExtra("EXTRA_ARR", outFlight.arr)
                        putExtra("EXTRA_DATETIME", outFlight.depTime)
                        putExtra("EXTRA_FLIGHT_NO", outFlight.flNo)
                        putExtra("EXTRA_GATE", randomGate())
                        putExtra("EXTRA_SEAT", randomSeat())
                        putExtra("EXTRA_CLASS", "이코노미")
                        // 오는 편(있으면)
                        inFlight?.let { f ->
                            putExtra("EXTRA_DEP_RETURN", f.dep)
                            putExtra("EXTRA_ARR_RETURN", f.arr)
                            putExtra("EXTRA_DATETIME_RETURN", f.depTime)
                            putExtra("EXTRA_FLIGHT_NO_RETURN", f.flNo)
                            putExtra("EXTRA_GATE_RETURN", randomGate())
                            putExtra("EXTRA_SEAT_RETURN", randomSeat())
                            putExtra("EXTRA_CLASS_RETURN", "이코노미")
                        }
                        // 승객 표시
                        putExtra("PASSENGERS", passengers)
                        putExtra("EXTRA_PASSENGER", mainPaxName.ifBlank { "승객 1" })
                        putExtra("EXTRA_PAX_COUNT", paxTotal)
                    })
                    finish()
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    Toast.makeText(this@PaymentActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // ----------------- 로그인/헤더 처리 -----------------

    private fun isLoggedIn(): Boolean {
        val sp = getSharedPreferences("userInfo", MODE_PRIVATE)
        return !sp.getString("usersId", null).isNullOrBlank()
    }

    private fun currentUserName(): String {
        val sp = getSharedPreferences("userInfo", MODE_PRIVATE)
        return sp.getString("name", null) ?: sp.getString("usersId", "") ?: ""
    }

    private fun currentUserEmail(): String {
        val sp = getSharedPreferences("userInfo", MODE_PRIVATE)
        return sp.getString("email", "") ?: ""
    }

    private fun updateHeader(navView: NavigationView) {
        val header = navView.getHeaderView(0)
        val tvGreet = header.findViewById<TextView>(R.id.tvUserGreeting)
        val tvEmail = header.findViewById<TextView>(R.id.tvUserEmail)
        val btnMyPage = header.findViewById<MaterialButton>(R.id.btnMyPage)
        val btnLogout = header.findViewById<MaterialButton>(R.id.btnLogout)

        if (isLoggedIn()) {
            val name = currentUserName()
            val email = currentUserEmail()
            tvGreet.text = getString(R.string.greeting_fmt, if (name.isBlank()) "회원" else name)
            tvEmail.visibility = View.VISIBLE
            tvEmail.text = if (email.isNotBlank()) email else "로그인됨"

            btnLogout.visibility = View.VISIBLE
            btnMyPage.text = getString(R.string.mypage)
            btnMyPage.setOnClickListener {
                startActivity(Intent(this, MyPageActivity::class.java))
            }
            btnLogout.setOnClickListener {
                val sp = getSharedPreferences("userInfo", MODE_PRIVATE)
                sp.edit().clear().apply()
                Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                updateHeader(navView)
            }
        } else {
            // 비로그인: “000님” 같은 더미 표시 제거하고 “로그인”만 노출
            tvGreet.text = "로그인"
            tvEmail.visibility = View.GONE

            btnLogout.visibility = View.GONE
            btnMyPage.text = "로그인"
            btnMyPage.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}
