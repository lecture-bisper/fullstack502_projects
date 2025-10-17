package bitc.full502.app_bq.data.model

data class StockRequestDto(

    val code: String,
    val remark: String?,
    val warehouseId: Long,
    val quantity: Long
)
