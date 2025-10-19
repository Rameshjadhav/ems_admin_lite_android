package com.ems.lite.admin.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityExportToExcelFilterBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Cast
import com.ems.lite.admin.model.table.Designation
import com.ems.lite.admin.model.table.Profession
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.IntentConstants
import com.ems.lite.admin.utils.IntentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExportToExcelFilterActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ExportToExcelFilterActivity::class.java).run {
                activity.startActivity(this)
            }
        }
    }

    private lateinit var binding: ActivityExportToExcelFilterBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val professionList: ArrayList<Profession> = arrayListOf()
    private val castList: ArrayList<Cast> = arrayListOf()
    private val designationList: ArrayList<Designation> = arrayListOf()
    private var selectedVillage: Village? = null
    private var selectedBooth: Booth? = null
    private var selectedStatus: String? = "0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportToExcelFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.wild_search))
        initClick()
        initProfessionSpinnerAdapter()
        initCasteSpinnerAdapter()
        initDesignationSpinnerAdapter()
        init()
    }

    private fun init() {
        selectedVillage = Village().apply {
            id = 0
            villageNo = 0
            villageName = getString(R.string.all)
            villageNameEng = getString(R.string.all)
        }
        updateWard()
        updateStatusView()
    }

    private fun updateWard() {
        if (selectedVillage != null) {
            binding.etWard.setText(selectedVillage!!.villageName)
            selectedBooth = Booth().apply {
                id = 0
                boothNo = 0
                boothName = getString(R.string.all)
                boothNameEng = getString(R.string.all)
            }
            updateBooth()
        }
    }

    private fun updateBooth() {
        if (selectedBooth != null) {
            binding.etBooth.setText(selectedBooth!!.getName())
        }
    }

    private fun initClick() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_ward -> {
                SelectVillageListActivity.startActivityForResult(
                    this,
                    selectedVillage,
                    villageLauncher
                )
            }

            R.id.et_booth -> {
                if (selectedVillage != null) {
                    SelectBoothListActivity.startActivityForResult(
                        this, selectedVillage!!.villageNo, selectedBooth, boothLauncher
                    )
                } else {
                    CommonUtils.showToast(this, getString(R.string.pls_select_ward_first))
                }
            }

            R.id.et_surname -> {
                CountDetailsActivity.startActivityForResult(
                    this@ExportToExcelFilterActivity, "SURNAME",
                    selectedVillage?.villageNo, selectedBooth?.boothNo, selectedBooth?.boothName,
                    true, surnameLauncher
                )
            }

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

            R.id.btn_search -> {
                if (binding.etFrom.text.toString().trim().isNotEmpty()
                    && binding.etTo.text.toString().trim().isEmpty()
                ) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_to_age))
                } else if (binding.etFrom.text.toString().trim()
                        .isEmpty() && binding.etTo.text.toString().trim().isNotEmpty()
                ) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_from_age))
                } else if (binding.etFrom.text.toString().trim()
                        .isNotEmpty() && binding.etTo.text.toString().trim()
                        .isNotEmpty() && binding.etFrom.text.toString().trim()
                        .toInt() > binding.etTo.text.toString().trim().toInt()
                ) {
                    CommonUtils.showToast(
                        this,
                        getString(R.string.from_booth_must_be_less_than_to_booth)
                    )
                } else {
                }
            }

            R.id.btn_reset -> {
                selectedVillage = Village().apply {
                    id = 0
                    villageNo = 0
                    villageName = getString(R.string.all)
                    villageNameEng = getString(R.string.all)
                }
                updateWard()
                binding.etSurname.setText("")
                selectedStatus = "0"
                updateStatusView()
                binding.spCast.setSelection(0)
                binding.spProfession.setSelection(0)
                binding.spDesignation.setSelection(0)
                binding.etFrom.setText("")
                binding.etTo.setText("")
                binding.rbGenderBoth.isChecked = true
                binding.rbCommiteeBoth.isChecked = true
                binding.rbMobileNone.isChecked = true
                binding.rbOutstation.isChecked = false
                binding.rbAlphabetical.isChecked = true
            }
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

    private fun initProfessionSpinnerAdapter() {

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, professionList)
        binding.spProfession.adapter = arrayAdapter
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

    private val villageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedVillage = IntentUtils.getVillageFromIntent(result.data)
                updateWard()
            }
        }

    private val boothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedBooth = IntentUtils.getBoothFromIntent(result.data)
                updateBooth()
            }
        }
    private val surnameLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val surname = it.data?.getStringExtra(IntentConstants.SURNAME)
                if (!surname.isNullOrEmpty())
                    binding.etSurname.setText(surname)
            }
        }
}