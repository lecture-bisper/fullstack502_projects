package bitc.full502.lostandfound.data.model

import com.google.gson.Gson


data class ChatData(
    var roomIdx: Long?,
    var sender: String?,
    var target: String,
    var message: String,
    var sendDate: String,
    var status: String
) {
    fun toJson(): String = Gson().toJson(this)
    companion object {
        fun fromJson(json: String): ChatData = Gson().fromJson(json, ChatData::class.java)
    }
}
