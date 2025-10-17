package bitc.full502.lostandfound.util

import bitc.full502.lostandfound.data.api.ReverseGeoService
import bitc.full502.lostandfound.data.model.ReverseGeoData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

object ReverseGeocoder {

    //    좌표 > 주소 변환 함수
    fun fetchAddress(
        service: ReverseGeoService,
        lat: Double,
        lng: Double,
        onResult: (String?) -> Unit
    ) {
        // NCP는 "lng,lat" 순서 주의!
        val coords = String.format(Locale.US, "%.6f,%.6f", lng, lat)

        service.getAddressFromCoordinate(
            KeyId = GeocodingConstants.NCP_KEY_ID,
            Key = GeocodingConstants.NCP_KEY,
            coords = coords,
            sourceCrs = "epsg:4326",
            orders = "roadaddr,addr,admcode,legalcode",
            output = "json"
        ).enqueue(object : Callback<ReverseGeoData> {
            override fun onResponse(call: Call<ReverseGeoData>, resp: Response<ReverseGeoData>) {
                if (!resp.isSuccessful) {
                    onResult(null); return
                }
                val base = Formatter.buildAddressFromReverse(resp.body())
                onResult(base)
            }

            override fun onFailure(call: Call<ReverseGeoData>, t: Throwable) {
                onResult(null)
            }
        })
    }

    /** 기본주소 + 상세주소 합치기 */
    fun mergeBaseAndDetail(base: String?, detail: String?): String =
        listOfNotNull(base?.trim(), detail?.trim())
            .filter { it.isNotEmpty() }
            .joinToString(" ")
}