package com.oceantech.tracking.model

import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
const val POSTER_IMAGE1 = "https://img.ophim9.cc/uploads/movies/su-thi-cua-akkiz-thumb.jpg"
@EpoxyModelClass(layout = R.layout.item_feed_header)
abstract class HeaderItemModel : EpoxyModelWithHolder<HeaderItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var title1: String

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.title.text = title1
        Glide.with(holder.background_image).load(POSTER_IMAGE1).into(holder.background_image)
        val animZoomOut =
            AnimationUtils.loadAnimation(holder.background_image.context, R.anim.zoom_out)
        holder.background_image.startAnimation(animZoomOut)

    }

    class Holder : EpoxyHolder() {


        lateinit var title: AppCompatTextView
        lateinit var background_image: AppCompatImageView

        override fun bindView(itemView: View) {
            title = itemView.findViewById(R.id.genres_text)
            background_image = itemView.findViewById(R.id.background_image)
        }
    }
}