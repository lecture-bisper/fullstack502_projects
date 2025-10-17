package bitc.full502.app_bq.data.model


data class ItemDto(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val code: String = "",
    val name: String,
    val manufacturer: String,
    val price: Long,
    val addDate: String?,  // 앱에서는 String으로 받는게 편합니다
    val addUser: String,
    val approveUser: String = "",
    val status: String?
)