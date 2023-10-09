package com.oceantech.tracking.adapters

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.Glide
import com.netflixclone.constants.BASE_IMG
import com.oceantech.tracking.R
import com.oceantech.tracking.data.models.Data
import com.oceantech.tracking.data.models.Items

class MainEpoxyController(private val onMediaClick: (Items) -> Unit) : AsyncEpoxyController() {
    var categories: MutableList<Data> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {

        HeaderModel_()
            .id("header")
            .data(categories[0])
            .onInfoClick { onMediaClick(categories[0]?.items!![0]) }
            .addTo(this)

        categories.mapIndexed { index, category ->
            CategoryModel_()
                .id("category_$index")
                .data(category)
                .onItemClick(onMediaClick)
                .addTo(this)
        }
    }
}

@EpoxyModelClass
abstract class HeaderModel : EpoxyModelWithHolder<HeaderModel.HeaderHolder>() {
    @EpoxyAttribute
    lateinit var data: Data

    @EpoxyAttribute
    lateinit var onInfoClick: () -> Unit

    inner class HeaderHolder : EpoxyHolder() {
        lateinit var backgroundImage: ImageView
        lateinit var genreText: TextView
        lateinit var infoButton: LinearLayout

        override fun bindView(itemView: View) {
            backgroundImage = itemView.findViewById(R.id.background_image)
            genreText = itemView.findViewById(R.id.genres_text)
            infoButton = itemView.findViewById(R.id.info_button)
        }
    }

    override fun bind(holder: HeaderHolder) {
        var posterUrl: String? = null
        posterUrl = BASE_IMG + data.items[0].thumbUrl
        Glide.with(holder.backgroundImage).load(posterUrl).into(holder.backgroundImage)

        holder.genreText.text = data.items[0].category[0].name
        val animZoomOut =
            AnimationUtils.loadAnimation(holder.backgroundImage.context, R.anim.zoom_out)
        holder.backgroundImage.startAnimation(animZoomOut)
        holder.infoButton.setOnClickListener { onInfoClick() }

    }

    override fun getDefaultLayout(): Int = R.layout.item_feed_header
}

@EpoxyModelClass
abstract class CategoryModel : EpoxyModelWithHolder<CategoryModel.FeedItemHorizontalListHolder>() {
    @EpoxyAttribute
    lateinit var data: Data

    @EpoxyAttribute
    lateinit var onItemClick: (Items) -> Unit

    inner class FeedItemHorizontalListHolder : EpoxyHolder() {
        lateinit var titleText: TextView
        lateinit var postersList: RecyclerView

        override fun bindView(itemView: View) {
            titleText = itemView.findViewById(R.id.title_text)
            postersList = itemView.findViewById(R.id.posters_list)
        }
    }

    override fun bind(holder: FeedItemHorizontalListHolder) {
        holder.titleText.text = data.titlePage
        val controller = MediaItemsController(onItemClick)
        controller.setData(data.items)
        holder.postersList.isNestedScrollingEnabled = true
        holder.postersList.adapter = controller.adapter
    }

    override fun getDefaultLayout(): Int = R.layout.item_feed_horizontal_list
}

class MediaItemsController(val onMediaClick: (Items) -> Unit) :
    TypedEpoxyController<List<Items>>() {
    override fun buildModels(it: List<Items>) {
        it.mapIndexed { index, item ->
            MediaModel_()
                .id(index)
                .items(item)
                .onClick { onMediaClick(item) }
                .addTo(this)
        }
    }
}

@EpoxyModelClass
abstract class MediaModel :
    EpoxyModelWithHolder<MediaModel.MediaHolder>() {
    @EpoxyAttribute
    lateinit var items: Items

    @EpoxyAttribute
    lateinit var onClick: () -> Unit

    inner class MediaHolder : EpoxyHolder() {
        lateinit var posterImage: ImageView

        override fun bindView(itemView: View) {
            posterImage = itemView.findViewById(R.id.poster_image)
        }
    }

    override fun bind(holder: MediaHolder) {
        var posterUrl: String? = null
        posterUrl = BASE_IMG + items.thumbUrl
        holder.posterImage.setOnClickListener { onClick() }
        Glide.with(holder.posterImage).load(posterUrl).into(holder.posterImage)
        holder.posterImage.clipToOutline = true
    }

    override fun getDefaultLayout(): Int = R.layout.item_poster
}

