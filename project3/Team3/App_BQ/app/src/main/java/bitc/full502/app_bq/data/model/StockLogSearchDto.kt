package bitc.full502.app_bq.data.model

import java.time.LocalDate

data class StockLogSearchDto(
    var nameOrCode: String? = null,
    var manufacturer: String? = null,
    var empCodeOrEmpName: String? = null,
    var type: String? = null,
    var warehouseId: Long? = null,
    var categoryId: Long? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var keyword: String? = null
)
