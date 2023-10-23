package dev.son.movie.adapters

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.*
import dev.son.movie.R
import dev.son.movie.data.models.Slug.Item
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class VideosController(private val onItemClick: ((Item) -> Unit)) :
    TypedEpoxyController<List<Item>>() {
    override fun buildModels(videos: List<Item>?) {
        videos?.forEachIndexed { index, it ->

        }
    }
}

@EpoxyModelClass
abstract class VideoModel : EpoxyModelWithHolder<VideoModel.VideoHolder>() {

    @EpoxyAttribute
    lateinit var video: Item

    @EpoxyAttribute
    lateinit var onClick: (Item) -> Unit

    inner class VideoHolder : EpoxyHolder() {
        lateinit var container: ConstraintLayout
        lateinit var youtubePlayerView: YouTubePlayerView
        lateinit var titleText: TextView
        override fun bindView(itemView: View) {
            container = itemView.findViewById(R.id.container)
            youtubePlayerView = itemView.findViewById(R.id.youtube_player_view)
            titleText = itemView.findViewById(R.id.title_text)
        }
    }

    override fun bind(holder: VideoHolder) {
        holder.titleText.text = video.name
        holder.youtubePlayerView.getYouTubePlayerWhenReady(object: YouTubePlayerCallback{
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                //youTubePlayer.cueVideo(video.key, 0f)
            }
        })
    }

    override fun getDefaultLayout(): Int = R.layout.item_video
}