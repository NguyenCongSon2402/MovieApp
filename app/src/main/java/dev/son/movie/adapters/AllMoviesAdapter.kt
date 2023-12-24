package dev.son.movie.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import com.airbnb.epoxy.*
import com.bumptech.glide.Glide
import dev.son.movie.R
import dev.son.movie.network.models.movie.Movie

class AllMoviesAdapter(private val onItemClick: ((Movie, View) -> Unit), private val onEdtClick:((Movie,Boolean) -> Unit)) :
    TypedEpoxyController<List<Movie>>() {
    override fun buildModels(allMovies: List<Movie>) {

        allMovies?.let { listMovie ->
            listMovie.forEachIndexed { index, movie ->
                AllMovieModel_()
                    .id(index)
                    .movie(movie)
                    .onClick(onItemClick)
                    .onEdtClick(onEdtClick)
                    .addTo(this)

            }
        }
    }
}

@EpoxyModelClass
abstract class AllMovieModel : EpoxyModelWithHolder<AllMovieModel.AllMovieHolder>() {

    @EpoxyAttribute
    lateinit var movie: Movie

    @EpoxyAttribute
    lateinit var onClick: (Movie, View) -> Unit

    @EpoxyAttribute
    lateinit var onEdtClick: (Movie, Boolean) -> Unit

    inner class AllMovieHolder : EpoxyHolder() {
        lateinit var content: LinearLayout
        lateinit var nameText: TextView
        lateinit var yearText: TextView
        lateinit var edit: ImageButton
        lateinit var backdropImage: ImageView
        override fun bindView(itemView: View) {
            content = itemView.findViewById(R.id.content)
            backdropImage = itemView.findViewById(R.id.backdrop_image)
            nameText = itemView.findViewById(R.id.name_text)
            yearText = itemView.findViewById(R.id.year_text)
            edit = itemView.findViewById(R.id.btn_edit)
        }
    }

    override fun bind(holder: AllMovieHolder) {
        Glide.with(holder.backdropImage).load(movie.posterHorizontal).into(holder.backdropImage)
        holder.nameText.text = movie.title
        holder.yearText.text = movie.releaseYear
        holder.content.setOnClickListener { onClick(movie, holder.content) }
        holder.edit.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.movie_edt_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        onEdtClick(movie,true,)
                        true
                    }

                    R.id.menu_delete -> {
                        onEdtClick(movie,false)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_top_movie
}