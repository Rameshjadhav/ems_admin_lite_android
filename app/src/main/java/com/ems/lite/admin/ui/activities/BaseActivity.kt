package com.ems.lite.admin.ui.activities

import android.Manifest
import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.ems.lite.admin.FetchVoterService
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.LayoutNoInternetBinding
import com.ems.lite.admin.databinding.ToolbarLayoutBinding
import com.ems.lite.admin.di.viewmodel.BoothViewModel
import com.ems.lite.admin.di.viewmodel.VillageViewModel
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.request.DivisionListRequest
import com.ems.lite.admin.model.request.VillageListRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.CountBy
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.ContextWrapper
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.DateFormatter
import com.ems.lite.admin.utils.Logger
import com.ems.lite.admin.utils.Prefs
import com.google.common.collect.Lists
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    companion object {
        var selectedPrinter: BluetoothConnection? = null
        var shareImage: Bitmap? = null
        var printImage: Bitmap? = null

        val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )
        val PERMISSIONS_SMS = arrayOf(
            Manifest.permission.SEND_SMS
        )
        val PERMISSIONS_31 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )
        val WRITE_PERMISSIONS11 = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES
        )
        val WRITE_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
        val BLUETOOTH_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        @RequiresApi(Build.VERSION_CODES.S)
        val BLUETOOTH_PERMISSIONS_11 = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        const val PERMISSION_ALL = 5003
        const val PERMISSION_SMS = 5002
        fun hasPermission(context: Context?, permission: String): Boolean {
            if (context != null) {
                return ActivityCompat.checkSelfPermission(
                    context, permission
                ) == PackageManager.PERMISSION_GRANTED
            }
            return true
        }
    }

    protected val villageViewModel: VillageViewModel by viewModels()
    protected val boothViewModel: BoothViewModel by viewModels()
    protected val voterViewModel: VoterViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var toolbarLayoutBinding: ToolbarLayoutBinding
    private lateinit var tvToolbarTitle: AppCompatTextView
    private lateinit var tvToolbarSubTitle: AppCompatTextView

    open fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    override fun attachBaseContext(newBase: Context?) {
        val context = ContextWrapper.wrap(newBase!!, Locale(Prefs.lang))
        super.attachBaseContext(context)
    }

    protected fun syncTables() {
        progressDialog = ProgressDialog(this@BaseActivity)
        progressDialog.setTitle(resources.getString(R.string.app_name))
        progressDialog.setMessage("Please wait...")
        if (!Prefs.isVoterSync && !isMyServiceRunning(FetchVoterService::class.java)) {
//            getVoterList()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, FetchVoterService::class.java))
            } else {
                startService(Intent(this, FetchVoterService::class.java))
            }
        }
        if (!Prefs.isVillageSync) {
            getVillageMasterList(VillageListRequest())
        }

        if (!Prefs.isBoothSync) {
            getBoothList()
        }
        if (!Prefs.isCastSync) {
            getCastList()
        }
        if (!Prefs.isProfessionSync) {
            getProfessionList()
        }
        if (!Prefs.isDesignationSync) {
            getDesignationMasters()
        }
        if (!Prefs.isReligionSync) {
            getReligionMasters()
        }
    }

    private fun getCastList() {
        if (CommonUtils.isNetworkAvailable(this)) {
//            CustomProgressDialog.showProgressDialog(this)
            progressDialog.show()
            voterViewModel.getCastList().observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()
                progressDialog.dismiss()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.castList.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            voterViewModel.insertCast(response.castList!!)
                            Prefs.isCastSync = true
                        }
                    }
                } else if (response?.error != null) {
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }


    private fun getProfessionList() {
        if (CommonUtils.isNetworkAvailable(this)) {
//            CustomProgressDialog.showProgressDialog(this)
            progressDialog.show()
            voterViewModel.getProfessionList().observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()
                progressDialog.dismiss()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.list.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            voterViewModel.insertProfession(response.list!!)
                            Prefs.isProfessionSync = true
                        }
                    }
                } else if (response?.error != null) {
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }

    private fun getVillageMasterList(request: VillageListRequest) {
        if (CommonUtils.isNetworkAvailable(this)) {
//            CustomProgressDialog.showProgressDialog(this)
            progressDialog.show()
            villageViewModel.getVillageMasterList(request).observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()

                progressDialog.dismiss()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.list.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            villageViewModel.insertVillage(response.list!!)
                            Prefs.isVillageSync = true
                        }
                    }
                } else if (response?.error != null) {
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }

    private fun getBoothList() {
        if (CommonUtils.isNetworkAvailable(this)) {
//            CustomProgressDialog.showProgressDialog(this)
            progressDialog.show()
            boothViewModel.getBoothMasterList().observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()
                progressDialog.dismiss()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.boothList.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            boothViewModel.insertBooth(response.boothList!!)
                            Prefs.isBoothSync = true
                        }
                    }
                } else if (response?.error != null) {
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }

    private fun getDesignationMasters() {
        if (CommonUtils.isNetworkAvailable(this)) {
//            CustomProgressDialog.showProgressDialog(this)
            progressDialog.show()
            voterViewModel.getDesignationMasters().observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()
                progressDialog.dismiss()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.list.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            voterViewModel.insertDesignation(response.list!!)
                            Prefs.isDesignationSync = true
                        }
                    }
                } else if (response?.error != null) {
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }

    private fun getReligionMasters() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialog.show()
            voterViewModel.getReligionMasters().observe(this) { response ->
                progressDialog.dismiss()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.list.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            voterViewModel.insertReligion(response.list!!)
                            Prefs.isReligionSync = true
                        }
                    }
                } else if (response?.error != null) {
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }


    protected fun setUpToolNewBar(toolbarLayout: ToolbarLayoutBinding) {
        this.toolbarLayoutBinding = toolbarLayout
        val toolbar = toolbarLayout.toolbar.apply {
            setPadding(0, 0, 0, 0)
            setContentInsetsAbsolute(0, 0)
        }
        tvToolbarTitle = toolbarLayout.tvToolbarTitle
        tvToolbarSubTitle = toolbarLayout.tvToolbarSubTitle
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
            if (this@BaseActivity !is HomeActivity)
                setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(ContextCompat.getDrawable(this@BaseActivity, R.drawable.ic_back))
        }
    }

    protected fun changeToolBarBgColor(color: Int) {
        toolbarLayoutBinding.toolbarRoot.setBackgroundColor(ContextCompat.getColor(this, color))
    }

    protected fun setToolBarTitle(title: String?) {
        tvToolbarTitle.text = if (!title.isNullOrEmpty()) title else ""
    }

    protected fun setToolBarSubTitle(subTitle: String?) {
        tvToolbarSubTitle.text = if (!subTitle.isNullOrEmpty()) subTitle else ""
        tvToolbarSubTitle.visibility= if (!subTitle.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    protected fun initNoInternet(
        noInternetLayout: LayoutNoInternetBinding, onClickListener: View.OnClickListener
    ) {
        noInternetLayout.onClickListener = onClickListener
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun pickWhatsappPackageName(): String {

        val w4bPackageName = "com.whatsapp.w4b"
        val wpPackageName = "com.whatsapp"

        var packageName: String = w4bPackageName

        val pm = packageManager
        try {
            pm.getPackageInfo(
                w4bPackageName,
                PackageManager.GET_ACTIVITIES
            )
        } catch (exWhatsappForBusiness: PackageManager.NameNotFoundException) {
            try {
                pm.getPackageInfo(
                    wpPackageName,
                    PackageManager.GET_ACTIVITIES
                )
                packageName = wpPackageName
            } catch (exWhatsapp: PackageManager.NameNotFoundException) {
                packageName = wpPackageName
            }
        }

        return packageName
    }


    protected class DownloadTask :
        AsyncTask<URL?, Void?, Bitmap?>() {
        override fun onPreExecute() {
        }

        override fun doInBackground(vararg urls: URL?): Bitmap? {
            val url = urls[0]
            var connection: HttpURLConnection? = null
            try {
                connection = url?.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection!!.inputStream
                val bufferedInputStream = BufferedInputStream(inputStream)
                return BitmapFactory.decodeStream(bufferedInputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        protected open fun stringToURL(string: String?): URL? {
            try {
                return URL(string)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            return null
        }

        // When all async task done
        override fun onPostExecute(result: Bitmap?) {
            // Hide the progress dialog
            if (result != null) {
                shareImage = result
                if (printImage == null) {
                    val url1 =
                        if (!Prefs.printImageUrl.isNullOrEmpty()) Prefs.printImageUrl
                        else "http://vishwainfotech.co.in/api/Kunaljadhav/images/111.jpg"
                    DownloadHeaderImageTask().execute(stringToURL(url1))
                }
            }
        }
    }

    protected class DownloadHeaderImageTask :
        AsyncTask<URL?, Void?, Bitmap?>() {
        override fun onPreExecute() {
        }

        override fun doInBackground(vararg urls: URL?): Bitmap? {
            val url = urls[0]
            var connection: HttpURLConnection? = null
            try {
                connection = url?.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection!!.inputStream
                val bufferedInputStream = BufferedInputStream(inputStream)
                return BitmapFactory.decodeStream(bufferedInputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        // When all async task done
        override fun onPostExecute(result: Bitmap?) {
            // Hide the progress dialog
            if (result != null) {
                printImage = result
                val stream = ByteArrayOutputStream()
                result.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray: ByteArray = stream.toByteArray()
                Prefs.headerImage = Base64.getEncoder().encodeToString(byteArray)
            }
        }
    }

    protected open fun stringToURL(string: String?): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    private fun updateUrl() {

    }

    protected fun checkContacts(phone: String): Int {
        val uri =
            Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone))
        var found = 0
        val contentResolver = contentResolver
        val contact = contentResolver.query(
            uri, arrayOf(
                BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME
            ), null, null, null
        )
        try {
            if (contact != null && contact.count > 0) {
                contact.moveToNext()
                found = 1
            }
        } finally {
            contact?.close()
        }
        return found
    }

    protected var callNumber: String? = null

    protected fun makePhoneCall() {
        if (!hasPermission(this, Manifest.permission.CALL_PHONE)) {
            permissionLauncher.launch(Manifest.permission.CALL_PHONE)
        } else {
            Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${callNumber}")
            }.run {
                startActivity(this)
            }
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                makePhoneCall()
            } else {
                CommonUtils.showToast(this, getString(R.string.call_permission))
            }
        }


    private var excelFile: File? = null
    private var workbook = HSSFWorkbook()
    private lateinit var sheet: Sheet
    private var fileName: String? = null
    private var headerMessage: String? = null
    private val voterList: ArrayList<Voter> = arrayListOf()
    private val countList: ArrayList<CountBy> = arrayListOf()
    protected fun prepareExcelFile(
        fileName: String?, headerMessage: String?, voterList: ArrayList<Voter>
    ) {
        this.fileName = fileName
        this.headerMessage = headerMessage
        this.voterList.clear()
        this.voterList.addAll(voterList)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !ImagePicker.hasPermissions(this, *WRITE_PERMISSIONS11)
        ) {
            readWritePermissionLauncher.launch(WRITE_PERMISSIONS11)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            && !ImagePicker.hasPermissions(this, *WRITE_PERMISSIONS)
        ) {
            readWritePermissionLauncher.launch(WRITE_PERMISSIONS)
        } else {
            CustomProgressDialog.showProgressDialog(this)
            val listOfList: List<List<Voter>> = Lists.partition(voterList, 50000)
            val t = Thread() {
                listOfList.forEachIndexed { index, countBIES ->
                    sheet = workbook.createSheet("Sheet ${index + 1}")
                    sheet.setColumnWidth(0, (15 * 200))
                    sheet.setColumnWidth(1, (15 * 200))
                    sheet.setColumnWidth(2, (15 * 200))
                    sheet.setColumnWidth(3, (15 * 200))
                    sheet.setColumnWidth(4, (15 * 200))
                    sheet.setColumnWidth(5, (15 * 200))
                    sheet.setColumnWidth(6, (15 * 300))
                    sheet.setColumnWidth(7, (15 * 500))
                    sheet.setColumnWidth(8, (15 * 200))
                    sheet.setColumnWidth(9, (15 * 200))
                    sheet.setColumnWidth(10, (15 * 300))
                    sheet.setColumnWidth(11, (15 * 600))
                    sheet.setColumnWidth(12, (15 * 200))
                    sheet.setColumnWidth(13, (15 * 200))
                    sheet.setColumnWidth(14, (15 * 300))
                    sheet.setColumnWidth(15, (15 * 300))
                    sheet.setColumnWidth(16, (15 * 300))

                    val headerCellStyle1 = workbook.createCellStyle()
                    headerCellStyle1.fillForegroundColor = HSSFColor.GREY_25_PERCENT.index
                    headerCellStyle1.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
                    headerCellStyle1.alignment = CellStyle.ALIGN_LEFT
                    headerCellStyle1.wrapText = true
                    // Row 1
                    val headerRow: Row = sheet.createRow(0)
                    var cell = headerRow.createCell(0)
                    cell.setCellValue(headerMessage)
                    cell.cellStyle = headerCellStyle1
                    // Row 2
                    val headerCellStyle2 = workbook.createCellStyle()
                    headerCellStyle2.fillForegroundColor = HSSFColor.AQUA.index
                    headerCellStyle2.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
                    headerCellStyle2.alignment = CellStyle.ALIGN_CENTER
                    headerCellStyle2.wrapText = true

                    val headerRow2: Row = sheet.createRow(2)
                    cell = headerRow2.createCell(0)
                    cell.setCellValue("Assembly No")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(1)
                    cell.setCellValue("Division No")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(2)
                    cell.setCellValue("Village No")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(3)
                    cell.setCellValue("Booth No")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(4)
                    cell.setCellValue("Section No")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(5)
                    cell.setCellValue("Voter No")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(6)
                    cell.setCellValue("EPIC No")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(7)
                    cell.setCellValue("Name")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(8)
                    cell.setCellValue("Age")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(9)
                    cell.setCellValue("Gender")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(10)
                    cell.setCellValue("House No.")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(11)
                    cell.setCellValue("Address")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(12)
                    cell.setCellValue("Cast")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(13)
                    cell.setCellValue("Designation")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(14)
                    cell.setCellValue("Profession")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(15)
                    cell.setCellValue("Voter Status")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(16)
                    cell.setCellValue("Committee")
                    cell.cellStyle = headerCellStyle2

                    val cellStyleLeft = workbook.createCellStyle()
                    cellStyleLeft.fillForegroundColor = HSSFColor.WHITE.index
                    cellStyleLeft.alignment = CellStyle.ALIGN_LEFT
                    cellStyleLeft.wrapText = true
                    val cellStyleCenter = workbook.createCellStyle()
                    cellStyleCenter.fillForegroundColor = HSSFColor.WHITE.index
                    cellStyleCenter.alignment = CellStyle.ALIGN_CENTER
                    cellStyleCenter.wrapText = true

                    for (i in countBIES.indices) {
                        // Create a New Row for every new entry in list
                        val rowData = sheet.createRow(i + 3)

                        // Create Cells for each row
                        cell = rowData.createCell(0)
                        cell.setCellValue(countBIES[i].acNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(1)
                        cell.setCellValue(countBIES[i].divNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(2)
                        cell.setCellValue(countBIES[i].villageNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(3)
                        cell.setCellValue(countBIES[i].boothNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(4)
                        cell.setCellValue(countBIES[i].sectionNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(5)
                        cell.setCellValue(countBIES[i].voterNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(6)
                        cell.setCellValue(countBIES[i].cardNo)
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(7)
                        cell.setCellValue(countBIES[i].voterNameEng + "\n" + countBIES[i].voterName)
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(8)
                        cell.setCellValue(countBIES[i].age.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(9)
                        cell.setCellValue(countBIES[i].sex)
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(10)
                        cell.setCellValue(countBIES[i].houseNo)
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(11)
                        cell.setCellValue(countBIES[i].address)
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(12)
                        cell.setCellValue(countBIES[i].castNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(13)
                        cell.setCellValue(countBIES[i].designationNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(14)
                        cell.setCellValue(countBIES[i].professionNo.toString())
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(15)
                        cell.setCellValue(countBIES[i].voterStatusName)
                        cell.cellStyle = cellStyleCenter
                        cell = rowData.createCell(16)
                        cell.setCellValue(countBIES[i].committeeDesignation)
                        cell.cellStyle = cellStyleCenter
                    }
                    if (index == listOfList.size - 1) {
                        shareOnWhatsApp()
                    }
                }
            }
            t.start()
        }
    }

    protected fun prepareCountExcelFile(
        fileName: String?, headerMessage: String?, countByList: ArrayList<CountBy>
    ) {
        this.fileName = fileName
        this.headerMessage = headerMessage
        this.countList.clear()
        this.countList.addAll(countByList)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !ImagePicker.hasPermissions(this, *WRITE_PERMISSIONS11)
        ) {
            CustomProgressDialog.dismissProgressDialog()
            readWritePermissionLauncher.launch(WRITE_PERMISSIONS11)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            && !ImagePicker.hasPermissions(this, *WRITE_PERMISSIONS)
        ) {
            CustomProgressDialog.dismissProgressDialog()
            readWritePermissionLauncher.launch(WRITE_PERMISSIONS)
        } else {
            CustomProgressDialog.showProgressDialog(this)
            val listOfList: List<List<CountBy>> = Lists.partition(countByList, 50000)
            val t = Thread() {
                listOfList.forEachIndexed { index, countBIES ->
                    sheet = workbook.createSheet("Sheet ${index + 1}")
                    sheet.setColumnWidth(0, (15 * 400))
                    sheet.setColumnWidth(1, (15 * 300))
                    val headerCellStyle1 = workbook.createCellStyle()
                    headerCellStyle1.fillForegroundColor = HSSFColor.GREY_25_PERCENT.index
                    headerCellStyle1.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
                    headerCellStyle1.alignment = CellStyle.ALIGN_LEFT
                    headerCellStyle1.wrapText = true
                    // Row 1
                    val headerRow: Row = sheet.createRow(0)
                    var cell = headerRow.createCell(0)
                    cell.setCellValue(headerMessage)
                    cell.cellStyle = headerCellStyle1
                    // Row 2
                    val headerCellStyle2 = workbook.createCellStyle()
                    headerCellStyle2.fillForegroundColor = HSSFColor.AQUA.index
                    headerCellStyle2.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
                    headerCellStyle2.alignment = CellStyle.ALIGN_CENTER
                    headerCellStyle2.wrapText = true
                    val headerRow2: Row = sheet.createRow(2)
                    cell = headerRow2.createCell(0)
                    cell.setCellValue("Name")
                    cell.cellStyle = headerCellStyle2
                    cell = headerRow2.createCell(1)
                    cell.setCellValue("Count")
                    cell.cellStyle = headerCellStyle2
                    val cellStyleLeft = workbook.createCellStyle()
                    cellStyleLeft.fillForegroundColor = HSSFColor.WHITE.index
                    cellStyleLeft.alignment = CellStyle.ALIGN_LEFT
                    cellStyleLeft.wrapText = true
                    val cellStyleCenter = workbook.createCellStyle()
                    cellStyleCenter.fillForegroundColor = HSSFColor.WHITE.index
                    cellStyleCenter.alignment = CellStyle.ALIGN_CENTER
                    cellStyleCenter.wrapText = true

                    for (i in countBIES.indices) {
                        // Create a New Row for every new entry in list
                        val rowData = sheet.createRow(i + 3)

                        // Create Cells for each row
                        cell = rowData.createCell(0)
                        cell.setCellValue(countBIES[i].getDisplayName())
                        cell.cellStyle = cellStyleLeft
                        cell = rowData.createCell(1)
                        cell.setCellValue(countBIES[i].totalCount.toString())
                        cell.cellStyle = cellStyleCenter
                    }
                    if (index == listOfList.size - 1) {
                        shareOnWhatsApp()
                    }
                }
            }
            t.start()
        }
    }

    private fun shareOnWhatsApp() {
        val storageState = Environment.getExternalStorageState()
        try {
            val mediaDir: File? = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES) // (1)
                }

                storageState == Environment.MEDIA_MOUNTED -> {
                    @Suppress("DEPRECATION")
                    (File(Environment.getExternalStorageDirectory().toString()))
                }

                else -> {
                    File(filesDir, "BizActive")
                }
            }
            if (mediaDir != null && !mediaDir.exists()) {
                mediaDir.mkdirs()
            }
            val name = "${fileName}_" +
                    DateFormatter.getFormattedDate(
                        Date().time, DateFormatter.yyyy_MM_dd_HH_mm_ss_dash
                    ) + ".xls"
            excelFile = File(mediaDir, name)
            if (!excelFile!!.exists()) {
                excelFile!!.createNewFile()
            }
            val fileInputStream = FileOutputStream(excelFile)
            workbook.write(fileInputStream)
            CustomProgressDialog.dismissProgressDialog()
            runOnUiThread {
                val uri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".utils.GenericFileProvider",
                    excelFile!!
                )
                val share = Intent("android.intent.action.MAIN")
                share.action = Intent.ACTION_SEND
                share.type = "image/*"
                share.putExtra(
                    Intent.EXTRA_STREAM,
                    uri
                )
                share.putExtra(Intent.EXTRA_TEXT, "Test Excel")
                share.setPackage(pickWhatsappPackageName())
                startActivity(Intent.createChooser(share, "Share Report"))
                workbook = HSSFWorkbook()
            }
        } catch (e: IOException) {
            Logger.d(ImagePicker.TAG, "Could not create file: $e")
            CustomProgressDialog.dismissProgressDialog()
        }
    }

    private val readWritePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
            if (!resultMap.containsValue(false)) {
                if (voterList.isNotEmpty())
                    prepareExcelFile(fileName, headerMessage, voterList)
                else
                    prepareCountExcelFile(fileName, headerMessage, countList)
            } else {
                CommonUtils.showToast(
                    this,
                    getString(R.string.write_permission_not_granted)
                )
            }
        }

    fun showHideProgress(show: Boolean) {
        if (show) {
            CustomProgressDialog.showProgressDialog(this)
        } else {
            CustomProgressDialog.dismissProgressDialog()
        }
    }

    protected fun textToBitmap(text: String, bottomSpace: Int = 15): Bitmap {
        val backgroundColor = Color.WHITE
        val textColor = Color.BLACK

        val paint = Paint().apply {
            isAntiAlias = true
            textSize = 25f
            color = textColor
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        val bitmapHeight = bounds.height() + bottomSpace
        val bitmap = Bitmap.createBitmap(bounds.width(), bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(backgroundColor)
        canvas.drawText(text, 0f, -bounds.top.toFloat(), paint)

        return bitmap
    }

    protected fun TitleBitmap(text: String, bottomSpace: Int = 22, fontSize: Float = 30f): Bitmap {
        val backgroundColor = Color.WHITE
        val textColor = Color.BLACK

        val paint = Paint().apply {
            isAntiAlias = true
            textSize = fontSize
            color = textColor
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        val bitmapHeight = bounds.height() + bottomSpace
        val bitmap = Bitmap.createBitmap(bounds.width(), bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(backgroundColor)
        canvas.drawText(text, 0f, -bounds.top.toFloat(), paint)

        return bitmap
    }

    protected fun footerBitmap(text: String, bottomSpace: Int = 10, fontSize: Float = 25f): Bitmap {
        val backgroundColor = Color.WHITE
        val textColor = Color.BLACK

        val paint = Paint().apply {
            isAntiAlias = true
            textSize = fontSize
            color = textColor
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        val bitmapHeight = bounds.height() + bottomSpace
        val bitmap = Bitmap.createBitmap(bounds.width(), bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(backgroundColor)
        canvas.drawText(text, 0f, -bounds.top.toFloat(), paint)

        return bitmap
    }

    protected fun wrapText(text: String, maxWidth: Int): List<String> {
        val wrappedLines = mutableListOf<String>()
        val words = text.split(" ")

        var currentLine = StringBuilder()
        for (word in words) {
            // Check if adding the new word will exceed the maxWidth
            if (currentLine.length + word.length + 1 > maxWidth) {
                // Add the current line to the list and start a new line
                wrappedLines.add(currentLine.toString().trim())
                currentLine = StringBuilder(word)
            } else {
                // Add the word to the current line
                if (currentLine.isNotEmpty()) {
                    currentLine.append(" ")
                }
                currentLine.append(word)
            }
        }
        // Add the last line if it's not empty
        if (currentLine.isNotEmpty()) {
            wrappedLines.add(currentLine.toString().trim())
        }

        return wrappedLines
    }

}