package bitc.full502.app_bq.data.model

import java.time.LocalDateTime

data class StockLogDto(
    val id: Long,
    val empCode: String?,
    val empName: String?,
    val logDate: String?,
    val type: String?,
    val quantity: Long,
    val memo: String?,

    val warehouseId: Long,
    val warehouseName: String?,
    val warehouseKrName: String?,

    val itemId: Long,
    val itemCode: String?,
    val itemName: String?,
    val itemManufacturer: String?,
    val itemPrice: Long,

    val categoryId: Long,
    val categoryName: String?,
    val categoryKrName: String?
)
