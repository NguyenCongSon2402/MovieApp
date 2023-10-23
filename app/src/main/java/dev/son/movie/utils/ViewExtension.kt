package dev.son.movie.utils

import android.view.View

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.hide(){
    this.visibility = View.GONE
}
fun View.setSingleClickListener(onClick: (View) -> Unit) {
    val debounceTime = 100L // Thời gian chờ trước khi cho phép nhấn lại
    var lastClickTime = 0L

    this.setOnClickListener { view ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceTime) {
            // Đủ thời gian đã trôi qua, cho phép thực hiện sự kiện
            onClick(view)
            lastClickTime = currentTime
        }
    }
}