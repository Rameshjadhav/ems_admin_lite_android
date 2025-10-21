package com.ems.lite.admin.ui.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityFamilyBinding
import com.ems.lite.admin.databinding.DialogUpdateVoterDetailsBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.interfaces.DialogClickListener
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Cast
import com.ems.lite.admin.model.table.Profession
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.ui.adapters.FamilyMemberListAdapter
import com.ems.lite.admin.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*

@AndroidEntryPoint
class FamilyActivity : BaseActivity(), OnClickListener {

    companion object {
        fun startActivityForResult(
            activity: Activity, voterId: Int?, launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, FamilyActivity::class.java).apply {
                putExtra(IntentConstants.VOTER_ID, voterId)
            }.run {
                launcher.launch(this)
            }
        }

        fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }

    private lateinit var binding: ActivityFamilyBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val voterList: ArrayList<Voter> = arrayListOf()
    private val castList: ArrayList<Cast> = arrayListOf()
    private val professionList: ArrayList<Profession> = arrayListOf()
    private lateinit var voterListAdapter: FamilyMemberListAdapter
    private var booth: Booth? = null
    private var voter: Voter? = null
    private var shareNumber = ""
    private val date = Prefs.votingDate
    private val time = Prefs.votingTime
    private var initialized = false
    private var selectedStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.family))
        initClickListener()
        initAdapter()
        init()
    }

    private fun init() {
        initCastSpinner()
        intent?.run {
            val voterId = getIntExtra(IntentConstants.VOTER_ID, 0)
            CoroutineScope(Dispatchers.Main).launch {
                val v = commonViewModel.getDB().voterDao()
                    .getVoterById(voterId!!)
                voter = v
                searchVoter()
            }
        }
    }

    private fun searchVoter() {
        CustomProgressDialog.showProgressDialog(this)
        CoroutineScope(Dispatchers.Main).launch {
            val list = commonViewModel.getDB().voterDao()
                .getFamilyList(voter?.houseNo)
            voterList.clear()
            if (!list.isNullOrEmpty()) {
                voterList.addAll(list)
                prefilledMobile()
                booth = commonViewModel.getDB().BoothDao().get(voter?.boothNo)
                updateFamilyCompleteBtn()

//                binding.etMobno.setText(if (!voter?.mobileNo.isNullOrEmpty()) voter?.mobileNo else "")
                Handler(Looper.getMainLooper()).postDelayed({
                    if (voter?.castNo != 0L && castList.isNotEmpty()) {
                        val index = castList.indexOfFirst { it.castNo == voter?.castNo }
                        if (index != -1)
                            binding.spCast.setSelection(index)
                    }
                }, 300)
                if (!voter?.voterStatusName.isNullOrEmpty()) {
                    selectedStatus = voter!!.voterStatusName
                }
                updateStatusView()
            }
            voterListAdapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                CustomProgressDialog.dismissProgressDialog()
            }, 300)
        }
    }

    private fun prefilledMobile() {
        val familyHead = voterList.find { it.familyHead == 1 }
        if (familyHead != null) {
            binding.etMobno.setText(familyHead.mobileNo ?: "")
        }
    }

    private fun updateFamilyCompleteBtn() {
        val isNotCompleted = voterList.find { it.completedFamily == 0 }
        val isUnknown = voterList.find { it.completedFamily == 2 }
        if (isNotCompleted != null || isUnknown != null) {
            binding.btnCompleteFamily.text = getString(R.string.complete_family)
            binding.btnCompleteFamily.isEnabled = true
            binding.btnCompleteFamily.alpha = 1.0f
        } else {
            binding.btnCompleteFamily.text = getString(R.string.completed)
            binding.btnCompleteFamily.isEnabled = false
            binding.btnCompleteFamily.alpha = 0.5f
        }

    }

    private fun updateStatusView() {
        binding.selectedImg.setImageResource(
            if (!selectedStatus.isNullOrEmpty()) {
                when (selectedStatus) {
                    Enums.Status.GREEN.toString() -> R.drawable.green
                    Enums.Status.YELLOW.toString() -> R.drawable.yellow
                    Enums.Status.OTHER.toString() -> R.drawable.other
                    Enums.Status.RED.toString() -> R.drawable.red
                    else -> R.drawable.nocolor
                }
            } else {
                R.drawable.nocolor
            }
        )
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgNoColor -> {
                selectedStatus = Enums.Status.SELECT.toString()
                updateStatusView()
            }

            R.id.imgGreen -> {
                selectedStatus = Enums.Status.GREEN.toString()
                updateStatusView()
            }

            R.id.imgYellow -> {
                selectedStatus = Enums.Status.YELLOW.toString()
                updateStatusView()
            }

            R.id.imgOther -> {
                selectedStatus = Enums.Status.OTHER.toString()
                updateStatusView()
            }

            R.id.imgRed -> {
                selectedStatus = Enums.Status.RED.toString()
                updateStatusView()
            }

            R.id.btn_update -> {
                updateDetails()
            }

            R.id.btn_complete_family -> {
                if (voterList.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val mobile = binding.etMobno.text.toString().trim()
                        val cast = castList[binding.spCast.selectedItemPosition]
                        voterList.forEach {
                            it.updated = 1
                            it.completedFamily = 1
                            if (mobile.isNotEmpty() && mobile.length == 10) {
                                it.mobileNo = mobile
                            }
                            if (cast.castNo != 0L) {
                                it.castNo = cast.castNo
                            }
                            if (!selectedStatus.isNullOrEmpty() && selectedStatus != Enums.Status.SELECT.toString()) {
                                it.voterStatusName = selectedStatus
                            }
                        }
                        commonViewModel.getDB().voterDao()
                            .insert(voterList)
                        CommonUtils.showToast(
                            this@FamilyActivity, getString(R.string.voter_updated_successfully)
                        )
                    }
                }
            }

            R.id.btn_unknown_voter -> {
                if (voterList.isNotEmpty()) {
                    val user = Prefs.user
                    CoroutineScope(Dispatchers.Main).launch {
                        voterList.forEach {
                            it.updated = 1
                            it.completedFamily = 2
                            it.userId = user?.userId?.toLong() ?: 0
                        }
                        commonViewModel.getDB().voterDao()
                            .insert(voterList)
                        CommonUtils.showToast(
                            this@FamilyActivity, getString(R.string.voter_updated_successfully)
                        )
                    }
                }
            }

            R.id.btn_whatsapp -> {
                val extra = "91"
                val number = binding.etMobno.getText().toString().filter { !it.isWhitespace() }
                if (number.isNotEmpty()) {
                    val nosize = number.length

                    if (nosize == 10) {
                        shareNumber = (extra + number).toString()
                    }
                    if (nosize == 13) {
                        shareNumber = number
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermissions(
                            this,
                            *PERMISSIONS_31
                        )
                    ) {
                        ActivityCompat.requestPermissions(
                            this, PERMISSIONS_31,
                            PERMISSION_ALL
                        )
                    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                        && !hasPermissions(this, *PERMISSIONS)
                    ) {
                        ActivityCompat.requestPermissions(
                            this, PERMISSIONS,
                            PERMISSION_ALL
                        )
                    } else {
//                        shareFromRaw()
                        shareItemFromServer()
                    }
                } else {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_valid_mobile_number))
                }
            }

            R.id.btn_send_sms -> {
                if (binding.etMobno.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(this, getString(R.string.enter_your_mobile_no))
                } else if (binding.etMobno.text.toString().trim().length < 10) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_valid_mobile_number))
                } else if (!FamilyActivity.hasPermissions(
                        this,
                        *PERMISSIONS_SMS
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this, PERMISSIONS_SMS,
                        PERMISSION_SMS
                    )
                } else {
                    sendSMSMessage()
                }
            }

            R.id.btn_print -> {
                if (selectedPrinter != null) {
                    print()
                } else {
                    CommonUtils.showErrorMessage(
                        this,
                        getString(R.string.pls_set_printer_from_setting)
                    )
                }
            }
        }
    }

    private fun print() {
        selectedPrinter?.let { printerConnection ->
            try {
                val printer = EscPosPrinter(printerConnection, 203, 48f, 32)
                // Define maximum width based on your printer's width
                val maxWidth = 40 // Adjust this based on your printer's width for the main content
                val titleBitmap = TitleBitmap(getString(R.string.voter_detail))

                val dateBitmaps = wrapText(
                    getString(R.string.voting_date) + " :-  " + date,
                    maxWidth
                ).map { textToBitmap(it) }

                val timeBitmaps = wrapText(
                    getString(R.string.voting_time) + " :-  " + time,
                    maxWidth
                ).map { textToBitmap(it) }


                val formattedText = buildString {

                    if (printImage != null && Prefs.isWithImageMsg)
                        append(
                            "[C]<img>${
                                PrinterTextParserImg.bitmapToHexadecimalString(
                                    printer, printImage
                                )
                            }</img>\n"
                        )
                    append(
                        "[C]<img>${
                            PrinterTextParserImg.bitmapToHexadecimalString(
                                printer, titleBitmap
                            )
                        }</img>\n"
                    )

                    voterList.forEach { voter ->
                        val nameBitmaps = wrapText(
                            getString(R.string.voter_name) + " :-  " + voter.voterName,
                            maxWidth
                        ).map { textToBitmap(it) }

                        val voterNoBitmaps = wrapText(
                            getString(R.string.voter_no) + " :-  " + voter.voterNo,
                            maxWidth
                        ).map { textToBitmap(it) }

                        val epicNoBitmaps = wrapText(
                            getString(R.string.epic_no) + " :-  " + voter.cardNo,
                            maxWidth
                        ).map { textToBitmap(it) }

                        nameBitmaps.forEach { bitmap ->
                            append(
                                "[L]<img>${
                                    PrinterTextParserImg.bitmapToHexadecimalString(
                                        printer, bitmap
                                    )
                                }</img>\n"
                            )
                        }
                        voterNoBitmaps.forEach { bitmap ->
                            append(
                                "[L]<img>${
                                    PrinterTextParserImg.bitmapToHexadecimalString(
                                        printer, bitmap
                                    )
                                }</img>\n"
                            )
                        }
                        epicNoBitmaps.forEach { bitmap ->
                            append(
                                "[L]<img>${
                                    PrinterTextParserImg.bitmapToHexadecimalString(
                                        printer, bitmap
                                    )
                                }</img>\n\n"
                            )
                        }
                    }

                    val pollingStationBitmaps = wrapText(
                        getString(R.string.polling_station) + " :- " + booth?.getName(),
                        maxWidth
                    ).map { textToBitmap(it) }

                    pollingStationBitmaps.forEach { bitmap ->
                        append(
                            "[L]<img>${
                                PrinterTextParserImg.bitmapToHexadecimalString(
                                    printer, bitmap
                                )
                            }</img>\n"
                        )
                    }
                    append("------------------\n")

                    dateBitmaps.forEach { bitmap ->
                        append(
                            "[L]<img>${
                                PrinterTextParserImg.bitmapToHexadecimalString(
                                    printer, bitmap
                                )
                            }</img>\n"
                        )
                    }
                    timeBitmaps.forEach { bitmap ->
                        append(
                            "[L]<img>${
                                PrinterTextParserImg.bitmapToHexadecimalString(
                                    printer, bitmap
                                )
                            }</img>\n"
                        )
                    }
                }
                printer.printFormattedText(formattedText)
            } catch (e: Exception) {
                Toast.makeText(this, "Error printing: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No printer selected.", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareItemFromServer() {
        if (Prefs.isGeneralMsg || !Prefs.isWithImageMsg) {
            shareGeneralWhatsappMessage()
        } else {

            if (shareImage != null) {
                shareImageWhatsApp(shareImage!!)
            } else {
                val url1 = if (!Prefs.shareImageUrl.isNullOrEmpty()) Prefs.shareImageUrl
                else "http://vishwainfotech.co.in/api/Kunaljadhav/images/111.jpg"
                DownloadTask().execute(stringToURL(url1))
            }
        }
    }

    private fun shareGeneralWhatsappMessage() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        shareNumber = shareNumber.replace("+", "")
        if (shareNumber.length == 10) {
            shareNumber = "91$shareNumber"
        }
        val msg = generateMessage(true)
        share.putExtra(Intent.EXTRA_TEXT, msg)
        share.putExtra(
            "jid",
            PhoneNumberUtils.stripSeparators(shareNumber) + "@s.whatsapp.net"
        )
        share.setPackage(pickWhatsappPackageName())
        startActivity(Intent.createChooser(share, "Share Image"))
    }

    fun shareImageWhatsApp(bmp: Bitmap) {
        shareNumber = shareNumber.replace("+", "")
        if (checkContacts(shareNumber) == 1) {
            val time = System.currentTimeMillis()
            val share = Intent("android.intent.action.MAIN")
            share.action = Intent.ACTION_SEND
            share.type = "image/*"
            val bytes = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val f = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + "temporary_file_${time}.jpg"
            )
            try {
                if (f.exists()) {
                    f.delete()
                }
                f.createNewFile()
                FileOutputStream(f).write(bytes.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
            shareNumber = shareNumber.replace("+", "")
            if (shareNumber.length == 10) {
                shareNumber = "91$shareNumber"
            }
            Log.e("aaa", shareNumber + "-")
            share.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + File.separator + "temporary_file_${time}.jpg"
                )
            )
            val msg = generateMessage(true)
            share.putExtra(Intent.EXTRA_TEXT, msg)
            share.putExtra(
                "jid",
                PhoneNumberUtils.stripSeparators(shareNumber) + "@s.whatsapp.net"
            )
            share.addFlags(268435456)
//        if (isPackageInstalled("com.whatsapp", this)) {
            share.setPackage(pickWhatsappPackageName())
            startActivity(Intent.createChooser(share, "Share Image"))
        } else {
            val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, shareNumber)
            val name = if (voter != null) {
                voter!!.getFullName()
            } else {
                shareNumber
            }
            intent.putExtra(ContactsContract.Intents.Insert.NAME, "Z-$name")
            startActivity(intent)
        }
    }

    private fun sendSMSMessage() {
        try {
            val msg = generateMessage(true)
//            val smsIntent = Intent(Intent.ACTION_VIEW)
//            smsIntent.type = "vnd.android-dir/mms-sms"
//            smsIntent.putExtra("address", binding.etMobno.text.toString())
//            smsIntent.putExtra("sms_body", msg)
//            startActivity(smsIntent)
            val smsManager: SmsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(msg)
            smsManager.sendMultipartTextMessage(
                binding.etMobno.text.toString(),
                null,
                parts,
                null,
                null
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun generateMessage(isAddFooter: Boolean): String {
        var msg = ""
        if (Prefs.isGeneralMsg) {
            if (!voter?.message.isNullOrEmpty())
                msg += voter?.message
            if (!voter?.image.isNullOrEmpty()) {
                if (msg.isNotEmpty())
                    msg += "\n\n"
                msg += voter?.image
            }
        } else {
            if (voterList.isNotEmpty()) {
                voterList.forEach {
                    msg += getString(R.string.voter_name) + " :-  " + it.voterName +
                            "\n" + getString(R.string.voter_no) + " :-  " + it.voterNo +
                            "\n" + getString(R.string.epic_no) + " :-  " + it.cardNo +
                            "\n" + getString(R.string.polling_station) + " :- " + booth?.getName()
                    msg += "\n-------------------------------------------\n"
                }
            }
            if (isAddFooter && !Prefs.footerMessage.isNullOrEmpty()) {
                msg += Prefs.footerMessage + "\n"
            }

            if (!date.isNullOrEmpty() && !time.isNullOrEmpty()) {
                msg += getString(R.string.voting_date) + " :-  " + date +
                        "\n" + getString(R.string.voting_time) + " :-  " + time
            }
        }
        return msg
    }

    private fun updateDetails() {
        val mobile = binding.etMobno.text.toString().trim()
        val cast = castList[binding.spCast.selectedItemPosition]
        if (mobile.isNotEmpty() && mobile.length < 10) {
            CommonUtils.showToast(this, getString(R.string.pls_enter_valid_mobile_number))
        } else {

            for (i in voterList.indices) {
                val voter = voterList[i]
                if (mobile.isNotEmpty() && voter.mobileNo.isNullOrEmpty()) {
                    voter.mobileNo = mobile
                }
                if (cast.castNo != 0L) {
                    voter.castNo = cast.castNo
                }
                if (!selectedStatus.isNullOrEmpty() && selectedStatus != Enums.Status.SELECT.toString()) {
                    voter.voterStatusName = selectedStatus
                }
                voter.updated = 1
                CoroutineScope(Dispatchers.Main).launch {
                    commonViewModel.getDB().voterDao().insert(voter)
                    if (i == voterList.size - 1) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }
    }

    private fun shareFromRaw() {
        try {
            val bmp = BitmapFactory.decodeResource(
                resources,
                R.drawable.mainimage
            )
            val share = Intent("android.intent.action.MAIN")
            share.action = Intent.ACTION_SEND
            share.type = "image/*"
            val bytes = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val f = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + "temporary_file.jpg"
            )
            try {
                f.createNewFile()
                FileOutputStream(f).write(bytes.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
            shareNumber = shareNumber.replace("+", "")
            if (shareNumber.length == 10) {
                shareNumber = "91$shareNumber"
            }
            Log.e("aaa", shareNumber + "-")
            share.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + File.separator + "temporary_file.jpg"
                )
            )
            val msg = generateMessage(true)
            share.putExtra(Intent.EXTRA_TEXT, msg)
            share.putExtra("jid", PhoneNumberUtils.stripSeparators(shareNumber) + "@s.whatsapp.net")

//        if (isPackageInstalled("com.whatsapp", this)) {
            share.setPackage(pickWhatsappPackageName())
            startActivity(Intent.createChooser(share, "Share Image"))
//        } else {
//            CommonUtils.showToast(applicationContext, "Please Install Whatsapp")
//        }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        /*val bmpUri = getLocalBitmapUri(bmp) // see previous remote images section

        shareImageWhatsApp(bmpUri)*/
    }

    private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        val storage = Environment.getExternalStorageState()
        val path: File? =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                }

                storage == Environment.MEDIA_MOUNTED -> {
                    File(Environment.getExternalStorageDirectory().toString())
                }

                else -> {
                    File(filesDir, getString(R.string.app_name))
                }
            }

// Make sure the path directory exists.
        if (path != null && !path.exists()) {
// Make it, if it doesn't exit
            path.mkdirs()
        }
        val file = File(path, "temp_image.png")
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val out: FileOutputStream?
        try {
            out = FileOutputStream(file)
            out.write(bytes.toByteArray())
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            bmpUri = FileProvider.getUriForFile(
                this,
                applicationContext
                    .packageName + ".utils.GenericFileProvider", file
            )

            //Uri.fromFile(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isPermissionGranted = true
        when (requestCode) {
            PERMISSION_ALL -> {
                for (i in grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = false
                        break
                    }
                }
                if (isPermissionGranted) {
//                    shareFromRaw()
                    shareItemFromServer()
                } else {
                    CommonUtils.showToast(this, getString(R.string.permission_denied))
                }
            }

            PERMISSION_SMS -> {
                for (i in grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = false
                        break
                    }
                }
                if (isPermissionGranted) {
                    sendSMSMessage()
                } else {
                    CommonUtils.showToast(this, getString(R.string.permission_denied))
                }
            }
        }

    }

    private fun initAdapter() {
        voterListAdapter = FamilyMemberListAdapter(voterList).apply {
            voterClickListener = object : FamilyMemberListAdapter.VoterClickListener {
                override fun onItemClick(voter: Voter) {
                    showUpdateVoterDialog(voter)
                }

                override fun onCallClick(mobileNo: String?) {
                    callNumber = mobileNo
                    makePhoneCall()
                }

                override fun removeFamilyMembe(voter: Voter) {
                    AlertDialogManager.showConfirmationDialog(
                        this@FamilyActivity,
                        getString(R.string.app_name),
                        getString(R.string.are_you_sure_want_to_remove_this_voter_in_this_family),
                        getString(R.string.yes),
                        button1Drawable = R.drawable.rc_theme_filled_c25,
                        button2Message = getString(R.string.no),
                        button2Drawable = R.drawable.rc_red_filled_c25,
                        dialogClickListener = object : DialogClickListener {
                            override fun onButton1Clicked() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    commonViewModel.getDB().voterDao()
                                        .updateVoterUnderFamily(voter._id, voter.houseNo + "_")
                                    voterList.remove(voter)
                                    voterListAdapter?.notifyDataSetChanged()
                                }
                            }

                            override fun onButton2Clicked() {
                            }

                            override fun onCloseClicked() {
                            }

                        }
                    )
                }

                override fun onHeadChanged(voter: Voter) {
                    CoroutineScope(Dispatchers.Main).launch {
                        commonViewModel.getDB().voterDao().insert(voter)
                        prefilledMobile()
                    }
                }

                override fun onRelativeClick(voter: Voter) {
                    RelativeListActivity.startActivity(this@FamilyActivity, voter?.cardNo)
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.scheduleLayoutAnimation()
        binding.recyclerView.adapter = voterListAdapter

    }

    private fun showUpdateVoterDialog(voter: Voter?) {

        val updateDialogBinding: DialogUpdateVoterDetailsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this@FamilyActivity),
            R.layout.dialog_update_voter_details, null, false
        )

        fun initProfessionSpinner() {

            val arrayAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, professionList)
            updateDialogBinding.spProfession.adapter = arrayAdapter
            if (professionList.isEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val list = commonViewModel.getDB().ProfessionDao().getAll()
                    if (!list.isNullOrEmpty()) {
                        professionList.clear()
                        professionList.addAll(list)
                        arrayAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        val dialog = Dialog(this).apply {
            setContentView(updateDialogBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
        initProfessionSpinner()
        updateDialogBinding.voterNameText.text = voter?.voterName
        updateDialogBinding.etMobno.setText(if (!voter?.mobileNo.isNullOrEmpty()) voter?.mobileNo else "")
        if (voter?.professionNo != 0L) {
            if (professionList.isNotEmpty()) {
                for (index in 0 until professionList.size) {
                    if (professionList[index].professionNo == voter?.professionNo) {
                        updateDialogBinding.spProfession.setSelection(index)
                        break
                    }
                }
            }
        }


        fun onUpdateClick() {
            val mobile = updateDialogBinding.etMobno.text.toString().trim()
            val profession = professionList[updateDialogBinding.spProfession.selectedItemPosition]
            if (mobile.isNotEmpty() && mobile.length < 10) {
                CommonUtils.showToast(this, getString(R.string.pls_enter_valid_mobile_number))
            } else {

                if (mobile.isNotEmpty()) {
                    voter?.mobileNo = mobile
                }
                if (profession.professionNo != 0L) {
                    voter?.professionNo = profession.professionNo
                }
                voter?.updated = 1
                val index = voterList.indexOfFirst { it._id == voter?._id }
                if (index != -1) {
                    voterList[index] = voter!!
                }
                voterListAdapter?.notifyDataSetChanged()
                prefilledMobile()
                CoroutineScope(Dispatchers.Main).launch {
                    commonViewModel.getDB().voterDao().insert(voter!!)
                    setResult(Activity.RESULT_OK)
                    dialog.dismiss()
                }
            }
        }

        updateDialogBinding.onClickListener = object : OnClickListener {
            override fun onClick(v: View?) {
                when (v!!.id) {
                    R.id.btn_update -> {
                        onUpdateClick()
                    }

                    R.id.btn_cancel -> {
                        dialog.dismiss()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun initCastSpinner() {

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, castList)
        binding.spCast.adapter = arrayAdapter
        CoroutineScope(Dispatchers.Main).launch {
            val list = commonViewModel.getDB().CastDao().getAll()
            if (!list.isNullOrEmpty()) {
                castList.clear()
                castList.addAll(list)
                arrayAdapter.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_meu) {
            Intent(this, SearchActivity::class.java).apply {
                putExtra(IntentConstants.SURNAME, voter?.voterLName)
                putExtra(IntentConstants.VILLAGE_NO, voter?.villageNo)
                putExtra(IntentConstants.BOOTH_NO, voter?.boothNo)
                putExtra(IntentConstants.HOUSE_NO, voter?.houseNo)
            }.run {
                updateLauncher.launch(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val updateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val voter1: Voter? = it.data?.getParcelableExtra(IntentConstants.VOTER)
                if (voter1 != null) {
                    val v = voterList.find { voter -> voter._id == voter1._id }
                    if (v == null) {
                        voterList.add(voter1)
                        voterListAdapter?.notifyDataSetChanged()
                        updateFamilyCompleteBtn()
                    }
                }
            }
        }
}