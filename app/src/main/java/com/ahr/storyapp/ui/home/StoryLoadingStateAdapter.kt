package com.ahr.storyapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ahr.storyapp.databinding.ItemLoadingBinding

class StoryLoadStateAdapter : LoadStateAdapter<StoryLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(private val binding: ItemLoadingBinding) : ViewHolder(binding.root) {
        fun bind(state: LoadState) {
            when (state) {
                is LoadState.Loading -> binding.root.isVisible = true
                is LoadState.Error -> {
                    binding.root.isVisible = false
                    state.error.localizedMessage?.let { onLoadingStateCallback?.onLoadError(it) }
                }
                is LoadState.NotLoading -> binding.root.isVisible = false
            }
        }
    }

    private var onLoadingStateCallback: OnLoadingStateCallback? = null

    fun setOnLoadingStateCallback(onLoadingStateCallback: OnLoadingStateCallback) {
        this.onLoadingStateCallback = onLoadingStateCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    interface OnLoadingStateCallback {
        fun onLoadError(message: String)
    }
}