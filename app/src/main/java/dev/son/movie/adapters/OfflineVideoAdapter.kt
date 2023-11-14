package dev.son.movie.adapters


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.util.Util
import com.netflixclone.constants.BASE_IMG
import dev.son.movie.databinding.ItemDownloadedBinding
import dev.son.movie.manager.DemoUtil
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.utils.hide
import dev.son.movie.utils.show


class OfflineVideoAdapter :
    ListAdapter<Download, OfflineVideoAdapter.DownloadedVideoViewHolder>(DownloadDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedVideoViewHolder {
        val binding =
            ItemDownloadedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DownloadedVideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadedVideoViewHolder, position: Int) {
        val download = getItem(position)
        holder.bind(download)
        if (download.state == Download.STATE_COMPLETED && download.percentDownloaded > 99f) {
            holder.itemView.setOnClickListener { it ->
                it.context.startActivity(
                    Intent(it.context, MovieDetailsActivity::class.java)
                        .putExtra(BUNDLE_SLUG, download.request.data?.let { Util.fromUtf8Bytes(it) })// slug movie
                        .putExtra(BUNDLE_NAME1,download.request.keySetId?.let { Util.fromUtf8Bytes(it) } )// slug movie
                        .putExtra(BUNDLE_URI, download.request.uri.toString())// uri movie
                )
            }
        }

        holder.binding.event.setOnClickListener {
            DemoUtil.getDownloadTracker(it.context)
                ?.toggleDownloadPopupMenu(it.context, it, download.request.uri)
        }
    }

    class DownloadedVideoViewHolder(var binding: ItemDownloadedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(download: Download) {
            var posterUrl: String? = null
            posterUrl = BASE_IMG + download.request.id
            Glide.with(binding.poster.backdropImage).load(posterUrl)
                .centerCrop()
                .into(binding.poster.backdropImage)

            val nameMovie = download.request.keySetId?.let { Util.fromUtf8Bytes(it) }
            when (download.state) {
                Download.STATE_COMPLETED -> {
                    binding.movieName.text = nameMovie
                    binding.loading.hide()
                    binding.imgDownloaded.show()
                }

                Download.STATE_DOWNLOADING -> {
                    binding.movieName.text = nameMovie
                    binding.loading.show()
                    binding.imgDownloaded.hide()
                    binding.loading.text =
                        "Đang tải xuống...%.1f%%".format(download.percentDownloaded)

                }

                Download.STATE_QUEUED -> {
                    binding.movieName.text = nameMovie
                    binding.loading.show()
                    binding.imgDownloaded.hide()
                    binding.loading.text = "Đang đợi để tải xuống"
                }

                Download.STATE_STOPPED -> {
                    binding.movieName.text = nameMovie
                    binding.loading.show()
                    binding.imgDownloaded.hide()
                    binding.loading.text = "Đã tạm dừng tải xuống"
                }

                Download.STATE_FAILED -> {
                    binding.movieName.text = nameMovie
                    binding.loading.show()
                    binding.imgDownloaded.hide()
                    binding.loading.text = "lỗi khi tải"
                }
            }
        }
    }

    object DownloadDiffCallback : DiffUtil.ItemCallback<Download>() {
        override fun areItemsTheSame(oldItem: Download, newItem: Download): Boolean {
            return oldItem.request == newItem.request
        }

        override fun areContentsTheSame(oldItem: Download, newItem: Download): Boolean {
            return false
        }

        override fun getChangePayload(oldItem: Download, newItem: Download): Any {
            val diffBundle = Bundle()

            diffBundle.putInt(BUNDLE_STATE, newItem.state)
            diffBundle.putFloat(BUNDLE_PERCENTAGE, newItem.percentDownloaded)
            diffBundle.putLong(BUNDLE_BYTES_DOWNLOADED, newItem.bytesDownloaded)
            return diffBundle
        }
    }

    companion object {
        private const val BUNDLE_PERCENTAGE = "bundle_percentage"
        private const val BUNDLE_STATE = "bundle_state"
        private const val BUNDLE_ID = "id"
        private const val BUNDLE_URI = "uri"
        private const val BUNDLE_SLUG = "name"
        private const val BUNDLE_NAME1 = "name1"
        private const val BUNDLE_BYTES_DOWNLOADED = "bundle_bytes_downloaded"
    }
}
