package bitc.full502.lostandfound.data.model

data class JoinData(
    var userId: String,
    var password: String,
    var phone: String,
    var email: String,
    var userName: String,
    var createDate: String?,
    var role: String,
    var autoLogin: Boolean
)
