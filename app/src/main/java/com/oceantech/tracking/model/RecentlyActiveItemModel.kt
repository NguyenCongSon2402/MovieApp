package com.oceantech.tracking.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.data.models.Data
import com.oceantech.tracking.data.models.Home
import com.oceantech.tracking.data.models.Items

const val APPDOMAINCDNIMAGE = "https://img.ophim9.cc/uploads/movies/"

@EpoxyModelClass(layout = R.layout.item_poster)
abstract class RecentlyActiveItemModel : EpoxyModelWithHolder<RecentlyActiveItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var items: Items

    override fun bind(holder: Holder) {
        var url = APPDOMAINCDNIMAGE + items.thumbUrl
        Glide.with(holder.poster_image).load(url)
            .into(holder.poster_image)
        holder.poster_image.clipToOutline = true
    }

    class Holder : EpoxyHolder() {

        lateinit var titleText: TextView
        lateinit var poster_image: ImageView

        override fun bindView(itemView: View) {
            //titleText = itemView.findViewById(R.id.title_text)
            poster_image = itemView.findViewById(R.id.poster_image)
        }
    }
}
