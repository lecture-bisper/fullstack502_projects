package bitc.full502.lostandfound.data.model

data class ItemData(
    val boardId: Long,
    var title: String,
    var ownerName: String,
    var eventDate: String?,
    var status: String,
    var type: String,
    var userId: String,
    var createDate: String?

)