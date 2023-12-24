package dev.son.movie.adapters

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.Glide
import dev.son.movie.R
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.utils.getNamesByCodes
import dev.son.movie.utils.setSingleClickListener

class MainEpoxyController(private val onMediaClick: (Movie, View) -> Unit) :
    TypedEpoxyController<MutableList<ApiResponse<List<Movie>>?>>() {

    override fun buildModels(data: MutableList<ApiResponse<List<Movie>>?>) {
        data.let { categories ->
            HeaderModel_()
                .id("header")
                .data(categories.random())
                .onInfoClick(onMediaClick)
                .addTo(this)

            categories.forEachIndexed { index, category ->
                category?.let { safeCategory ->
                    CategoryModel_()
                        .id("category_$index")
                        .data(safeCategory)
                        .onItemClick(onMediaClick)
                        .addTo(this)
                }
            }
        }
    }
}


@EpoxyModelClass
abstract class HeaderModel : EpoxyModelWithHolder<HeaderModel.HeaderHolder>() {
    @EpoxyAttribute
    var data: ApiResponse<List<Movie>>? = null

    @EpoxyAttribute
    lateinit var onInfoClick: (Movie, View) -> Unit

    inner class HeaderHolder : EpoxyHolder() {
        lateinit var backgroundImage: ImageView
        lateinit var genreText: TextView
        lateinit var play_button: LinearLayout

        override fun bindView(itemView: View) {
            backgroundImage = itemView.findViewById(R.id.background_image)
            genreText = itemView.findViewById(R.id.genres_text)
            play_button = itemView.findViewById(R.id.play_button)
        }
    }

    override fun bind(holder: HeaderHolder) {
        if (data != null) {
            val movie = data?.data?.random()

            Glide.with(holder.backgroundImage).load(movie?.posterHorizontal)
                .into(holder.backgroundImage)
            val categories = getNamesByCodes(movie?.genre!!)

            holder.genreText.text = categories

            val animZoomOut =
                AnimationUtils.loadAnimation(holder.backgroundImage.context, R.anim.zoom_out)
            holder.backgroundImage.startAnimation(animZoomOut)
            holder.backgroundImage.setSingleClickListener {
                onInfoClick(movie, holder.backgroundImage)
            }
            holder.play_button.setSingleClickListener {
                onInfoClick(movie, holder.backgroundImage)
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_feed_header
}

@EpoxyModelClass
abstract class CategoryModel : EpoxyModelWithHolder<CategoryModel.FeedItemHorizontalListHolder>() {
    @EpoxyAttribute
    var data: ApiResponse<List<Movie>>? = null

    @EpoxyAttribute
    lateinit var onItemClick: (Movie, View) -> Unit

    inner class FeedItemHorizontalListHolder : EpoxyHolder() {
        lateinit var titleText: TextView
        lateinit var postersList: RecyclerView

        override fun bindView(itemView: View) {
            titleText = itemView.findViewById(R.id.title_text)
            postersList = itemView.findViewById(R.id.posters_list)
        }
    }

    override fun bind(holder: FeedItemHorizontalListHolder) {
        if (data != null) {
            holder.titleText.text = data!!.titlePage
            val controller = MediaItemsController(onItemClick)
            controller.setData(data!!.data)
            holder.postersList.isNestedScrollingEnabled = true
            holder.postersList.adapter = controller.adapter
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_feed_horizontal_list
}

class MediaItemsController(val onMediaClick: (Movie, View) -> Unit) :
    TypedEpoxyController<List<Movie>>() {
    override fun buildModels(it: List<Movie>) {
        it.mapIndexed { index, item ->
            MediaModel_()
                .id(index)
                .items(item)
                .onClick(onMediaClick)
                .addTo(this)
        }
    }
}

@EpoxyModelClass
abstract class MediaModel :
    EpoxyModelWithHolder<MediaModel.MediaHolder>() {
    @EpoxyAttribute
    lateinit var items: Movie

    @EpoxyAttribute
    lateinit var onClick: (Movie, View) -> Unit

    inner class MediaHolder : EpoxyHolder() {
        lateinit var posterImage: ImageView
        override fun bindView(itemView: View) {
            posterImage = itemView.findViewById(R.id.poster_image)
        }
    }

    override fun bind(holder: MediaHolder) {
        var posterUrl: String? = null
        posterUrl = items.posterHorizontal
        holder.posterImage.setSingleClickListener { onClick(items, holder.posterImage) }
        Glide.with(holder.posterImage).load(posterUrl).into(holder.posterImage)
        holder.posterImage.clipToOutline = true
    }

    override fun getDefaultLayout(): Int = R.layout.item_poster
}

