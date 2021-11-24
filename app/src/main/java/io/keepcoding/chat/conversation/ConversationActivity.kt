package io.keepcoding.chat.conversation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import io.keepcoding.chat.Channel
import io.keepcoding.chat.Message
import io.keepcoding.chat.Repository
import io.keepcoding.chat.common.TextChangedWatcher
import io.keepcoding.chat.databinding.ActivityConversationBinding

class ConversationActivity : AppCompatActivity() {

	private val binding: ActivityConversationBinding by lazy {
		ActivityConversationBinding.inflate(layoutInflater)
	}
	private val vm: ConversationViewModel by viewModels {
		ConversationViewModel.ConversationViewModelProviderFactory(Repository)
	}
	private val messagesAdapter: MessagesAdapter = MessagesAdapter()
	private val channelId: String by lazy { intent.getStringExtra(CHANNEL_ID)!! }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		binding.progressBar.isVisible = true
		binding.emptyViewNoData.isVisible = false
		binding.sendButton.isEnabled = false
		binding.sendButton.alpha = 0.4F

		binding.conversation.apply {
			layoutManager = LinearLayoutManager(context).apply {
				stackFromEnd = true
			}
			adapter = messagesAdapter
		}
		vm.state.observe(this) {
			when (it) {
				is ConversationViewModel.State.MessagesReceived -> {
					renderMessages(it.messages)
					binding.emptyViewNoData.isVisible = false
					hideLoading()
				}
				is ConversationViewModel.State.Error.ErrorLoading -> {
					Toast.makeText(this, "Ups! Error Loading", Toast.LENGTH_LONG).show()
					binding.emptyViewNoData.isVisible = true
					hideLoading()
				}
				is ConversationViewModel.State.Error.ErrorWithMessages -> {
					println("DV: State.Error.ErrorWithMessages")
					Toast.makeText(this, "Ups! Error Loading Messages", Toast.LENGTH_LONG).show()
					renderMessages(it.messages)
					hideLoading()
				}
				is ConversationViewModel.State.LoadingMessages.Loading -> {
					binding.emptyViewNoData.isVisible = false
					showLoading()
				}
				is ConversationViewModel.State.LoadingMessages.LoadingWithMessages -> {
					renderMessages(it.messages)
				}
			}
		}
		vm.message.observe(this) {
			binding.tvMessage.apply {
				setText(it)
				setSelection(it.length)

				binding.sendButton.isEnabled = it.length > 0
				if(it.length > 0) {
					binding.sendButton.alpha = 1F
					binding.sendButton.setColorFilter(Color.argb(100, 0, 177, 255));
				}else{
					binding.sendButton.alpha = 0.4F
					binding.sendButton.setColorFilter(Color.argb(100, 153, 153, 153));
				}
			}
		}
		binding.tvMessage.addTextChangedListener(TextChangedWatcher(vm::onInputMessageUpdated))
		binding.sendButton.setOnClickListener { vm.sendMessage(channelId) }
	}

	private fun renderMessages(messages: List<Message>) {
		messagesAdapter.submitList(messages) { binding.conversation.smoothScrollToPosition(messages.size) }
	}

	private fun showLoading() {
		println("DV: Progressbar TRUE")
		binding.progressBar.isVisible = true
	}

	private fun hideLoading() {
		println("DV: Progressbar FALSE")
		binding.progressBar.isVisible = false
	}

	override fun onResume() {
		super.onResume()
		vm.loadConversation(channelId)
	}

	companion object {
		const val CHANNEL_ID = "CHANNEL_ID"

		fun createIntent(context: Context, channel: Channel): Intent =
			Intent(
				context,
				ConversationActivity::class.java
			).apply {
				putExtra(CHANNEL_ID, channel.id)
			}
	}
}