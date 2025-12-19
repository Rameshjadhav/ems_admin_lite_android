package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.lifecycle.lifecycleScope
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.SettingActivityBinding
import com.ems.lite.admin.model.request.UpdateSettingRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.ui.adapters.SpinnerAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.ImageSetter
import com.ems.lite.admin.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
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
    private val villageList: ArrayList<Village> = arrayListOf()
    private var imageType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.setting_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.settings))
        binding.rbGeneral.isChecked = Prefs.isGeneralMsg
        binding.rbVoting.isChecked = !Prefs.isGeneralMsg
        binding.rbWithImage.isChecked = Prefs.isWithImageMsg
        binding.rbWithoutImage.isChecked = !Prefs.isWithImageMsg
        binding.switchFullSearch.isChecked = Prefs.isFullSearch
        initObserver()
        initSpinnerAdapter()
        initClickListener()
        init()
    }

    private fun init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        loadVillageList()

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
        setData()
    }

    private fun setData() {
        val setting = Prefs.setting
        ImageSetter.loadImage(
            setting?.printImage, R.drawable.ic_place_holder, binding.ivPrintImage
        )
        ImageSetter.loadImage(
            setting?.shareImage, R.drawable.ic_place_holder, binding.ivShareImage
        )
        binding.date = setting?.votingDate?:""
        binding.time = setting?.votingTime?:""
        binding.footerMessage = setting?.message?:""
    }

    private fun initObserver() {
        lifecycleScope.launch {
            voterViewModel.getSettingState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            CommonUtils.showToast(
                                this@SettingActivity,
                                getString(R.string.setting_reloaded_successfully)
                            )
                            val response = it.data
                            Prefs.setting = response.info
                            selectedVillageSetting = it.data.info
                            addInSettingList()
                            init()
                        } else {
                            CommonUtils.showErrorMessage(this@SettingActivity, it.message)
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showErrorMessage(this@SettingActivity, it.message)
                    }
                }
            }
        }
        lifecycleScope.launch {
            voterViewModel.uploadPhotoState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            updatedImageFile = null
                            imageType = ""
                            binding.btnUploadShareImage.visibility = View.GONE
                            binding.btnUploadPrintImage.visibility = View.GONE
                            getSettings()
                        } else {
                            CommonUtils.showErrorMessage(this@SettingActivity, it.message)
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showErrorMessage(this@SettingActivity, it.message)
                    }
                }
            }
        }

        lifecycleScope.launch {
            voterViewModel.updateSettingState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            CommonUtils.showToast(
                                this@SettingActivity, getString(R.string.setting_save_successfully)
                            )
                            getSettings()
                        } else {
                            CommonUtils.showErrorMessage(this@SettingActivity, it.message)
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showErrorMessage(this@SettingActivity, it.message)
                    }
                }
            }
        }

    }

    private fun loadVillageList() {
        CoroutineScope(Dispatchers.Main).launch {
            val list = villageViewModel.getDB().villageDao().getAll()
            villageList.clear()
            if (!list.isNullOrEmpty()) {
                villageList.addAll(list.filter { village -> village.villageNo != 0L })
            }
            binding.villageAdapter?.notifyDataSetChanged()
        }
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
                        selectedPrinter = bluetoothPrinters.getOrNull(position - 1)?.device
                        Prefs.printerName = deviceNames[position - 1]
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.switchFullSearch.setOnCheckedChangeListener { buttonView, isChecked ->
            Prefs.isFullSearch = isChecked
        }
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
                getSettings()
            }

            R.id.iv_share_image -> {
                if (imageType == Enums.ImageType.PRINT_IMAGE.toString() && updatedImageFile != null) {
                    CommonUtils.showErrorMessage(this, getString(R.string.pls_upload_print_image))
                } else {
                    imageType = Enums.ImageType.SHARE_IMAGE.toString()
                    showTakeImagePopup(cropLauncher)
                }
            }

            R.id.btn_upload_share_image -> {
                if (imageType == Enums.ImageType.SHARE_IMAGE.toString() && updatedImageFile != null) {
                    val photoRequestFile: RequestBody = File(updatedImageFile!!.absolutePath)
                        .asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val photoFilePart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "file", updatedImageFile!!.name, photoRequestFile
                    )
                    voterViewModel.uploadPhoto(
                        villageList[binding.spinnerVillage.selectedItemPosition].villageNo,
                        imageType, photoFilePart
                    )
                } else {
                    CommonUtils.showErrorMessage(this, getString(R.string.pls_select_share_image))
                }
            }

            R.id.iv_print_image -> {
                if (imageType == Enums.ImageType.SHARE_IMAGE.toString() && updatedImageFile != null) {
                    CommonUtils.showErrorMessage(this, getString(R.string.pls_upload_share_image))
                } else {
                    imageType = Enums.ImageType.PRINT_IMAGE.toString()
                    showTakeImagePopup(cropLauncher)
                }
            }

            R.id.btn_upload_print_image -> {
                if (imageType == Enums.ImageType.PRINT_IMAGE.toString() && updatedImageFile != null) {
                    val photoRequestFile: RequestBody = File(updatedImageFile!!.absolutePath)
                        .asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val photoFilePart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "file", updatedImageFile!!.name, photoRequestFile
                    )
                    voterViewModel.uploadPhoto(
                        villageList[binding.spinnerVillage.selectedItemPosition].villageNo,
                        imageType, photoFilePart
                    )
                } else {
                    CommonUtils.showErrorMessage(this, getString(R.string.pls_select_print_image))
                }
            }


            R.id.btn_save -> {
                if (binding.etVotingDate.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(
                        this, getString(R.string.please_enter_voting_date)
                    )
                } else if (binding.etVotingTime.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(
                        this, getString(R.string.please_enter_voting_time)
                    )
                } else if (binding.etFooterMessage.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(
                        this, getString(R.string.please_enter_footer_message)
                    )
                } else {
//                    if (updatedImageFile != null) {
//                        val image = convertImageFileToBase64(updatedImageFile!!)
//                        Prefs.headerImage = image
//                    }
//                    Prefs.votingDate = binding.etVotingDate.text.toString().trim()
//                    Prefs.votingTime = binding.etVotingTime.text.toString().trim()
//                    Prefs.footerMessage = binding.etFooterMessage.text.toString().trim()
                    voterViewModel.updateSetting(
                        UpdateSettingRequest(
                            Prefs.user?.userId,
                            villageList[binding.spinnerVillage.selectedItemPosition].villageNo,
                            binding.etVotingDate.text.toString().trim(),
                            binding.etVotingTime.text.toString().trim(),
                            binding.etFooterMessage.text.toString().trim()
                        )
                    )
                }
            }
        }
    }

    private fun getSettings() {
        if (CommonUtils.isNetworkAvailable(this)) {
            CustomProgressDialog.showProgressDialog(this)
            voterViewModel.getSetting(villageList[binding.spinnerVillage.selectedItemPosition].villageNo)
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
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

    private fun initSpinnerAdapter() {
        binding.villageAdapter = SpinnerAdapter(
            this, R.layout.spinner_item, villageList, -1
        )
        binding.spinnerVillage.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View, position: Int, id: Long
                ) {
                    CommonUtils.updateDisabledPositionInSpinner(
                        context, parent, position, binding.villageAdapter!!.disabledPosition
                    )
                    getSettings()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

    }

    private val cropLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
        if (uri?.path != null) {
            val imgPath = uri.path
            updatedImageFile = File(imgPath!!)
            if (updatedImageFile != null && updatedImageFile!!.exists()) {
                if (imageType == Enums.ImageType.SHARE_IMAGE.toString()) {
                    ImageSetter.loadImage(
                        updatedImageFile, R.drawable.ic_place_holder, binding.ivShareImage
                    )
                    binding.btnUploadShareImage.visibility = View.VISIBLE
                    binding.btnUploadPrintImage.visibility = View.GONE
                } else if (imageType == Enums.ImageType.PRINT_IMAGE.toString()) {
                    ImageSetter.loadImage(
                        updatedImageFile, R.drawable.ic_place_holder, binding.ivPrintImage
                    )
                    binding.btnUploadShareImage.visibility = View.GONE
                    binding.btnUploadPrintImage.visibility = View.VISIBLE
                } else {
                    binding.btnUploadShareImage.visibility = View.GONE
                    binding.btnUploadPrintImage.visibility = View.GONE
                }
            }
        }
    }
}