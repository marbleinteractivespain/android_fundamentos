package io.keepcoding.chat.channels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.keepcoding.chat.Channel
import io.keepcoding.chat.R
import io.keepcoding.chat.Repository
import io.keepcoding.chat.conversation.ConversationActivity
import io.keepcoding.chat.databinding.ActivityChannelsBinding

class ChannelsActivity : AppCompatActivity() {

	val binding: ActivityChannelsBinding by lazy { ActivityChannelsBinding.inflate(layoutInflater) }
	val channelsAdapter: ChannelsAdapter by lazy { ChannelsAdapter(::openChannel) }
	val vm: ChannelsViewModel by viewModels {
		ChannelsViewModel.ChannelsViewModelProviderFactory(Repository)
	}

	lateinit var swipeContainer: SwipeRefreshLayout

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		 swipeContainer = findViewById(R.id.swipeContainer)

		 swipeContainer.setOnRefreshListener {
			println("DV: swipe refresh")
			vm.loadChannels()
		}
		// Configure the refreshing colors
		 swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
			android.R.color.holo_green_light,
			android.R.color.holo_orange_light,
			android.R.color.holo_red_light);


		binding.progressBar.isVisible = true
		binding.emptyViewNoDataChannels.isVisible = false

		binding.topics.apply {
			adapter = channelsAdapter
			addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
		}
		vm.state.observe(this) {
			when (it) {
				is ChannelsViewModel.State.ChannelsReceived -> {
					channelsAdapter.submitList(it.channels)
					swipeContainer.isRefreshing = false
					binding.emptyViewNoDataChannels.isVisible = false
					hideLoading()
				}
				is ChannelsViewModel.State.Error.ErrorLoading -> {
					Toast.makeText(this, "Ups! Error Loading Channels", Toast.LENGTH_SHORT).show()
					swipeContainer.isRefreshing = false
					binding.emptyViewNoDataChannels.isVisible = true
					hideLoading()
				}
				is ChannelsViewModel.State.Error.ErrorWithChannels -> {
					Toast.makeText(this, "Ups! Error With Channels", Toast.LENGTH_SHORT).show()
					channelsAdapter.submitList(it.channels)
					swipeContainer.isRefreshing = false
					hideLoading()
				}
				is ChannelsViewModel.State.LoadingChannels.Loading -> {
					showLoading()
					swipeContainer.isRefreshing = false
					binding.emptyViewNoDataChannels.isVisible = false
				}
				is ChannelsViewModel.State.LoadingChannels.LoadingWithChannels -> {
					channelsAdapter.submitList(it.channels)
				}
			}
		}
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
		vm.loadChannels()
	}

	private fun openChannel(channel: Channel) {
		startActivity(ConversationActivity.createIntent(this, channel))
	}


}