package bitc.full502.lostandfound.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.AuthService
import bitc.full502.lostandfound.data.api.ChatService
import bitc.full502.lostandfound.data.api.ChatWebSocketClient
import bitc.full502.lostandfound.data.model.ChatData
import bitc.full502.lostandfound.data.model.ChatRoomData
import bitc.full502.lostandfound.data.model.UserData
import bitc.full502.lostandfound.databinding.ActivityChatBinding
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.ChatAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

class ChatActivity : AppCompatActivity() {

    private val binding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private val chatApi by lazy { ApiClient.createJsonService(Constants.BASE_URL, ChatService::class.java) }
    private val userApi by lazy { ApiClient.createJsonService(Constants.BASE_URL, AuthService::class.java) }
    private val tokenManager: TokenManager by lazy { TokenManager(this) }
    private lateinit var client: ChatWebSocketClient
    private val chatList = mutableListOf<ChatData>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var myUserId: String
    private lateinit var otherUserId: String
    private var boardIdx: Long = 0L
    private var roomIdx: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottom = maxOf(sys.bottom, ime.bottom)
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, bottom)
            insets
        }

        otherUserId = intent.getStringExtra("otherUserId") ?: ""
        boardIdx = intent.getLongExtra("boardIdx", 0)

        if (boardIdx <= 0) {
            Toast.makeText(this, "유효하지 않은 게시글입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        binding.topBar.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // 1. 로그인 유저 정보 가져오기
        fetchMyUserInfo()
        initClient()

    }

    private fun initRoomIdx() {
        tokenManager.getToken()?.let {
            chatApi.getChatRoom("Bearer $it", boardIdx)
                .enqueue(object : Callback<ChatRoomData> {
                    override fun onResponse(call: Call<ChatRoomData?>, response: Response<ChatRoomData?>) {
                        if (response.isSuccessful) {
                            roomIdx = response.body()!!.roomIdx
                        }
                    }

                    override fun onFailure(call: Call<ChatRoomData?>, t: Throwable) {
                        Log.d("**fullstack502**", "initRoomIdx 네트워크 통신 오류: ${t.message}")
                    }
                })
        }
    }

    private fun initClient() {
        tokenManager.getToken()?.let {
            client = ChatWebSocketClient(this, Constants.WEBSOCKET_URL, it, object : ChatWebSocketClient.ChatListener {
                override fun onConnected() {
                    Log.d("**fullstack502**", "소켓 연결 성공!")
                }

                override fun onMessageReceived(sender: String?, target: String, message: String) {
                    Log.d("**fullstack502**", "메시지 받음!")

                    runOnUiThread {
                        val chatData = ChatData(
                            roomIdx = roomIdx,
                            sender = sender,
                            target = otherUserId,
                            message = message,
                            sendDate = "",
                            status = ""
                        )
                        addChatMessage(chatData)
                    }
                }

                override fun onDisconnected() {
                    Log.d("**fullstack502**", "소켓 연결 끊킴")
                }

                override fun onError(ex: Exception) {
                    Log.d("**fullstack502**", "소켓 에러 발생: ${ex.message}")
                }
            })

            client.connect()
        }
    }

    private fun fetchMyUserInfo() {
        val token = "Bearer ${tokenManager.getToken() ?: return}"

        userApi.getUserInfo(token).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {
                    val user = response.body()!!
                    myUserId = user.userId

                    Log.d("DEBUG_CHAT", "로그인 유저 ID: $myUserId")

                    // 2. 리사이클러뷰 초기화
                    initRecyclerView()

                    // 3. 채팅방 내역 불러오기
                    getChatList()
                    initSendButton()
                } else {
                    Toast.makeText(this@ChatActivity, "유저 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG_CHAT", "유저 정보 통신 실패: ${t.message}")
            }
        })
    }

    private fun initRecyclerView() {
        chatAdapter = ChatAdapter(chatList, myUserId)
        binding.recyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }
    }

    private fun initSendButton() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }
    }

    private fun addChatMessage(chat: ChatData) {
        chatList.add(chat)
        chatAdapter.notifyItemInserted(chatList.size - 1)
        binding.recyclerView.scrollToPosition(chatList.size - 1) // 새 메시지 자동 스크롤
    }

    private fun getChatList() {
        val token = "Bearer ${tokenManager.getToken() ?: return}"

        chatApi.getOrCreateChatRoom(token, otherUserId, boardIdx)
            .enqueue(object : Callback<List<ChatData>> {
                override fun onResponse(call: Call<List<ChatData>?>, response: Response<List<ChatData>?>) {
                    if (response.isSuccessful) {
                        val list = response.body()
                        list?.let {
                            chatList.clear()
                            chatList.addAll(it)
                            chatAdapter.notifyDataSetChanged()
                            binding.recyclerView.scrollToPosition(chatList.size - 1)
                            initRoomIdx()
                        }
                    } else {
                        Toast.makeText(this@ChatActivity, "채팅 내역 불러오기 실패", Toast.LENGTH_SHORT).show()
                        Log.d("DEBUG_CHAT", "채팅 내역 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ChatData>?>, t: Throwable) {
                    Toast.makeText(this@ChatActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    Log.d("DEBUG_CHAT", "채팅 내역 통신 실패: ${t.message}")
                }
            })
    }

    private fun sendMessage(message: String) {
        tokenManager.getToken()?.let {
            val chatData = ChatData(
                roomIdx = roomIdx,
                sender = myUserId,
                target = otherUserId,
                message = message,
                sendDate = "",
                status = ""
            )

            binding.etMessage.setText("")
            addChatMessage(chatData)
            client.sendMessage(this, chatData)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::client.isInitialized) {
            client.close()
        }
    }
}
