package bitc.full502.lostandfound.util

object GeoConstants {

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


}