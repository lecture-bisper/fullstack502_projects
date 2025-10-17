package bitc.full502.app_bq.data.model

data class OrderRequestDto(
    val itemId: Long,
    val name: String,
    val code: String,
    val manufacturer: String,
    val category: String,
    val requestQty: Long,
    val price: Long,
    val requestUser: String
)
