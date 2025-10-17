package bitc.full502.lostandfound.data.model


data class ChatRoomData(
    var roomIdx: Long,
    var boardIdx: Long,
    var userId1: String,
    var userId2: String,
    var createDate: String,
    var unreadCount: Int,
    var imgUrl: String,
    var title: String,
    var categoryId: Long,
    var boardType: String,
    var updateDate: String
)
