package bitc.full502.app_bq.data.model


data class StockDto(
    val id: Long,
    val itemName: String,
    val itemCode: String,
    val manufacturer: String,
    val quantity: Long,
    val categoryName: String,
    val warehouseName: String,
    val warehouseKrName: String
)