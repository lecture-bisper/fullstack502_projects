package bitc.full502.lostandfound.data.model

import com.google.gson.annotations.SerializedName

data class GeoData(
    @SerializedName("status")
    val status: String,

    @SerializedName("meta")
    val meta: Meta,

    @SerializedName("addresses")
    val addresses: List<Address>,

    @SerializedName("errorMessage")
    val errorMessage: String
)

data class Meta(
    @SerializedName("totalCount")
    val totalCount: Long,

    @SerializedName("page")
    val page: Long,

    @SerializedName("count")
    val count: Long
)

data class Address(
//    도로명
    @SerializedName("roadAddress")
    val roadAddress: String,

//    동주소
    @SerializedName("jibunAddress")
    val jibunAddress: String,

//    영어주소
    @SerializedName("englishAddress")
    val englishAddress: String,

    @SerializedName("addressElements")
    val addressElements: List<AddressElement>,

    // 이거 API에서 문자열로 줌
//    경도
    @SerializedName("x")
    val x: String,

//    위도
    @SerializedName("y")
    val y: String,

//    거리?
    @SerializedName("distance")
    val distance: Double
) {
    // 숫자로 변환
    val lngDouble: Double? get() = x.toDoubleOrNull()
    val latDouble: Double? get() = y.toDoubleOrNull()
}

data class AddressElement(
    @SerializedName("types")
    val types: List<String>,

    @SerializedName("longName")
    val longName: String,

    @SerializedName("shortName")
    val shortName: String,

    @SerializedName("code")
    val code: String
)