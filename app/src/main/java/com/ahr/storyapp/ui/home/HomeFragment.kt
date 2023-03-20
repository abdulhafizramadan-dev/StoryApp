package com.ahr.storyapp.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.ahr.storyapp.R
import com.ahr.storyapp.databinding.FragmentHomeBinding
import com.ahr.storyapp.databinding.ItemStoryBinding
import com.ahr.storyapp.domain.Story
import com.ahr.storyapp.ui.MainActivity
import com.ahr.storyapp.ui.detail.DetailActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), StoryListAdapter.OnStoryClick,
    StoryLoadStateAdapter.OnLoadingStateCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var storyListAdapter: StoryListAdapter
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(LayoutInflater.from(context), container, false)
        return _binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = arguments?.getString(MainActivity.EXTRA_TOKEN)

        setupRecyclerView()
        setupStories()
        storiesObserver()
    }

    private fun setupRecyclerView() {
        storyListAdapter = StoryListAdapter(this)
        val storyLoadStateAdapter = StoryLoadStateAdapter()
        storyLoadStateAdapter.setOnLoadingStateCallback(this)
        binding.rvStory.adapter = storyListAdapter.withLoadStateFooter(footer = storyLoadStateAdapter)
    }

    private fun setupStories() {
        homeViewModel.getStories("Bearer $token")
            .observe(viewLifecycleOwner) { pagingData ->
                storyListAdapter.submitData(lifecycle, pagingData)
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun storiesObserver() {
        storyListAdapter.addLoadStateListener { loadStates ->
            val endOfPaginationReached = loadStates.mediator?.refresh?.endOfPaginationReached
            if (endOfPaginationReached == true) {
                Toast.makeText(
                    context,
                    "Tidak ada data lagi!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val state = loadStates.mediator?.refresh
            if (state is LoadState.Error) {
                state.error.message?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { storyListAdapter.retry() }
                        .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStoryClicked(story: Story, binding: ItemStoryBinding) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_ID, story.id)
            putExtra(DetailActivity.EXTRA_NAME, story.name)
            putExtra(DetailActivity.EXTRA_TOKEN, token)
        }
        val optionsCompat = activity?.let {
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                it,
                Pair(binding.ivStory, "iv_story"),
                Pair(binding.tvStory, "tv_story")
            )
        }
        startActivity(intent, optionsCompat?.toBundle())
    }

    override fun onLoadError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { storyListAdapter.retry() }
            .show()
    }
}