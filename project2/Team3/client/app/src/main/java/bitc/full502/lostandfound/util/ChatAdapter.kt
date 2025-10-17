package bitc.full502.lostandfound.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bitc.full502.lostandfound.R
import bitc.full502.lostandfound.data.model.ChatData

class ChatAdapter(
    private val chatList: List<ChatData>,
    private val myUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MY = 0
        private const val TYPE_OTHER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].sender == myUserId) TYPE_MY else TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_MY) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_me, parent, false)
            MyViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_other, parent, false)
            OtherViewHolder(view)
        }
    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chatList[position]
        if (holder is MyViewHolder) {
            holder.tvMessage.text = chat.message
        } else if (holder is OtherViewHolder) {
            holder.tvMessage.text = chat.message
            holder.tvSenderId.text = chat.sender
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
    }

    class OtherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvSenderId: TextView = itemView.findViewById(R.id.tvSenderId)
    }
}
