package bitc.full502.app_bq.data.model

data class ResponseDto(
    var message: String,
    var data: String? = null,   // nullable로 수정
    val user: UserDto? = null   // nullable 이미 되어있음
)
