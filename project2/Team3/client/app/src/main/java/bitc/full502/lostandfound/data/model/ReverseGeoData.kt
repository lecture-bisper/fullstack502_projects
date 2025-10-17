package bitc.full502.lostandfound.data.model

import com.google.gson.annotations.SerializedName

data class ReverseGeoData(
    @SerializedName("status") val status: Status,
    @SerializedName("results") val results: List<Result>
)

// 개별 결과
data class Result(
//    변환 타입
    @SerializedName("name")   val name: String,
//    코드 정보
    @SerializedName("code")   val code: Code,
//    	주소 정보 , 이거 이하 area에 도 시 군 읍 순으로 쭉 들어감 동일한 트리위치
    @SerializedName("region") val region: Region,
//    	상세 주소 정보 ,이거 우편번호?아니면 지번인듯
    @SerializedName("land")   val land: Land? = null
)

// 코드 블록
data class Code(
//    이거 별 필요 없긴 함
    @SerializedName("id")         val id: String,
    @SerializedName("type")       val type: String,
    @SerializedName("mappingId")  val mappingId: String
)

// 지번(land) 블록
//숫자 하나씩 지정되어 있음
data class Land(
    @SerializedName("type")     val type: String,
    @SerializedName("number1")  val number1: String,
    @SerializedName("number2")  val number2: String,
    @SerializedName("addition0") val addition0: Addition,
    @SerializedName("addition1") val addition1: Addition,
    @SerializedName("addition2") val addition2: Addition,
    @SerializedName("addition3") val addition3: Addition,
    @SerializedName("addition4") val addition4: Addition,
    @SerializedName("coords")   val coords: Coords,
    @SerializedName("name")     val name: String? = null
)

// 부가 정보
data class Addition(
    @SerializedName("type")  val type: String,
    @SerializedName("value") val value: String
)

// 좌표 컨테이너
data class Coords(
    @SerializedName("center") val center: Center
)

// 중심 좌표
data class Center(
    // 문자열 대신 enum으로 안전하게 파싱(미지의 값이 오면 Gson은 예외를 던짐)
    // 새로운 CRS 값이 올 수 있다면 String으로 두는 걸 권장: val crs: String
    @SerializedName("crs") val crs: Crs,
    @SerializedName("x")   val x: Double,
    @SerializedName("y")   val y: Double
) {
    // 편의 프로퍼티 (경/위도 접근)
    val longitude: Double get() = x
    val latitude: Double  get() = y
}

// CRS enum (필요 시 값 추가)
enum class Crs {
    @SerializedName("")          EMPTY,
    @SerializedName("EPSG:4326") EPSG_4326
}

// 행정구역 정보
data class Region(
    @SerializedName("area0") val area0: Area,
    @SerializedName("area1") val area1: Area1,
    @SerializedName("area2") val area2: Area,
    @SerializedName("area3") val area3: Area,
    @SerializedName("area4") val area4: Area
)
//찐주소 상위
data class Area(
    @SerializedName("name")  val name: String,
    @SerializedName("coords") val coords: Coords
)
//이게 찐주소
data class Area1(
    @SerializedName("name")   val name: String,
    @SerializedName("coords") val coords: Coords,
//    이거 전라남도 -> 전남 으로 해주는거
    @SerializedName("alias")  val alias: String
)

// 상태 블록
data class Status(
//    응답 상태 코드
    @SerializedName("code")    val code: Int,
//응답 상태 메시지
    @SerializedName("name")    val name: String,
//    응답 상태에 대한 설명
    @SerializedName("message") val message: String
)