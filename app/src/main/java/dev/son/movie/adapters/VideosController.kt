package dev.son.movie.adapters

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getDrawable
import com.airbnb.epoxy.*
import com.bumptech.glide.Glide
import dev.son.movie.R
import dev.son.movie.network.models.Slug.Item
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import de.hdodenhof.circleimageview.CircleImageView
import dev.son.movie.network.models.postcomment.UserIdComment

class VideosController :
    TypedEpoxyController<MutableList<UserIdComment>>() {
    override fun buildModels(comment: MutableList<UserIdComment>?) {
        if (comment.isNullOrEmpty()) {
            EmptyModel_()
                .id("emptyModel")
                .addTo(this)
        } else {
            comment.forEachIndexed { index, it ->
                it.let { data ->
                    VideoModel_()
                        .id("$index")
                        .comment(data)
                        .addTo(this)
                }
            }
        }
    }
}

@EpoxyModelClass
abstract class VideoModel : EpoxyModelWithHolder<VideoModel.VideoHolder>() {

    @EpoxyAttribute
    lateinit var comment: UserIdComment

    inner class VideoHolder : EpoxyHolder() {
        lateinit var img_user: CircleImageView
        lateinit var user_name: TextView
        lateinit var comment: TextView
        lateinit var hour: TextView
        override fun bindView(itemView: View) {
            img_user = itemView.findViewById(R.id.img_user)
            user_name = itemView.findViewById(R.id.user_name)
            comment = itemView.findViewById(R.id.comment)
            hour = itemView.findViewById(R.id.hour)
        }
    }

    override fun bind(holder: VideoHolder) {
        holder.user_name.text = comment.name
        holder.comment.text = comment.text
        holder.hour.text = comment.timestamp
        Glide.with(holder.img_user).load(comment.avatar).centerCrop()
           .into(holder.img_user)
    }

    override fun getDefaultLayout(): Int = R.layout.item_comment
}

@EpoxyModelClass
abstract class EmptyModel : EpoxyModelWithHolder<EmptyModel.EmptyHolder>() {
    inner class EmptyHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {}
    }

    override fun bind(holder: EmptyHolder) {}

    override fun getDefaultLayout(): Int = R.layout.item_empty
}