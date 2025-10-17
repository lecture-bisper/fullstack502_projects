package bitc.full502.lostandfound.util

object GeocodingConstants {

    /** ----- 기본 파라미터 ----- **/
    const val DEFAULT_SOURCE_CRS = "epsg:4326"
    const val DEFAULT_ORDERS = "roadaddr,addr" // 도로명 우선, 지번 보조
    const val DEFAULT_OUTPUT = "json"

    /** ----- 좌표 처리 ----- **/
    const val COORD_PRECISION = 6 // 소수점 자릿수

    /** ----- 공급자 식별 ----- **/
    const val PROVIDER_NCP = "NCP"

    /** ----- 타임아웃(ms) ----- **/
    const val TIMEOUT_CONNECT_MS = 5000
    const val TIMEOUT_READ_MS = 5000

    /** ----- 캐시/데이터 정책 ----- **/
    const val CACHE_TTL_SUCCESS_MS = 10 * 60 * 1000L // 10분
    const val CACHE_TTL_NO_RESULT_MS = 60 * 1000L // 1분

    const val NCP_KEY= "JmtF8POl8y2aRhNkU1uHmcZEZNRYIJdcPI3mu1oG"
    const val NCP_KEY_ID= "wokyo79tgl"

    //    base url
    // Reverse Geocoding (좌표 → 주소)
    const val REVERSE_GEOCODE_URL = "https://maps.apigw.ntruss.com/map-reversegeocode/v2/"


    // Geocoding (주소 → 좌표)
    const val GEOCODE_URL = "https://maps.apigw.ntruss.com/map-geocode/v2/"
}