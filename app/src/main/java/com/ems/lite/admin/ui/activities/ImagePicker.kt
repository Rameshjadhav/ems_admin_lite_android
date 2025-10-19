package com.ems.lite.admin.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.ems.lite.admin.R
import com.ems.lite.admin.utils.Logger
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.IOException
import java.util.*

@SuppressLint("Registered")
open class ImagePicker : BaseActivity() {

    protected var updatedImageFile: File? = null
    protected var isSquareRatio = false
    private var mImageUri: Uri? = null

    fun showTakeImagePopup(launcher: ActivityResultLauncher<Any?>?) {
        cropLauncher = launcher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !hasPermissions(this, *PERMISSIONS_11)
        ) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_11, PERMISSION_ALL)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            && !hasPermissions(this, *PERMISSIONS)
        ) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        } else {
            val items = arrayOf<CharSequence>(
                getString(R.string.select_image_from_gallery),
                getString(R.string.open_camera),
                getString(R.string.cancel)
            )
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.select_photo))
            builder.setItems(items) { _, item ->
                when (item) {
                    0 -> openGallery()
                    1 -> openPhoneCamera()
                    3 -> {
                    }
                }
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }.run {
            galleryPickerLauncher.launch(this)
        }
    }

    private fun openPhoneCamera() {
        val storageState = Environment.getExternalStorageState()
        try {
            val mediaDir: File? = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES) // (1)
                }

                storageState == Environment.MEDIA_MOUNTED -> {
                    @Suppress("DEPRECATION")
                    File(Environment.getExternalStorageDirectory().toString())
                }

                else -> {
                    File(filesDir, "BizActive")
                }
            }
            if (mediaDir != null && !mediaDir.exists()) {
                mediaDir.mkdirs()
            }
            updatedImageFile = File(mediaDir, Date().time.toString() + ".jpg")
            if (!updatedImageFile!!.exists()) {
                updatedImageFile!!.createNewFile()
            }
        } catch (e: IOException) {
            Logger.d(TAG, "Could not create file: $e")
        }

        if (updatedImageFile != null)
            mImageUri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".utils.GenericFileProvider",
                updatedImageFile!!
            )
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.run {
            //GRANT URI PERMISSIONS TO ALL APPS , ELSE CAMERA CRASHES WHILE SAVING PHOTO.
            val resInfoList =
                packageManager.queryIntentActivities(this, PackageManager.MATCH_DEFAULT_ONLY)
            resInfoList.forEach {
                val packageName = it.activityInfo.packageName
                grantUriPermission(
                    packageName, mImageUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            /*for(resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName, mImageUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }*/

            cameraLauncher.launch(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_ALL -> {
                val isNotPermissionGranted =
                    grantResults.find { it != PackageManager.PERMISSION_GRANTED }
                if (isNotPermissionGranted == null) {
                    showTakeImagePopup(cropLauncher)
                } else {
                    Toast.makeText(this, getString(R.string.camera_permission), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    private fun startCroppingActivity(/*mImageUri: Uri?*/) {
        // start picker to get image for cropping and then use the image in cropping activity
//        val intent = com.theartofdev.edmodo.cropper.CropImage.activity(mImageUri)
//            .setMultiTouchEnabled(false)
//            .setGuidelines(CropImageView.Guidelines.OFF)
//            .setCropShape(CropImageView.CropShape.RECTANGLE)
//        if (isSquareRatio) {
//            intent.setAspectRatio(1, 1)
//        }
//        intent.setRequestedSize(400, 400, CropImageView.RequestSizeOptions.RESIZE_EXACT)
//        intent.start(this)
        cropLauncher?.launch(null)
    }

    private var cropLauncher: ActivityResultLauncher<Any?>? = null
    protected val cropActivityResultContract by lazy {
        object : ActivityResultContract<Any?, Uri?>() {
            override fun createIntent(context: Context, input: Any?): Intent {
                val builder = CropImage.activity(mImageUri)
                    .setMultiTouchEnabled(false)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setRequestedSize(400, 400, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                if (isSquareRatio) {
                    builder.setAspectRatio(1, 1)
                }
                return builder.getIntent(this@ImagePicker)

            }

            override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
                return CropImage.getActivityResult(intent)?.uri
            }
        }
    }
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                startCroppingActivity()
            }
        }
    private val galleryPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                mImageUri = result.data!!.data
                startCroppingActivity()
            }
        }


    companion object {

//        const val RC_CROP_ACTIVITY =
//            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE

        val TAG: String = ImagePicker::class.java.name
        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val PERMISSIONS_11 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
        private const val PERMISSION_ALL = 500

        // My Generic Check Permission Method
        fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (context != null) {
                val hasNotPermission = permissions.find {
                    ActivityCompat.checkSelfPermission(
                        context, it
                    ) != PackageManager.PERMISSION_GRANTED
                }
                return (hasNotPermission == null)
            }
            return true
        }
    }
}