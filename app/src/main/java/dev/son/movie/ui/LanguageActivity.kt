package dev.son.movie.ui

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import com.mobiai.base.language.LanguageUtil
import dev.son.movie.R
import dev.son.movie.adapters.LanguageAdapter
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.SharedPreferenceUtils
import dev.son.movie.databinding.ActivityLanguageBinding
import dev.son.movie.language.Language

class LanguageActivity : TrackingBaseActivity<ActivityLanguageBinding>() {

    var listLanguages: ArrayList<Language> = arrayListOf()
    var languageCode = "en"
    lateinit var languageAdapter: LanguageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDataLanguage()
        views.imgConfirm.setOnClickListener {
            changeLanguage()
        }
        views.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun getDataLanguage() {
        initData()
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Resources.getSystem().configuration.locale
        }
        var languageSystem: Language? = null
        var position = 0
        for (language in listLanguages) {
            if (language.locale.equals(locale.language)) {
                languageSystem = language
                languageCode = locale.language
            }
            if (SharedPreferenceUtils.languageCode == language.locale) {
                languageSystem = language
                languageCode = languageSystem.locale
            }
            position = listLanguages.indexOf(languageSystem)


        }
        listLanguages[position].isChoose = true
        initAdapter()
    }

    private fun initAdapter() {
        languageAdapter = LanguageAdapter(this, object : LanguageAdapter.OnLanguageClickListener {
            override fun onClickItemListener(language: Language?) {
                languageCode = language!!.locale
            }
        })
        languageAdapter.submitList(listLanguages)
        views.recyclerViewLanguage.adapter = languageAdapter
    }

    private fun changeLanguage() {
        SharedPreferenceUtils.languageCode = languageCode
        LanguageUtil.changeLang(SharedPreferenceUtils.languageCode!!, this)
        startActivity(Intent(this, BottomNavActivity::class.java))
    }

    private fun initData() {
        listLanguages = ArrayList()

        listLanguages.add(Language(R.drawable.flag_en, getString(R.string.language_english), "en"))
        listLanguages.add(
            Language(
                R.drawable.flag_cn_china,
                "Chinese",
                "zh"
            )
        )
        listLanguages.add(
            Language(
                R.drawable.flag_in_hindi,
                "Hindi",
                "hi"
            )
        )
        listLanguages.add(
            Language(
                R.drawable.flag_fr_france,
                "French",
                "fr"
            )
        )
        listLanguages.add(
            Language(
                R.drawable.flag_es_spain,
                "Spanish",
                "es"
            )
        )
        listLanguages.add(
            Language(
                R.drawable.flag_pt_portugal,
                "Portuguese",
                "pt"
            )
        )  //18


        listLanguages.add(
            Language(
                R.drawable.flag_id,
                getString(R.string.language_indo),
                "in"
            )
        ) // //17
        listLanguages.add(
            Language(
                R.drawable.flag_kr,
                getString(R.string.language_korean),
                "ko"
            )
        ) //16
        listLanguages.add(
            Language(
                R.drawable.flag_nl,
                getString(R.string.language_dutch_nl),
                "nl"
            )
        ) //15
        listLanguages.add(
            Language(
                R.drawable.flag_ja,
                getString(R.string.language_japan),
                "ja"
            )
        ) //14
        listLanguages.add(
            Language(
                R.drawable.flag_de_germany,
                getString(R.string.language_germany),
                "de"
            )
        ) //13
        listLanguages.add(
            Language(
                R.drawable.flag_pt_portugal,
                getString(R.string.language_danish_da),
                "da"
            )
        ) //Todo //12
        listLanguages.add(
            Language(
                R.drawable.flag_pt_portugal,
                getString(R.string.language_zuhu_zu),
                "zu"
            )
        )  //todo //1
        listLanguages.add(
            Language(
                R.drawable.flag_vi,
                getString(R.string.language_viet_nam),
                "vi"
            )
        ) //10
        listLanguages.add(
            Language(
                R.drawable.flag_ie,
                getString(R.string.language_irish_en_ie),
                "ie"
            )
        ) // 9
        listLanguages.add(
            Language(
                R.drawable.flag_it,
                getString(R.string.language_italian_it),
                "it"
            )
        ) //8
        listLanguages.add(
            Language(
                R.drawable.flag_pl,
                getString(R.string.language_polish_pl),
                "pl"
            )
        ) //7
        listLanguages.add(
            Language(
                R.drawable.flag_ru,
                getString(R.string.language_russian),
                "ru"
            )
        ) //6
        listLanguages.add(
            Language(
                R.drawable.flag_tr,
                getString(R.string.language_turkish),
                "tr"
            )
        )  //5
        listLanguages.add(
            Language(
                R.drawable.flag_pt_portugal,
                getString(R.string.language_malay),
                "ms"
            )
        ) //todo //4
        listLanguages.add(
            Language(
                R.drawable.flag_pt_portugal,
                getString(R.string.language_filipino),
                "fil"
            )
        )  //todo //3
        listLanguages.add(
            Language(
                R.drawable.flag_uk,
                getString(R.string.language_ukrainian),
                "uk"
            )
        )  //2
        listLanguages.add(
            Language(
                R.drawable.flag_af,
                getString(R.string.language_afrikaans),
                "af"
            )
        )   //1
    }


    override fun getBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }
}