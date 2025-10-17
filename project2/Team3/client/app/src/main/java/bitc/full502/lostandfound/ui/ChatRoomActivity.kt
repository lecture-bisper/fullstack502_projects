package bitc.full502.lostandfound.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import bitc.full502.lostandfound.R
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.AuthService
import bitc.full502.lostandfound.data.api.ChatService
import bitc.full502.lostandfound.data.model.ChatRoomData
import bitc.full502.lostandfound.data.model.UserData
import bitc.full502.lostandfound.databinding.ActivityChatRoomBinding
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.ui.adapter.ChatRoomAdapter
import bitc.full502.lostandfound.util.Constants
import bitc.full502.lostandfound.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRoomActivity : AppCompatActivity() {

    private val binding by lazy { ActivityChatRoomBinding.inflate(layoutInflater) }
    private val chatApi by lazy { ApiClient.createJsonService(Constants.BASE_URL, ChatService::class.java) }
    private val userApi by lazy { ApiClient.createJsonService(Constants.BASE_URL, AuthService::class.java) }
    private val tokenManager: TokenManager by lazy { TokenManager(this) }

    private lateinit var adapter: ChatRoomAdapter
    private val chatRoomList = mutableListOf<ChatRoomData>()

    private var myUserId: String = "" // 로그인 유저 ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchUserInfo()
        binding.topBar.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun onResume() {
        super.onResume()

        (application as MyApplication).setCurrentActivity(this)
        fetchUserInfo()
    }

    override fun onPause() {
        super.onPause()

        (application as MyApplication).setCurrentActivity(null)
    }

    // 서버에서 로그인 유저 정보 가져오기
        fun fetchUserInfo() {
        val token = "Bearer ${tokenManager.getToken() ?: return}"

        userApi.getUserInfo(token).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {
                    myUserId = response.body()?.userId ?: ""
                    Log.d("ChatRoomActivity", "로그인 유저 ID: $myUserId")
                    tokenManager.saveUserId(myUserId)
                    initRecyclerView()
                    fetchChatRooms()
                } else {
                    Toast.makeText(this@ChatRoomActivity, "유저 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                    Log.d("ChatRoomActivity", "유저 정보 실패 코드: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Toast.makeText(this@ChatRoomActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                Log.d("ChatRoomActivity", "유저 정보 통신 실패: ${t.message}")
            }
        })
    }

    private fun initRecyclerView() {
        adapter = ChatRoomAdapter(chatRoomList, myUserId) { chatRoom ->
            val otherUserId =
                if (chatRoom.userId1 == myUserId) chatRoom.userId2 else chatRoom.userId1
            Log.d("ChatRoomActivity", "클릭한 상대방 ID: $otherUserId")

            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("otherUserId", otherUserId)
                putExtra("boardIdx", chatRoom.boardIdx)
            }
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchChatRooms() {
        val token = "Bearer ${tokenManager.getToken() ?: return}"

        chatApi.getAllRooms(token).enqueue(object : retrofit2.Callback<List<ChatRoomData>> {
            override fun onResponse(
                call: retrofit2.Call<List<ChatRoomData>>,
                response: retrofit2.Response<List<ChatRoomData>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        chatRoomList.clear()
                        chatRoomList.addAll(it)
                        adapter.notifyDataSetChanged()

                        // 👇 데이터가 없을 때 텍스트 표시
                        if (chatRoomList.isEmpty()) {
                            binding.emptyView.visibility = android.view.View.VISIBLE
                            binding.recyclerView.visibility = android.view.View.GONE
                        } else {
                            binding.emptyView.visibility = android.view.View.GONE
                            binding.recyclerView.visibility = android.view.View.VISIBLE
                        }

                        Log.d("ChatRoomActivity", "채팅방 수: ${chatRoomList.size}")
                    }
                } else {
                    Toast.makeText(this@ChatRoomActivity, "채팅방 불러오기 실패", Toast.LENGTH_SHORT).show()
                    Log.d("ChatRoomActivity", "채팅방 실패 코드: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<ChatRoomData>>, t: Throwable) {
                Toast.makeText(this@ChatRoomActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                Log.d("ChatRoomActivity", "채팅방 통신 실패: ${t.message}")
            }
        })
    }

}
