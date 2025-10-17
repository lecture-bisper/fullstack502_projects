package bitc.full502.app_bq.data.model

data class MinStockDto(
    val id: Long,
    val itemId: Long,
    val itemName: String,
    val itemCode: String,
    val itemManufacturer: String,
    val itemPrice: Long,
    val categoryId: Long,
    val categoryName: String,
    val categoryKrName: String,
    val stockQuantity: Long,
    val standardQty: Long,
    val safetyQty: Long,
    val minStockStatus: String
)