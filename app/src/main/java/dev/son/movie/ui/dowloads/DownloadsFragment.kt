package dev.son.moviestreamhub.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.offline.Download
import dev.son.movie.adapters.OfflineVideoAdapter
import dev.son.movie.core.TrackingBaseFragment
import dev.son.movie.databinding.FragmentDownloadsBinding
import dev.son.movie.manager.DemoUtil
import dev.son.movie.manager.DownloadTracker
import dev.son.movie.ui.search.SearchActivity
import dev.son.movie.utils.setSingleClickListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DownloadsFragment : TrackingBaseFragment<FragmentDownloadsBinding>() {
    private var downloadTracker: DownloadTracker? = null
    private var downloadFlowJob2: Job? = null
    private lateinit var downloadAdapter: OfflineVideoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadTracker = DemoUtil.getDownloadTracker(requireActivity())
        views.rvDownloadedVideo.layoutManager = LinearLayoutManager(requireContext())
        downloadAdapter = OfflineVideoAdapter()
        views.rvDownloadedVideo.adapter = downloadAdapter


        views.buttonSearch.setSingleClickListener {
            startActivity(Intent(requireActivity(),SearchActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        setupUI()
    }

    private fun setupUI() {
        downloadFlowJob2 = lifecycleScope.launch {
            downloadTracker?.getAllDownloadProgressFlow()?.collect {
                val sortedDownloadList = it.sortedByDescending { it1->
                    it1.state == Download.STATE_DOWNLOADING
                }
                downloadAdapter.submitList(sortedDownloadList)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        downloadFlowJob2?.cancel()
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDownloadsBinding {
        return FragmentDownloadsBinding.inflate(inflater, container, false)
    }

    override fun onFirstDisplay() {
        //fetchData()
    }
}