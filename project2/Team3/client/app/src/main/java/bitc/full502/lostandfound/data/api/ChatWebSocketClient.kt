package bitc.full502.lostandfound.data.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import bitc.full502.lostandfound.data.model.ChatData
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.MyApplication
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.URI
import kotlin.jvm.java
import kotlin.let

class ChatWebSocketClient(
    context: Context,
    serverUrl: String,
    token: String,
    private val listener: ChatListener
) : WebSocketClient(URI("$serverUrl?token=$token")) {

    private val app = context.applicationContext as MyApplication

    interface ChatListener {
        fun onConnected()
        fun onMessageReceived(sender: String?, target: String, message: String)
        fun onDisconnected()
        fun onError(ex: Exception)
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("WebSocket", "Connected")
        app.setSocketConnected(true)
        listener.onConnected()
    }

    override fun onMessage(message: String?) {
        message?.let {
            try {
                val chat = ChatData.fromJson(it)
                listener.onMessageReceived(chat.sender, chat.target, chat.message)
            } catch (e: Exception) {
                Log.e("WebSocket", "Parsing error: ${e.message}")
            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("WebSocket", "Closed: $reason")
        app.setSocketConnected(false)
        listener.onDisconnected()
    }

    override fun onError(ex: Exception?) {
        ex?.let {
            Log.e("WebSocket", "Error: ${it.message}")
            app.setSocketConnected(false)
            listener.onError(it)
        }
    }

    fun sendMessage(context: Context, chat: ChatData) {
        val tokenManager = TokenManager(context)
        val chatApi = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ChatService::class.java)
        tokenManager.getToken()?.let {
            chatApi.insertChat("Bearer $it", chat.roomIdx!!, chat)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
//                        if (response.isSuccessful) {
//                            if (response.body() == Constants.SUCCESS) {
//                                Log.d("**fullstack502**", "채팅 저장 완료")
//                            }
//                        } else Log.d("**fullstack502**", "채팅 저장 실패: ${response.code()}")
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Toast.makeText(context, "네트워크 통신 실패", Toast.LENGTH_SHORT).show()
                        Log.d("**fullstack502**", "채팅 저장 중 네트워크 통신 실패: ${t.message}")
                    }
                })

            send(chat.toJson())
        }
    }
}
