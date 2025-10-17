package bitc.fullstack502.android_studio.model

import com.google.gson.annotations.SerializedName

data class LodgingItem(
    val id: Long,
    val name: String,
    val city: String?,
    val town: String?,
    val addrRd: String?,
    val basePrice: Long,
    val img: String?
)
