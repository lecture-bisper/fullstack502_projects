package bitc.full502.app_bq.data.model

import java.time.LocalDateTime

data class ItemSearchDto(
    val name: String? = null,
    val code: String? = null,
    val manufacturer: String? = null,
    val categoryId: Long? = null,
    val minPrice: Long? = null,   // 서버에서는 null로 보내도 됨
    val maxPrice: Long? = null,  // 서버에서는 null로 보내도 됨
)
