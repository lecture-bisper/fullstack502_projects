package bitc.full502.lostandfound.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardData(
    var idx: Long,
    var userId: String,
    var categoryId: Int,
    var title: String,
    var imgUrl: String?,
    var ownerName: String,
    var description: String,
    var eventDate: String?,
    var eventLat: Double,
    var eventLng: Double,
    var eventDetail: String,
    var storageLocation: String,
    var type: String,
    var status: String,
    var createDate: String?
) : Parcelable