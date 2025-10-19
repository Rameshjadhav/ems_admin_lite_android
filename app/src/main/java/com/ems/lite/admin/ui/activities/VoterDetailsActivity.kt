package com.ems.lite.admin.ui.activities


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityVoterDetailsBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.request.SaveVoterRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Cast
import com.ems.lite.admin.model.table.Designation
import com.ems.lite.admin.model.table.Profession
import com.ems.lite.admin.model.table.Religion
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.DateFormatter
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.IntentConstants
import com.ems.lite.admin.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar


@AndroidEntryPoint
open class VoterDetailsActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val REQUEST_CODE_WRITE_SETTINGS_PERMISSION = 21

        // My Generic Check Permission Method
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

    private lateinit var binding: ActivityVoterDetailsBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val professionList: ArrayList<Profession> = arrayListOf()
    private val castList: ArrayList<Cast> = arrayListOf()
    private val designationList: ArrayList<Designation> = arrayListOf()
    private val positionList: ArrayList<String> = arrayListOf()
    private val religionList: ArrayList<Religion> = arrayListOf()
    private var voter: Voter? = null
    private var booth: Booth? = null
    private val date = Prefs.votingDate
    private val time = Prefs.votingTime
    private var initialized = false
    private var selectedStatus: String? = null
    private val selectedDateTime = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voter_details)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.voter_detail))
        initClickListener()
        initPositionSpinnerAdapter()
        initReligionSpinnerAdapter()
        initProfessionSpinnerAdapter()
        initCasteSpinnerAdapter()
        initDesignationSpinnerAdapter()
        init()
    }

    private fun init() {
        val id = intent.getIntExtra(IntentConstants.ID, 0)
//        val st=intent.getStringExtra(IntentConstants.ID)
        CoroutineScope(Dispatchers.Main).launch {
            voter = commonViewModel.getDB().voterDao().get(id)
            if (voter != null) {
                binding.tvName.setText(
                    if (!voter?.getFullName().isNullOrEmpty()) voter?.getFullName()!! else ""
                )
                val village = commonViewModel.getDB().villageDao().get(voter?.villageNo)
                binding.tvVillage.setText(
                    if (village != null) village.toString() else ""
                )
                binding.tvVoterNo.setText(if (voter?.voterNo != null) voter?.voterNo!!.toString() else "")

                binding.etHomeno.setText(if (!voter?.houseNo.isNullOrEmpty()) voter?.houseNo!! else "")
                binding.tvAge.setText(if (voter?.age != null) voter?.age.toString() else "")
                binding.tvEpicNo.setText(if (!voter?.cardNo.isNullOrEmpty()) voter?.cardNo!! else "")
                binding.etAddr.setText(if (!voter?.address.isNullOrEmpty()) voter?.address!! else "")
                booth = commonViewModel.getDB().BoothDao().get(voter?.boothNo)

                binding.tvBooth.setText(
                    if (!booth?.getName().isNullOrEmpty()) booth?.getName()!! else ""
                )
                binding.etMobno.setText(if (!voter?.mobileNo.isNullOrEmpty()) voter?.mobileNo!! else "")
                binding.vipRatingBar.rating = voter!!.vip.toFloat()
                if (!voter?.outstationAddress.isNullOrEmpty()) {
                    binding.etOutstationAddress.setText(voter?.outstationAddress)
                    if (positionList.isNotEmpty()) {
                        for (index in 0 until positionList.size) {
                            if (positionList[index] == voter?.outstationAddress!!) {
                                binding.spPosition.setSelection(index)
                                break
                            }
                        }
                    }
                }
                if (!voter?.bDate.isNullOrEmpty()) {
//                    val d = DateFormatter.getDate(DateFormatter.yyyy_MM_dd_DASH, voter?.bDate!!)
//                    selectedDateTime.time = d
//                    binding.etDob.setText(
//                        DateFormatter.getFormattedDate(
//                            selectedDateTime.timeInMillis,
//                            DateFormatter.dd_MM_yyyy_slash
//                        )
//                    )
                    binding.etDob.setText(voter?.bDate)
                }
                if (voter?.religionNo != null) {
                    if (religionList.isNotEmpty()) {
                        for (index in 0 until religionList.size) {
                            if (religionList[index].religionNo == voter?.religionNo) {
                                binding.spReligion.setSelection(index)
                                break
                            }
                        }
                    }
                }
                if (voter?.castNo != 0L) {
                    if (castList.isNotEmpty()) {
                        for (index in 0 until castList.size) {
                            if (castList[index].castNo == voter?.castNo!!) {
                                binding.spCast.setSelection(index)
                                break
                            }
                        }
                    }
                }
                if (voter?.professionNo != 0L) {
                    if (professionList.isNotEmpty()) {
                        for (index in 0 until professionList.size) {
                            if (professionList[index].professionNo == voter?.professionNo) {
                                binding.spprofession.setSelection(index)
                                break
                            }
                        }
                    }
                }
                if (!voter?.voterStatusName.isNullOrEmpty()) {
                    selectedStatus = voter!!.voterStatusName
                }
                updateStatusView()

                if (voter?.designationNo != 0L) {
                    if (designationList.isNotEmpty()) {
                        for (index in 0 until designationList.size) {
                            if (designationList[index].designationNo == voter?.designationNo) {
                                binding.spDesignation.setSelection(index)
                                break
                            }
                        }
                    }
                }
                // binding.spprofession.setText(if (!voter?.professionno.isNullOrEmpty()) voter?.professionno!! else "")
                // val prono =
                ///data/data/com.volunteer/databases/ElectionDB
                //    if (!voter?.professionno.isNullOrEmpty()) voter?.professionno!!.toInt() else 0
                // val professionname = commonViewModel.getDB().ProfessionDao().get(prono)
                // binding.spprofession.setText(if (!professionname?.profession.isNullOrEmpty()) professionname.profession!! else "")
                if (!voter?.committeeDesignation.isNullOrEmpty()) {
                    when (voter?.committeeDesignation) {
                        Enums.Committee.ADMIN.toString() -> {
                            binding.rbAdmin.isChecked = true
                        }

                        Enums.Committee.MEMBER.toString() -> {
                            binding.rbMember.isChecked = true
                        }

                        else -> {
                            binding.rbNone.isChecked = true
                        }
                    }
                } else {
                    binding.rbNone.isChecked = true
                }

            }
            binding.deadSwitch.isChecked = (voter!!.dead == 1)
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

            R.id.btn_add_number -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_CONTACTS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val PHONE_CALL_REQUEST = 0
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_CONTACTS),
                        PHONE_CALL_REQUEST
                    )
                } else {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    startActivityForResult(intent, 111)
                    //startActivity(intent)
                }
            }

            R.id.et_dob -> {
                showDatePicker()
            }

            R.id.btn_save -> {
                save()
            }

            R.id.btn_whatsapp -> {
                shareWhatsApp()
            }

            R.id.btn_call -> {
                val number = binding.etMobno.text.toString()
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val PHONE_CALL_REQUEST = 0
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CALL_PHONE),
                        PHONE_CALL_REQUEST
                    )
                } else {
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(number)))
                    startActivity(intent)
                }
            }

            R.id.btn_family -> {
                FamilyActivity.startActivityForResult(this, voter?._id, familyLauncher)
            }

            R.id.btn_send_sms -> {
                if (binding.etMobno.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(this, getString(R.string.enter_your_mobile_no))
                } else if (binding.etMobno.text.toString().trim().length < 10) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_valid_mobile_number))
                } else if (!hasPermissions(
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
//                val jsonArray = JSONArray(jsonData)

                // Define maximum width based on your printer's width
                val maxWidth = 40 // Adjust this based on your printer's width for the main content
                val titleBitmap = TitleBitmap(getString(R.string.voter_detail))
                val nameBitmaps = wrapText(
                    getString(R.string.voter_name) + " :-  " + voter?.voterName,
                    maxWidth
                ).map { textToBitmap(it) }

                val voterNoBitmaps = wrapText(
                    getString(R.string.voter_no) + " :-  " + voter?.voterNo,
                    maxWidth
                ).map { textToBitmap(it) }

                val epicNoBitmaps = wrapText(
                    getString(R.string.epic_no) + " :-  " + voter?.cardNo,
                    maxWidth
                ).map { textToBitmap(it) }

                val pollingStationBitmaps = wrapText(
                    getString(R.string.polling_station) + " :- " + booth?.getName(),
                    maxWidth
                ).map { textToBitmap(it) }

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
                            }</img>\n"
                        )
                    }
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
//                        if (isAddFooter && !Prefs.footerMessage.isNullOrEmpty()) {
//                            val footerMessageBitmaps = wrapText(
//                                Prefs.footerMessage!!,
//                                maxWidth
//                            ).map { textToBitmap(it) }
//                            footerMessageBitmaps.forEach { bitmap ->
//                                append(
//                                    "[L]<img>${
//                                        PrinterTextParserImg.bitmapToHexadecimalString(
//                                            printer, bitmap
//                                        )
//                                    }</img>\n\n"
//                                )
//                            }
//                        }

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

                Log.d("PRINT_DEBUG", "Print Text: $formattedText")
                printer.printFormattedText(formattedText)
            } catch (e: Exception) {
                Log.e("PRINT_ERROR", "Error printing: ${e.message}")
                Toast.makeText(this, "Error printing: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No printer selected.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun save() {
        val cast: Cast? =
            if (castList.isNotEmpty()) castList[binding.spCast.selectedItemPosition] else null
        val profession =
            if (professionList.isNotEmpty()) professionList[binding.spprofession.selectedItemPosition] else null
        val designation =
            if (designationList.isNotEmpty()) designationList[binding.spDesignation.selectedItemPosition] else null
//        voter!!.votername = binding.etVotername.text.toString()
        voter!!.houseNo = binding.etHomeno.text.toString().trim()
        voter!!.address = binding.etAddr.text.toString().trim()
        voter!!.committeeDesignation = when {
            binding.rbAdmin.isChecked -> {
                Enums.Committee.ADMIN.toString()
            }

            binding.rbMember.isChecked -> {
                Enums.Committee.MEMBER.toString()
            }

            else -> {
                null
            }
        }
        if (binding.etMobno.text.toString().isNotEmpty())
            voter!!.mobileNo = binding.etMobno.text.toString()

        if (voter != null) {
            val voterEntity = getData()
            voterEntity.outstationAddress = binding.etOutstationAddress.text.toString().trim()
//            if (binding.spPosition.selectedItemPosition > 0) {
//                voterEntity.outstationAddress =
//                    positionList[binding.spPosition.selectedItemPosition]
//            }
            if (binding.spReligion.selectedItemPosition > 0) {
                voterEntity.religionNo =
                    religionList[binding.spReligion.selectedItemPosition].religionNo
            }

            if (cast?.castNo != 1L) {
                voterEntity.castNo = cast!!.castNo
            }

            if (!selectedStatus.isNullOrEmpty() && selectedStatus != Enums.Status.SELECT.toString()) {
                voterEntity.voterStatusName = selectedStatus
            }

            voterEntity.vip = binding.vipRatingBar.rating.toInt()

            if (binding.etDob.text.toString().trim().isNotEmpty()) {
                voterEntity.bDate = binding.etDob.text.toString().trim()
//                    DateFormatter.getFormattedDate(
//                    selectedDateTime.timeInMillis, DateFormatter.yyyy_MM_dd_DASH
//                )
            }


            if (profession?.professionNo != 1L) {
                voterEntity.professionNo = profession!!.professionNo
            }
            if (designation?.designationNo != 1L) {
                voterEntity.designationNo = designation!!.designationNo
            }
            voterEntity.dead = if (binding.deadSwitch.isChecked) 1 else 0
            val request = SaveVoterRequest(
                voterEntity._id,
                voterEntity.mobileNo,
                voterEntity.perAddress,
                voterEntity.bDate,
                voterEntity.address,
                voterEntity.houseNo,
                voterEntity.outstationAddress,
                voterEntity.castNo,
                voterEntity.voterStatusName,
                voterEntity.professionNo,
                voterEntity.designationNo,
                voterEntity.committeeDesignation,
                voterEntity.religionNo,
                voterEntity.vip,
                voterEntity.userId,
                voterEntity.remark1,
                voterEntity.remark2,
                voterEntity.updated,
                voterEntity.refVoterNo,
                voterEntity.dead
            )
            saveVoter(voterEntity, request)
//            CoroutineScope(Dispatchers.Main).launch {
//                commonViewModel.getDB().voterDao().insert(voterEntity)
//                setResult(Activity.RESULT_OK)
//                finish()
//            }
        }
    }

    private fun saveVoter(voterEntity: Voter, request: SaveVoterRequest) {
        if (CommonUtils.isNetworkAvailable(this)) {
            showHideProgress(true)
            voterViewModel.saveVoter(request).observe(this) { response ->
                //  CustomProgressDialog.dismissProgressDialog()
                showHideProgress(false)
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    CoroutineScope(Dispatchers.Main).launch {
                        voterEntity.updated = 0
                        commonViewModel.getDB().voterDao().insert(voterEntity)
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                } else if (response?.error != null) {
                    showHideProgress(false)
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                voterEntity.updated = 1
                commonViewModel.getDB().voterDao().insert(voterEntity)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun getData(): Voter {
        val voterEntity = Voter()
        voterEntity._id = voter!!._id
        voterEntity.srNo = voter!!.srNo
        voterEntity.acNo = voter!!.acNo
        voterEntity.divNo = voter!!.divNo
        voterEntity.villageNo = voter!!.villageNo
        voterEntity.boothNo = voter!!.boothNo
        voterEntity.sectionNo = voter!!.sectionNo
        voterEntity.sectionName = voter!!.sectionName
        voterEntity.voterNo = voter!!.voterNo
        voterEntity.voterName = voter!!.voterName
        voterEntity.voterNameEng = voter!!.voterNameEng
        voterEntity.voterFName = voter!!.voterFName
        voterEntity.voterMName = voter!!.voterMName
        voterEntity.voterLName = voter!!.voterLName
        voterEntity.voterFNameEng = voter!!.voterFNameEng
        voterEntity.voterMNameEng = voter!!.voterMNameEng
        voterEntity.voterLNameEng = voter!!.voterLNameEng
        voterEntity.houseNo = voter!!.houseNo
        voterEntity.address = voter!!.address
        voterEntity.outstationAddress = voter!!.outstationAddress
        voterEntity.perAddress = voter!!.perAddress
        voterEntity.bDate = voter!!.bDate
        voterEntity.mobileNo = voter!!.mobileNo
        voterEntity.whatsAppNo = voter!!.whatsAppNo
        voterEntity.sex = voter!!.sex
        voterEntity.age = voter!!.age
        voterEntity.cardNo = voter!!.cardNo
        voterEntity.castNo = voter!!.castNo
        voterEntity.dead = voter!!.dead
        voterEntity.professionNo = voter!!.professionNo
        voterEntity.designationNo = voter!!.designationNo
        voterEntity.voted = voter!!.voted
        voterEntity.updated = 1
        voterEntity.message = voter!!.message
        voterEntity.image = voter!!.image
        voterEntity.refVoterNo = voter!!.refVoterNo
        voterEntity.committeeDesignation = voter!!.committeeDesignation
        voterEntity.vip = voter!!.vip
        voterEntity.voterStatusName = voter!!.voterStatusName
        voterEntity.religionNo = voter!!.religionNo
        voterEntity.dead = voter!!.dead
//        voterEntity.userId = Prefs.user?.userId?.toString()
        voterEntity.remark1 = voter!!.remark1
        voterEntity.remark2 = voter!!.remark2
        return voterEntity
    }

    private fun showDatePicker() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDateTime.set(year, month, day)
                binding.etDob.setText(
                    DateFormatter.getFormattedDate(
                        selectedDateTime.timeInMillis,
                        DateFormatter.dd_MM_yyyy_slash
                    )
                )
            },
            startYear,
            startMonth,
            startDay
        ).show()
    }

    private fun initPositionSpinnerAdapter() {

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, positionList)
        binding.spPosition.adapter = arrayAdapter
        positionList.clear()
        positionList.add(getString(R.string.select_voter_position))
        positionList.add(getString(R.string.local))
        positionList.add(getString(R.string.non_local))
        positionList.add(getString(R.string.sr_citizen_unhealthy))
        positionList.add(getString(R.string.postal))
        arrayAdapter.notifyDataSetChanged()
    }

    private fun initReligionSpinnerAdapter() {
        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, religionList)
        binding.spReligion.adapter = arrayAdapter
        religionList.clear()
        CoroutineScope(Dispatchers.Main).launch {
            val list = commonViewModel.getDB().religionDao().getAll()
            if (!list.isNullOrEmpty()) {
                religionList.clear()
                religionList.addAll(list)
                arrayAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initProfessionSpinnerAdapter() {

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, professionList)
        binding.spprofession.adapter = arrayAdapter
        CoroutineScope(Dispatchers.Main).launch {
            val list = commonViewModel.getDB().ProfessionDao().getAll()
            if (!list.isNullOrEmpty()) {
                professionList.clear()
                professionList.addAll(list)
                arrayAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun initCasteSpinnerAdapter() {

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

    private fun initDesignationSpinnerAdapter() {

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, designationList)
        binding.spDesignation.adapter = arrayAdapter
        CoroutineScope(Dispatchers.Main).launch {
            val list = commonViewModel.getDB().designationDao().getAll()
            if (!list.isNullOrEmpty()) {
                designationList.clear()
                designationList.addAll(list)
                arrayAdapter.notifyDataSetChanged()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            val contacturi: Uri = data?.data ?: return
            val cols: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val rs: Cursor? = contentResolver.query(contacturi, cols, null, null, null)
            if (rs?.moveToFirst()!!) {
                binding.etMobno.setText(rs.getString(0))
            }
        } else if (requestCode == REQUEST_CODE_WRITE_SETTINGS_PERMISSION) {
            if (canWriteSettings) {
                // change the settings here ...
//                printBluetooth()
            } else {
                CommonUtils.showToast(this, getString(R.string.write_permission_not_granted))
            }
        }
    }

    private var shareNumber = ""
    private fun shareWhatsApp() {

        val extra = "91"
        val number = binding.etMobno.text.toString().filter { !it.isWhitespace() }
//        val name = binding.etVotername.text.toString()
//        val init = "*Dear*\n"
        val noSize = number.length
        if (noSize == 10) {
            shareNumber = (extra + number).toString()
        }
        if (noSize == 13) {
            shareNumber = number
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !hasPermissions(this, *PERMISSIONS_31)
        ) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_31, PERMISSION_ALL)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            && !hasPermissions(this, *PERMISSIONS)
        ) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        } else {
            shareItemFromServer()
        }
    }

    private fun shareItemFromServer() {
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

    private fun shareImageWhatsApp(bmp: Bitmap) {
        shareNumber = shareNumber.replace("+", "")
        val name = binding.tvName.text.toString()
        if (checkContacts(shareNumber) == 1) {

            val time = System.currentTimeMillis()
            val share = Intent("android.intent.action.MAIN")
            share.action = Intent.ACTION_SEND
            shareNumber = shareNumber.replace("+", "")
            if (shareNumber.length == 10) {
                shareNumber = "91$shareNumber"
            }
            Log.e("aaa", "$shareNumber-")

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
            share.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + File.separator + "temporary_file_${time}.jpg"
                )
            )

//            val name = binding.etVotername.text.toString()
            val msg = generateMessage(true)
            share.putExtra(Intent.EXTRA_TEXT, msg)
            share.putExtra(
                "jid",
                PhoneNumberUtils.stripSeparators(shareNumber) + "@s.whatsapp.net"
            )

            share.setPackage(pickWhatsappPackageName())
            startActivity(Intent.createChooser(share, "Share Image"))

        } else {
            val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, shareNumber)
            intent.putExtra(ContactsContract.Intents.Insert.NAME, "Z-$name")
            startActivity(intent)
        }
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

    val Context.canWriteSettings: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(this)

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
            msg += getString(R.string.voter_name) + " :-  " + voter?.voterName +
                    "\n" + getString(R.string.voter_no) + " :-  " + voter?.voterNo +
                    "\n" + getString(R.string.epic_no) + " :-  " + voter?.cardNo +
                    "\n" + getString(R.string.polling_station) + " :- " + booth?.getName() +
                    "\n\n---------------------------------------\n\n"
            if (isAddFooter && !Prefs.footerMessage.isNullOrEmpty()) {
                msg += Prefs.footerMessage + "\n"
            }
            msg += getString(R.string.voting_date) + " :-  " + date +
                    "\n" + getString(R.string.voting_time) + " :-  " + time
        }
        return msg
    }

    private fun sendSMSMessage() {
        try {
            val msg = generateMessage(true)
            val smsManager: SmsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(msg)
            smsManager.sendMultipartTextMessage(
                binding.etMobno.text.toString(),
                null, parts, null, null
            )
            Toast.makeText(this, "SMS sent.", Toast.LENGTH_LONG).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val familyLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                init()
            }
        }
}




