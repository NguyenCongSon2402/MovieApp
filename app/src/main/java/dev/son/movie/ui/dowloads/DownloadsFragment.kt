package dev.son.moviestreamhub.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide
import dev.son.movie.core.TrackingBaseFragment
import dev.son.movie.databinding.FragmentDownloadsBinding


const val POSTER_IMAGE = "https://i.ibb.co/12fHwfg/netflix-downloads.png"

class DownloadsFragment : TrackingBaseFragment<FragmentDownloadsBinding>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        Glide.with(views.posterImage).load(POSTER_IMAGE).into(views.posterImage)
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