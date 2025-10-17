package bitc.full502.lostandfound.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import bitc.full502.lostandfound.data.model.BoardData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker

/**
 * GMS 없이 동작. 앱 시작 시 '마지막 알려진 위치'로 카메라를 한 번만 이동한다.
 * - 추적 모드/리스너 없음
 * - 권한 없으면 요청, 승인되면 1회 센터링
 */
class MapHelper(
    private val activity: Activity,
    private val fragmentManager: FragmentManager,
    @IdRes private val mapContainerId: Int,
    private val ncpApiKey: String,
    private val locationRequestCode: Int = 1000
) {
    private var naverMap: NaverMap? = null
    private val infoWindow = InfoWindow()
    private var textProvider: ((BoardData) -> CharSequence)? = null
    private var onNavigateToDetail: ((BoardData) -> Unit)? = null

    fun init(onReady: (NaverMap) -> Unit = {}) {
        // 1) NCP 키
        NaverMapSdk.getInstance(activity).client = NaverMapSdk.NcpKeyClient(ncpApiKey)

        // 2) MapFragment 부착 (KTX 없이 즉시 커밋)
        val mapFragment = (fragmentManager.findFragmentById(mapContainerId) as? MapFragment)
            ?: MapFragment.newInstance().also {
                fragmentManager.beginTransaction()
                    .replace(mapContainerId, it)
                    .commitNow()
            }

        // 3) 맵 준비
        mapFragment.getMapAsync { map ->
            naverMap = map
//            map.uiSettings.isLocationButtonEnabled = true   // 내 위치 버튼 , 일단 안쓸거라 주석처리함
            onReady(map)
            centerToMyLastKnown(map)                        // 한 번만 센터링
        }
    }

    /** 권한 확인 → 없으면 요청, 있으면 lastKnown으로 1회 카메라 이동 */
    private fun centerToMyLastKnown(map: NaverMap) {
        val fineGranted = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationRequestCode
            )
            return
        }

        val lm = activity.getSystemService(LocationManager::class.java)
        val best: Location? = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        ).mapNotNull { p ->
            try { lm.getLastKnownLocation(p) } catch (_: SecurityException) { null }
        }.maxByOrNull { it.time }

        val target = best?.let { LatLng(it.latitude, it.longitude) }
            ?: LatLng(37.5665, 126.9780) // fallback: 서울시청 (원하면 변경)

        map.moveCamera(CameraUpdate.scrollTo(target))
        map.locationOverlay.apply {
            position = target
            isVisible = true
        }
    }

    /** Activity.onRequestPermissionsResult에서 그대로 위임 */
    fun onRequestPermissionsResult(
        requestCode: Int,
        @Suppress("UNUSED_PARAMETER") permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != locationRequestCode) return
        if (grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
            naverMap?.let { centerToMyLastKnown(it) }  // 권한 승인 뒤 즉시 1회 센터링
        }
    }

    fun bindMap(map: NaverMap) {
        naverMap = map
        map.setOnMapClickListener { _, _ -> infoWindow.close() }
    }

    fun getMap(): NaverMap? = naverMap

}
