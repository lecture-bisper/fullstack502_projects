package bitc.full502.lostandfound.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bitc.full502.lostandfound.R
import bitc.full502.lostandfound.data.model.ChatRoomData
import com.bumptech.glide.Glide

class ChatRoomAdapter(
    private var chatRoomList: List<ChatRoomData>,
    private val myUserId: String, // 로그인한 유저 ID
    private val onItemClick: (ChatRoomData) -> Unit
) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    inner class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.name)
        val title: TextView = itemView.findViewById(R.id.title)
        val cg: TextView = itemView.findViewById(R.id.cg)
        val boardType: TextView = itemView.findViewById(R.id.lostandfound)
        val unread: TextView = itemView.findViewById(R.id.unread)
    }

    private val categoryMap = mapOf(
        0L to "전자기기",
        1L to "지갑/가방",
        2L to "신분증/카드",
        3L to "열쇠",
        4L to "의류/악세서리",
        5L to "문서/서류",
        6L to "귀중품",
        7L to "기타"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_list_row, parent, false)
        return ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val item = chatRoomList[position]

        // 이미지 로드
        Glide.with(holder.itemView.context)
            .load(item.imgUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.img)

        // 로그인 유저 기준으로 상대방 ID 표시
        val displayUserId = when {
            item.userId1 == myUserId -> item.userId2
            item.userId2 == myUserId -> item.userId1
            else -> "알수없음"
        }

        Log.d("ChatRoomAdapter", "userId1(${item.userId1}), userId2(${item.userId2}), myUserId($myUserId), 표시: $displayUserId")

        holder.name.text = displayUserId
        holder.title.text = item.title
        holder.cg.text = categoryMap[item.categoryId] ?: "기타"
        holder.boardType.text = if (item.boardType == "LOST") "분실" else "습득"

        if (item.unreadCount > 0) {
            holder.unread.visibility = View.VISIBLE
            holder.unread.text = "안읽음 ${item.unreadCount}"
        } else {
            holder.unread.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = chatRoomList.size

    fun updateData(newList: List<ChatRoomData>) {
        chatRoomList = newList
        notifyDataSetChanged()
    }
}
