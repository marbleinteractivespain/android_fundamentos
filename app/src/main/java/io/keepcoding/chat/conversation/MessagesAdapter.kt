package io.keepcoding.chat.conversation

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.chat.Message
import io.keepcoding.chat.Repository
import io.keepcoding.chat.common.getDateTime
import io.keepcoding.chat.databinding.ViewMessageBinding
import io.keepcoding.chat.databinding.ViewMessageRecieveBinding
import io.keepcoding.chat.extensions.inflater

class MessagesAdapter(
	diffUtilCallback: DiffUtil.ItemCallback<Message> = DIFF
) : ListAdapter<Message, RecyclerView.ViewHolder>(diffUtilCallback) {

	private val MSG_SENDER = 0
	private val MSG_RECIEVER = 1

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
		return when(viewType){
			MSG_SENDER -> MessageViewHolder(parent)
			else -> MessageViewRecieveHolder(parent)
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (holder){
			is MessageViewHolder -> holder.bind(getItem(position))
			is MessageViewRecieveHolder -> holder.bind(getItem(position))
		}
	}

	override fun getItemViewType(position: Int): Int {
		val msg = currentList[position]
		val idSender = Repository.currentSender.id

		return when(msg.sender.id == idSender){
			true -> MSG_SENDER
			else -> MSG_RECIEVER
		}
	}

	companion object {

		val DIFF = object : DiffUtil.ItemCallback<Message>() {
			override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
				oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
				oldItem == newItem
		}
	}

	class MessageViewHolder(
		parent: ViewGroup,
		private val binding: ViewMessageBinding = ViewMessageBinding.inflate(
			parent.inflater,
			parent,
			false
		)
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(message: Message) {
			binding.userImageProfile.setBackgroundResource(message.sender.profileImageRes)
			binding.textUserName.text = "${message.sender.name}:"
			binding.channelName.text = "${message.text}"
			binding.textDateMessage.text = getDateTime(message.timestamp)
		}
	}

	class MessageViewRecieveHolder(
		parent: ViewGroup,
		private val binding: ViewMessageRecieveBinding = ViewMessageRecieveBinding.inflate(
			parent.inflater,
			parent,
			false
		)
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(message: Message) {
			binding.userImageProfile.setBackgroundResource(message.sender.profileImageRes)
			binding.textUserName.text = "${message.sender.name}:"
			binding.channelName.text = "${message.text}"
			binding.textDateMessage.text = getDateTime(message.timestamp)
		}
	}

}