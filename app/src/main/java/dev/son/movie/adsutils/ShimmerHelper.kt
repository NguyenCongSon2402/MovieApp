package dev.son.movie.adsutils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.shimmer.ShimmerFrameLayout


class ShimmerHelper(private val context: Context) {

    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var contentView: View? = null

    fun startShimmer(layoutResId: Int, container: ViewGroup) {
        stopShimmer() // Stop any existing shimmer before starting a new one

        // Inflate the layout to be used as shimmer layout
        val shimmerLayout = LayoutInflater.from(context).inflate(layoutResId, container, false)

        // Create ShimmerFrameLayout and add it to the container
        shimmerFrameLayout = ShimmerFrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(shimmerLayout)
            container.addView(this)
            startShimmer()
        }
    }

    fun stopShimmer() {
        shimmerFrameLayout?.stopShimmer()
        contentView?.visibility = View.VISIBLE
        shimmerFrameLayout?.visibility = View.GONE
    }

    private fun startShimmer() {
        shimmerFrameLayout?.startShimmer()
    }

    fun setContent(view: View) {
        contentView = view
    }
}


