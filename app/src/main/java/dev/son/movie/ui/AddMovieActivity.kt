package dev.son.movie.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.adapters.CategoryAdapter
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityAddMovieBinding
import dev.son.movie.network.models.AddMovieRequestBody
import dev.son.movie.network.models.movie.Genre
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.ui.home.HomeViewAction
import dev.son.movie.ui.home.HomeViewModel
import dev.son.movie.ui.home.HomeViewState
import dev.son.movie.ui.search.SearchViewAction
import dev.son.movie.ui.search.SearchViewModel
import dev.son.movie.ui.search.SearchViewState
import dev.son.movie.utils.PermissionUtils
import dev.son.movie.utils.checkStatusApiRes
import dev.son.movie.utils.convertBitmapToBase64
import dev.son.movie.utils.hide
import dev.son.movie.utils.show
import javax.inject.Inject

class AddMovieActivity : TrackingBaseActivity<ActivityAddMovieBinding>(), SearchViewModel.Factory,
    HomeViewModel.Factory {

    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 2

    private val PICK_IMAGE_REQUEST_1 = 1
    private val PICK_IMAGE_REQUEST_2 = 2
    private lateinit var resultLauncher1: ActivityResultLauncher<Intent>
    private lateinit var resultLauncher2: ActivityResultLauncher<Intent>

    private val searchViewModel: SearchViewModel by viewModel()

    @Inject
    lateinit var searchViewModelFactory: SearchViewModel.Factory

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory
    private val homeViewModel: HomeViewModel by viewModel()

    private var country1: String? = null

    private var genres: List<Genre>? = null
    private var dataCountries: List<Genre>? = null
    private val selectedTopic: MutableList<String> = mutableListOf()
    private val selectedCategory: MutableList<String> = mutableListOf()
    private var selectedGenre: List<String> = emptyList()
    private var movieLinks: MutableList<String> = mutableListOf()

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var topicAdapter: CategoryAdapter

    private var LINK_NEXT = 2


    @Suppress("DEPRECATION")
    private val movie: Movie?
        get() = intent.getParcelableExtra("movie")

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        if (movie != null) {
            views.toolbar.title = "Sửa phim"
            views.idMovie.show()
            views.idMovie.text = movie?.id.toString()
        }
        registerResult()
        fetchData()
        setUpUi()
        subscribeData()
        setDataUI()
    }

    private fun setDataUI() {
        if (movie != null) {
            views.btnAdd.hide()
            views.btnEdit.show()
            views.edtUrl3x4.show()
            views.edtUrl4x3.show()
            views.edtUrl3x4.setText(movie?.posterVertical)
            views.edtUrl4x3.setText(movie?.posterHorizontal)
            views.movieName.setText(movie?.title)
            views.actors.setText(movie?.actors)
            views.director.setText(movie?.director)
            views.time.setText(movie?.duration)
            views.releaseYear.setText(movie?.releaseYear)
            views.movieDesc.setText(movie?.description)
            views.trailerLink.setText(movie?.trailerURL)
            movieLinks = movie?.videoURL as MutableList<String>
            if (!movieLinks.isNullOrEmpty()) {
                Log.e("MOVIE", movieLinks.toString())
                createInputLinks(movieLinks)
            }

            views.btnEdit.setOnClickListener {
                validateInput(false)
            }

        }
    }

    private fun setUpUi() {
        categoryAdapter = CategoryAdapter(this::handleClick)
        topicAdapter = CategoryAdapter(this::handleClickTopic)
        views.rvGenres.adapter = categoryAdapter
        views.rvTopic.adapter = topicAdapter
        views.toolbar.setOnClickListener { finish() }
        views.btnAddLink.setOnClickListener {
            createInputLink()
        }
        views.btnRemoveLink.setOnClickListener {
            if (LINK_NEXT > 2) {
                LINK_NEXT--
                views.movieLinks.removeView(findViewById(LINK_NEXT))
                try {
                    movieLinks.removeAt(LINK_NEXT - 1)
                } catch (e: Exception) {
                    print(e)
                }
            }
        }



        views.btnImage3x4.setOnClickListener {
            // Kiểm tra quyền trước khi truy cập Gallery.
            if (PermissionUtils.shouldAskForPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Yêu cầu quyền đọc bộ nhớ ngoài.
                PermissionUtils.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
                )
            } else {
                openGallery(PICK_IMAGE_REQUEST_1)
            }

        }
        views.btnImage4x3.setOnClickListener {
            // Kiểm tra quyền trước khi truy cập Gallery.
            if (PermissionUtils.shouldAskForPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Yêu cầu quyền đọc bộ nhớ ngoài.
                PermissionUtils.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
                )
            } else {
                openGallery(PICK_IMAGE_REQUEST_2)
            }

        }
        views.btnImageUrl3x4.setOnClickListener {
            views.edtUrl3x4.show()
        }
        views.btnImageUrl4x3.setOnClickListener {
            views.edtUrl4x3.show()
        }

        views.edtUrl3x4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Không cần thực hiện gì đặc biệt trước khi văn bản thay đổi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Không cần thực hiện gì đặc biệt khi văn bản thay đổi
            }

            override fun afterTextChanged(s: Editable?) {
                val imageUrl = s.toString()

                Glide.with(this@AddMovieActivity)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(views.image1)
            }
        })
        views.edtUrl4x3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Không cần thực hiện gì đặc biệt trước khi văn bản thay đổi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Không cần thực hiện gì đặc biệt khi văn bản thay đổi
            }

            override fun afterTextChanged(s: Editable?) {
                val imageUrl = s.toString()

                // Sử dụng Glide để hiển thị ảnh từ URL trong ImageView
                Glide.with(this@AddMovieActivity)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(views.image2)
            }
        })
        views.btnAdd.setOnClickListener {
            validateInput(true)
        }
        views.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCountry = parent?.getItemAtPosition(position) as? String
                if (!selectedCountry.isNullOrBlank()) {
                    // Sử dụng giá trị đã chọn ở đây
                    country1 = dataCountries?.get(position)?.code.toString()
                    Log.d("Spinner", "Selected Country: $country1")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Được gọi khi không có item nào được chọn
                Log.d("Spinner", "Selected Country:")
            }
        }


    }

    private fun validateInput(add: Boolean) {


        movieLinks.clear()
        for (i in 0 until views.movieLinks.childCount) {
            val view: View = views.movieLinks.getChildAt(i)
            if (view is EditText) {
                val link = view.text.toString().trim { it <= ' ' }
                if (!link.isEmpty()) {
                    movieLinks.add(link)
                }
            }
        }


        val movieName = views.movieName.text
        val actor = views.actors.text
        val director = views.director.text
        val time = views.time.text
        val year = views.releaseYear.text
        val movieDesc = views.movieDesc.text
        val trailer = views.trailerLink.text
//        val image1 = convertBitmapToBase64((views.image1.drawable as BitmapDrawable).bitmap)
//        val image2 = convertBitmapToBase64((views.image2.drawable as BitmapDrawable).bitmap)
//        val imgUrl1 =
//            if (views.edtUrl3x4.text.isNullOrEmpty()) image1 else views.edtUrl3x4.text.toString()
//        val imgUrl2 =
//            if (views.edtUrl4x3.text.isNullOrEmpty()) image2 else views.edtUrl4x3.text.toString()
//
//
//        if (imgUrl1.isNullOrEmpty()) {
//            Toast.makeText(this, "Ảnh 3x4 không được để trống", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (imgUrl2.isNullOrEmpty()) {
//            Toast.makeText(this, "Ảnh 4x3 không được để trống", Toast.LENGTH_SHORT).show()
//            return
//        }

        if (selectedCategory.isEmpty() || selectedTopic.isEmpty()) {
            Toast.makeText(this, "Chủ đề hoặc thể loại không được trống", Toast.LENGTH_SHORT).show()
            return
        }

        if (movieName.isNullOrEmpty()) views.movieName.error =
            "Tên phim không được để trống"
        if (time.isNullOrEmpty()) views.time.error =
            "Thời gian không được để trống"
        if (year.isNullOrEmpty()) views.releaseYear.error =
            "Năm phát hành không được để trống"
        if (year.isNullOrEmpty()) views.releaseYear.error =
            "Nội dung phim không được để trống"

        if (!movieName.isNullOrEmpty() && !time.isNullOrEmpty() && !year.isNullOrEmpty() && !country1.isNullOrEmpty() && !selectedTopic.isNullOrEmpty() && !selectedCategory.isNullOrEmpty()) {

            val addMovieRequestBody = AddMovieRequestBody().apply {
                this.title = movieName.toString()
                this.description = movieDesc.toString()
                this.director = director.toString()
                this.releaseYear = year.toString()
                this.duration = time.toString()
                this.country = country1.toString()
                this.actors = actor.toString()
                this.trailerURL = trailer.toString()
                this.genre = selectedCategory + selectedTopic
                this.posterVertical = views.edtUrl3x4.text.toString()
                this.posterHorizontal = views.edtUrl4x3.text.toString()
                this.videoURL = movieLinks
            }

            if (add == true) {
                searchViewModel.handle(SearchViewAction.addMovie(addMovieRequestBody))
            } else {
                searchViewModel.handle(
                    SearchViewAction.updateMovie(
                        movie?.id.toString(),
                        addMovieRequestBody
                    )
                )
            }
        }


    }

    private fun openGallery(PICK_IMAGE_REQUEST: Int) {
        // Mở Gallery để chọn ảnh và xử lý kết quả khi người dùng chọn ảnh.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        when (PICK_IMAGE_REQUEST) {
            PICK_IMAGE_REQUEST_1 -> resultLauncher1.launch(galleryIntent)
            PICK_IMAGE_REQUEST_2 -> resultLauncher2.launch(galleryIntent)
            // Nếu bạn có nhiều nút, thêm các trường hợp khác tương ứng
        }
    }

    // Xử lý kết quả yêu cầu quyền từ người dùng.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, bạn có thể truy cập Gallery và chọn ảnh ở đây.
            } else {
                // Quyền bị từ chối, hiển thị thông báo cho người dùng hoặc thực hiện xử lý phù hợp.
            }
        }
    }

    private fun registerResult() {
        resultLauncher1 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // Xử lý kết quả ở đây.
                    try {
                        val image = result.data?.data
                        views.image1.setImageURI(image)

//                        val imageStr=
//                            convertBitmapToBase64((views.imgProfile.drawable as BitmapDrawable).bitmap)
//                        val imgUser = HashMap<String, Any>()
//                        imageStr?.let {
//                            imgUser["photoURL"] = it
////                            loginViewModel.handle(LoginViewAction.upDateUser(imgUser))
////                            views.loading.show()
//                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        resultLauncher2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // Xử lý kết quả ở đây.
                    try {
                        val image = result.data?.data
                        views.image2.setImageURI(image)

//                        val imageStr=
//                            convertBitmapToBase64((views.imgProfile.drawable as BitmapDrawable).bitmap)
//                        val imgUser = HashMap<String, Any>()
//                        imageStr?.let {
//                            imgUser["photoURL"] = it
////                            loginViewModel.handle(LoginViewAction.upDateUser(imgUser))
////                            views.loading.show()
//                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun createInputLinks(links: List<String>) {
        views.link.setText(links[0])
        if (links.size > 1) {
            links.forEachIndexed { index, element ->
                if (index > 0) {
                    val editText = EditText(this)
                    editText.layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    editText.setHintTextColor(ContextCompat.getColor(this, R.color.white))
                    editText.hint =
                        "Url ${index + 1}" // Bắt đầu từ 1, nếu muốn bắt đầu từ 0 thì chỉ cần thay đổi index thành index + 1
                    editText.setTextColor(ContextCompat.getColor(this, R.color.white))
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    editText.id = LINK_NEXT
                    Log.e("LINK_NEXT", LINK_NEXT.toString() + links.size)
                    editText.setText(element) // Set dữ liệu cho EditText
                    views.movieLinks.addView(editText)
                    LINK_NEXT++
                }
            }
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun createInputLink() {
        val editText: EditText
        editText = EditText(this)
        editText.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.white))
        editText.hint = "Url $LINK_NEXT"
        editText.setTextColor(ContextCompat.getColor(this, R.color.white))
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        editText.id = LINK_NEXT
        views.movieLinks.addView(editText)
        LINK_NEXT++
    }

    private fun handleClick(genre: Genre, isChecked: Boolean) {
        if (isChecked) {
            selectedCategory.add(genre.code.toString())
        } else {
            selectedCategory.remove(genre.code)
        }
//        Log.e("GENRECLICK", selectedCategory.toString())
    }

    private fun handleClickTopic(genre: Genre, isChecked: Boolean) {
        if (isChecked) {
            selectedTopic.add(genre.code.toString())
        } else {
            selectedTopic.remove(genre.code)
        }
    }

    private fun subscribeData() {
        homeViewModel.subscribe(this) {
            when (it.genre) {
                is Success -> {

                    genres = it.genre.invoke().data

                    if (movie != null && !genres.isNullOrEmpty()) {
                        selectedGenre = movie!!.genre.split(", ").map { it1 -> it1.trim() }
                        selectedGenre.forEach { selectedGenre ->
                            genres!!.find { it1 -> it1.code == selectedGenre }?.selected = true

                        }
                    }

                    //Log.e("GENRE", genres.toString())
                    val (genresWithAt, genresWithoutAt) = genres?.partition { genre ->
                        genre.code?.contains("@") == true
                    } ?: Pair(emptyList(), emptyList())
                    categoryAdapter.submitList(genresWithoutAt)
                    topicAdapter.submitList(genresWithAt)
                    homeViewModel.handleRemoveStateGetGenre()
                    //views.loading.hide()
                }

                is Loading -> {
                    //.loading.show()
                }

                is Fail -> {
                    //views.loading.hide()
                    Toast.makeText(
                        this,
                        "genre " +
                                checkStatusApiRes(it.genre),
                        Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateGetGenre()
                }

                else -> {}
            }
            when (it.countriesMovies) {
                is Success -> {
                    dataCountries = it.countriesMovies.invoke().data
                    val namesList: List<String> = dataCountries!!.map { it.name ?: "" }
                    val codesList: List<String> = dataCountries!!.map { it.code ?: "" }
                    // Tạo Adapter cho Spinner
                    val adapter = ArrayAdapter(
                        this@AddMovieActivity,
                        android.R.layout.simple_spinner_item,
                        namesList
                    )
                    views.spinner.adapter = adapter


                    if (movie != null && !dataCountries.isNullOrEmpty()) {
                        val position = codesList.indexOf(movie!!.country)
                        if (position != -1) {
                            views.spinner.setSelection(position)
                        } else {
                            views.spinner.setSelection(0)
                        }

                    }
                    homeViewModel.handleRemoveStateCountries()
                }

                is Fail -> {
                    Toast.makeText(
                        this,
                        "countriesMovies " +
                                checkStatusApiRes(it.countriesMovies),
                        Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.handleRemoveStateCountries()
                }

                else -> {}
            }
        }
        searchViewModel.subscribe(this) {
            when (it.addMovie) {
                is Success -> {
                    Toast.makeText(
                        this,
                        it.addMovie.invoke().message,
                        Toast.LENGTH_SHORT
                    ).show()
                    searchViewModel.handleRemoveStateAddMovie()
                }

                is Fail -> {
                    Toast.makeText(
                        this,
                        "addMovie " +
                                checkStatusApiRes(it.addMovie),
                        Toast.LENGTH_SHORT
                    ).show()
                    searchViewModel.handleRemoveStateAddMovie()
                }

                else -> {}
            }
            when (it.updateMovie) {
                is Success -> {
                    if (it.updateMovie.invoke().code==200){
                        Toast.makeText(this,"Cập nhập thành công",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"${it.updateMovie.invoke().code}",Toast.LENGTH_SHORT).show()
                    }
                    searchViewModel.handleRemoveStateUpdateMovie()
                }

                is Fail -> {
                    Toast.makeText(
                        this,
                        "updateMovie " +
                                checkStatusApiRes(it.updateMovie),
                        Toast.LENGTH_SHORT
                    ).show()
                    searchViewModel.handleRemoveStateUpdateMovie()
                }

                else -> {}
            }
        }
    }

    private fun fetchData() {
        homeViewModel.handle(HomeViewAction.getGenre)
        homeViewModel.handle(HomeViewAction.getCountries)
    }

    override fun getBinding(): ActivityAddMovieBinding {
        return ActivityAddMovieBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SearchViewState): SearchViewModel {
        return searchViewModelFactory.create(initialState)
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }
}