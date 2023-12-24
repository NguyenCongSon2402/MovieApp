package dev.son.movie.adapters

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.*
import com.bumptech.glide.Glide
import dev.son.movie.R
import de.hdodenhof.circleimageview.CircleImageView
import dev.son.movie.network.models.postcomment.Comment
import dev.son.movie.utils.convertStringToFormattedDate

class CommentAdapter() :
    TypedEpoxyController<MutableList<Comment>>() {
    override fun buildModels(comment: MutableList<Comment>?) {
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
    lateinit var comment: Comment

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
        holder.user_name.text = comment.user?.name
        holder.comment.text = comment.comment
        holder.hour.text = convertStringToFormattedDate(comment.updatedAt.toString())
        Glide.with(holder.img_user).load(comment.user?.photoURL).centerCrop()
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