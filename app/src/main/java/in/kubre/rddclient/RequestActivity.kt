package `in`.kubre.rddclient

import `in`.kubre.rddclient.data.Issue
import `in`.kubre.rddclient.service.AppService
import `in`.kubre.rddclient.utils.ImageUtils
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_request.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.FileOutputStream


const val TAKE_PICTURE = 1

class RequestActivity : AppCompatActivity() {

    // region varibles
    private var mPhoto: File? = null
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null
    private var mService: AppService? = null
    private var mUserName: String? = null
    private var mUserMobile: String? = null
    private var mAlert: AlertDialog? = null
    private lateinit var mMapView: WebView
    private var mLocationAlert: AlertDialog? = null
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        val profileData =
            getSharedPreferences(resources.getString(R.string.profile_data), Context.MODE_PRIVATE)
                ?: return

        val appUrl = "http://${profileData.getString("ip", "192.168.43.224")}:5000"
        val mapUrl = "$appUrl/maps"

        /// region
        mService = Retrofit.Builder()
            .baseUrl(appUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(AppService::class.java)

        mAlert = AlertDialog.Builder(this@RequestActivity).apply {
            title = "Create Issue"
            setMessage("Your issue created successfully!")

        }.create()

        mMapView = WebView(this@RequestActivity)
        mMapView.settings.javaScriptEnabled = true
        mMapView.webViewClient = WebViewClient()

        mLocationAlert = AlertDialog.Builder(this@RequestActivity).apply {
            setTitle("Location Information")
            setView(mMapView)
            setNeutralButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }.create()

        mLocationAlert?.window?.setLayout(400, 500)


        mUserName = profileData.getString("name", "TEST")
        mUserMobile = profileData.getString("mobile", "8080808080")
        /// endregion

        // Click Photo
        f_request_img_problem.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    mPhoto = File(externalMediaDirs[0], "photo.jpg")
                    val mImageUri = FileProvider.getUriForFile(
                        this@RequestActivity,
                        BuildConfig.APPLICATION_ID + ".provider",
                        mPhoto!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
                    startActivityForResult(takePictureIntent, TAKE_PICTURE)
                }
            }
        }

        // Location
        f_request_btn_location.setOnClickListener {
            SmartLocation.with(this@RequestActivity).location()
                .oneFix()
                .start {
                    mLatitude = it.latitude
                    mLongitude = it.longitude
                    f_request_btn_location.text = "Lat: $mLatitude, Lon: $mLongitude"
                    SmartLocation.with(this@RequestActivity).location().stop()
                    Log.d("DEMO", "$mapUrl/$mLatitude/$mLongitude")
                    mMapView.loadUrl("$mapUrl/$mLatitude/$mLongitude")
                    mLocationAlert?.show()
                }
        }

        // Submit
        f_request_btn_submit.setOnClickListener {
            val f = File(externalMediaDirs[0], "photo.jpg")

            val body: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), f)

            val photo: MultipartBody.Part = MultipartBody.Part.createFormData("photo", f.name, body)

            mService!!.openIssue(
                mUserName!!,
                mUserMobile!!,
                "Depth: ${f_request_txt_depth_details.text}, Width: ${f_request_txt_width_details.text}, Details: ${f_request_txt_details.text}",
                mLatitude.toString(),
                mLongitude.toString(),
                photo
            ).enqueue(object : Callback<Issue> {
                override fun onFailure(call: Call<Issue>, t: Throwable) {
                    mAlert?.also {
                        title = "Error"
                        it.setMessage("Error occurred while creating the issue.")
                    }?.show()
                }

                override fun onResponse(
                    call: Call<Issue>, response: Response<Issue>
                ) {
                    if (response.isSuccessful) {
                        mAlert?.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                            val i = Intent(this@RequestActivity, MainActivity::class.java)
                            startActivity(i)
                        }
                        mAlert?.show()
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            saveBitmapToFile(mPhoto!!)
            Glide.with(applicationContext)
                .load(mPhoto!!)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .into(f_request_img_problem)
            f_request_img_problem.background = null
        }
    }

    private fun saveBitmapToFile(file: File): File? {
        return try {

            val selectedBitmap = ImageUtils.getCompressedBitmap(file.absolutePath)
            file.createNewFile()
            val outputStream = FileOutputStream(file)

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            file
        } catch (e: Exception) {
            null
        }
    }
}
