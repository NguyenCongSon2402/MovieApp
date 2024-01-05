package dev.son.movie.ui.rateApp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dev.son.movie.R
import dev.son.movie.adapters.FeedBackAdapter
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.FragmentRateAppBinding
import dev.son.movie.network.models.FeedBack


class RateAppActivity : TrackingBaseActivity<FragmentRateAppBinding>() {
    private var listFeedBack: ArrayList<FeedBack> = ArrayList()
    private var TAG = "RateAppFragment"
    private var textEditText: String = ""
    private lateinit var feedBackAdapter: FeedBackAdapter
    private var totalLength: Int = 0


    companion object {
        const val EMAIL = "suytdeptrai2402@gmail.com"
        const val SUBJECT = "Movie Feedback"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        views.rcvfeedback.layoutManager = layoutManager

        feedBackAdapter = FeedBackAdapter(this, object : FeedBackAdapter.OnItemClicked {
            override fun onItemClicked(position: Int, feedBack: FeedBack, isSelectedOne: Boolean) {

                /*if (text!= null){
                    binding.btnSubmit.setBackgroundResource(R.drawable.bg_buttom_submit)
                }*/
                if (isSelectedOne) {
                    views.btnSubmit.setBackgroundResource(R.drawable.bg_buttom_submit)
                    views.btnSubmit.isEnabled = true
                } else {
                    views.btnSubmit.setBackgroundResource(R.drawable.bg_multishotnull)
                    views.btnSubmit.isEnabled = false
                }
            }
        })

        views.rcvfeedback.adapter = feedBackAdapter

        feedBackAdapter.submitList((getListFeedBack(this)))
        //getListFeedBack(this)

        views.btnSubmit.setOnClickListener {
            var text = ""
            feedBackAdapter.currentList.forEach {
                if (it.isSelected) {
                    text = "$text ${it.text}, "
                }
            }

            text += textEditText


            val selectorIntent = Intent(Intent.ACTION_SENDTO)
            val urlString =
                "mailto:" + Uri.encode(EMAIL) + "?subject=" + Uri.encode(SUBJECT) + "&body=" + Uri.encode(
                    text
                )
            selectorIntent.data = Uri.parse(urlString)

            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
            emailIntent.putExtra(Intent.EXTRA_TEXT, text)
            emailIntent.selector = selectorIntent

            startActivity(Intent.createChooser(emailIntent, "Send email"))
            finish()

        }


        views.edtInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    val trimmedString = s.trim()
                    textEditText = trimmedString.toString()
                    totalLength = trimmedString.length
                    Log.d(TAG, "Tổng số ký tự: $totalLength")
                }

                if (totalLength >= 6) {
                    views.btnSubmit.setBackgroundResource(R.drawable.bg_buttom_submit)
                    views.btnSubmit.isEnabled = true
                } else {
                    views.btnSubmit.setBackgroundResource(R.drawable.bg_submit_null)
                    views.btnSubmit.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        views.imgBack.setOnClickListener {
           finish()
        }
    }

    private fun getListFeedBack(context: Context): ArrayList<FeedBack> {
        listFeedBack.add(FeedBack(context.getString(R.string.crashes_bugs), false))
        listFeedBack.add(FeedBack(context.getString(R.string.susggestion), false))
        listFeedBack.add(FeedBack(context.getString(R.string.others), false))

        return listFeedBack
    }


    override fun getBinding(): FragmentRateAppBinding {
        return FragmentRateAppBinding.inflate(layoutInflater)
    }

}



