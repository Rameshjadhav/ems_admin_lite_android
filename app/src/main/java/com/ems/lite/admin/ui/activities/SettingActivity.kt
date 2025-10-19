package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Base64OutputStream
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.SettingActivityBinding
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.ImageSetter
import com.ems.lite.admin.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File

@AndroidEntryPoint
class SettingActivity : ImagePicker(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, SettingActivity::class.java).run {
                activity.startActivity(this)
            }
        }
    }

    private lateinit var binding: SettingActivityBinding
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val bluetoothPrinters = mutableListOf<BluetoothConnection>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.setting_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.settings))
        binding.rbGeneral.isChecked = Prefs.isGeneralMsg
        binding.rbVoting.isChecked = !Prefs.isGeneralMsg
        binding.rbWithImage.isChecked = Prefs.isWithImageMsg
        binding.rbWithoutImage.isChecked = !Prefs.isWithImageMsg
        initClickListener()
        init()
    }

    private fun init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        checkAndRequestPermissions()
//        if (!Prefs.headerImage.isNullOrEmpty()) {
//            val encoded: ByteArray = Base64.decode(Prefs.headerImage!!, Base64.DEFAULT)
//            binding.ivHeaderImage.setImageBitmap(
//                BitmapFactory.decodeByteArray(
//                    encoded,
//                    0,
//                    encoded.size
//                )
//            )
//        }
        ImageSetter.loadImage(
            Prefs.printImageUrl,
            R.drawable.ic_place_holder,
            binding.ivHeaderImage
        )
        binding.date = Prefs.votingDate
        binding.time = Prefs.votingTime
        binding.footerMessage = Prefs.footerMessage
    }

    private fun checkAndRequestPermissions() {
        /*val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !hasPermissions(this, *BLUETOOTH_PERMISSIONS_11)
        ) {
            ActivityCompat.requestPermissions(this, BLUETOOTH_PERMISSIONS_11, 1)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S
            && !hasPermissions(this, *BLUETOOTH_PERMISSIONS)
        ) {
            ActivityCompat.requestPermissions(this, BLUETOOTH_PERMISSIONS, 1)
        } else {
            scanBluetoothPrinters()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                scanBluetoothPrinters()
            } else {
                Toast.makeText(this, "Permissions denied. Cannot proceed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanBluetoothPrinters() {
        val bluetoothDevicesList = BluetoothPrintersConnections().list ?: arrayOf()
        bluetoothPrinters.clear()
        bluetoothPrinters.addAll(bluetoothDevicesList.filterNotNull())
        val deviceList: ArrayList<String> = arrayListOf()
        val deviceNames = bluetoothPrinters.map { it.device?.name ?: "Unknown" }
        if (deviceNames.isNotEmpty()) {
            deviceList.add(getString(R.string.select_printer))
            deviceList.addAll(deviceNames)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrinters.adapter = adapter
        if (!Prefs.printerName.isNullOrEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                val index = deviceList.indexOf(Prefs.printerName)
                if (index != -1) {
                    binding.spinnerPrinters.setSelection(index)
                }
            }, 300)
        }

        binding.spinnerPrinters.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (position != 0) {
                        selectedPrinter = bluetoothPrinters.getOrNull(position - 1)
                        Prefs.printerName = deviceNames[position - 1]
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.rgMessageType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_general -> {
                    if (binding.rbGeneral.isChecked) {
                        Prefs.isGeneralMsg = true
                        CommonUtils.showToast(
                            this@SettingActivity,
                            getString(R.string.message_type_saved_successfully)
                        )
                    }
                }

                R.id.rb_voting -> {
                    if (binding.rbVoting.isChecked) {
                        Prefs.isGeneralMsg = false
                        CommonUtils.showToast(
                            this@SettingActivity,
                            getString(R.string.message_type_saved_successfully)
                        )
                    }
                }
            }
        }
        binding.rgImageMessageType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_with_image -> {
                    if (binding.rbWithImage.isChecked) {
                        Prefs.isWithImageMsg = true
                        CommonUtils.showToast(
                            this@SettingActivity,
                            getString(R.string.message_type_saved_successfully)
                        )
                    }
                }

                R.id.rb_without_image -> {
                    if (binding.rbWithoutImage.isChecked) {
                        Prefs.isWithImageMsg = false
                        CommonUtils.showToast(
                            this@SettingActivity,
                            getString(R.string.message_type_saved_successfully)
                        )
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_reload -> {
                getAppSettings()
            }

            R.id.iv_header_image -> {
                showTakeImagePopup(cropLauncher)
            }

            R.id.btn_save -> {
                if (binding.etVotingDate.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(
                        this,
                        getString(R.string.please_enter_voting_date)
                    )
                } else if (binding.etVotingTime.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(
                        this,
                        getString(R.string.please_enter_voting_time)
                    )
                } else if (binding.etFooterMessage.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(
                        this,
                        getString(R.string.please_enter_footer_message)
                    )
                } else {
                    if (updatedImageFile != null) {
                        val image = convertImageFileToBase64(updatedImageFile!!)
                        Prefs.headerImage = image
                    }
                    Prefs.votingDate = binding.etVotingDate.text.toString().trim()
                    Prefs.votingTime = binding.etVotingTime.text.toString().trim()
                    Prefs.footerMessage = binding.etFooterMessage.text.toString().trim()
                    CommonUtils.showToast(
                        this,
                        getString(R.string.setting_save_successfully)
                    )
                    finish()
                }
            }
        }
    }

    private fun getAppSettings() {
        if (CommonUtils.isNetworkAvailable(this)) {
            CustomProgressDialog.showProgressDialog(this)
            voterViewModel.getAppSetting().observe(this) { response ->
                CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    CommonUtils.showToast(this, getString(R.string.setting_reloaded_successfully))
                    Prefs.setting = response.info
                    Prefs.shareImageUrl = response.info?.shareImage
                    Prefs.printImageUrl = response.info?.printImage
                    Prefs.votingDate = response.info?.votingDate
                    Prefs.votingTime = response.info?.votingTime
                    Prefs.footerMessage = response.info?.massage
                    printImage = null
                    downloadShareImage()
                    init()
                } else if (response?.error != null) {
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }

    private fun downloadShareImage() {
        val url1 = if (!Prefs.shareImageUrl.isNullOrEmpty()) Prefs.shareImageUrl
        else "http://vishwainfotech.co.in/api/Kunaljadhav/images/111.jpg"
        DownloadTask().execute(stringToURL(url1))
    }

    fun convertImageFileToBase64(imageFile: File): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                imageFile.inputStream().use { inputStream ->
                    inputStream.copyTo(base64FilterStream)
                }
            }
            return@use outputStream.toString()
        }
    }

    private val cropLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
        if (uri?.path != null) {
            val imgPath = uri.path
            updatedImageFile = File(imgPath!!)
            if (updatedImageFile != null && updatedImageFile!!.exists()) {
                ImageSetter.loadImage(
                    updatedImageFile,
                    R.drawable.ic_place_holder,
                    binding.ivHeaderImage
                )
            }
        }
    }
}