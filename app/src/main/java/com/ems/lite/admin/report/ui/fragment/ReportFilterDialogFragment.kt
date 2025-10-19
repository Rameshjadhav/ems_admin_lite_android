package com.ems.lite.admin.report.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.ems.lite.admin.R
import com.ems.lite.admin.common.ui.fragment.BaseBottomSheetDialogFragment
import com.ems.lite.admin.databinding.ReportFilterBottomSheetBinding
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.report.model.ReportFilter
import com.ems.lite.admin.ui.activities.SelectBoothListActivity
import com.ems.lite.admin.ui.activities.SelectVillageListActivity
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.IntentUtils

class ReportFilterDialogFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {
    companion object {
        val TAG: String = ReportFilterDialogFragment::class.java.simpleName
        fun onNewInstance(
            reportFilter: ReportFilter, cancellable: Boolean,
            filterListener: FilterListener
        ): ReportFilterDialogFragment {
            return ReportFilterDialogFragment().apply {
                this.reportFilter = reportFilter
                isCancelable = cancellable
                this.filterListener = filterListener
            }
        }
    }

    private lateinit var binding: ReportFilterBottomSheetBinding
    private var filterListener: FilterListener? = null
    private lateinit var reportFilter: ReportFilter
    private var selectedVillage: Village? = null
    private var selectedBooth: Booth? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.report_filter_bottom_sheet, container, false
        )
        initClickListener()
        init()
        return binding.root
    }

    private fun init() {
        selectedVillage = reportFilter.village
        updateVillage()
        selectedBooth = reportFilter.booth
        updateBooth()
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_village -> {
                SelectVillageListActivity.startActivityForResult(
                    requireActivity(), selectedVillage, villageLauncher
                )
            }

            R.id.et_booth -> {
                if (selectedVillage != null) {
                    SelectBoothListActivity.startActivityForResult(
                        requireActivity(), selectedVillage!!.villageNo, selectedBooth, boothLauncher
                    )
                } else {
                    CommonUtils.showToast(
                        requireActivity(), getString(R.string.pls_select_ward_first)
                    )
                }
            }

            R.id.btn_reset -> {
                filterListener?.onResetFilter()
                dismiss()
            }

            R.id.btn_apply -> {
                reportFilter.village = selectedVillage
                reportFilter.booth = selectedBooth
                filterListener?.onFilterApply(reportFilter)
                dismiss()
            }
        }
    }

    private val villageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedVillage = IntentUtils.getVillageFromIntent(result.data)
                updateVillage()
            }
        }

    private val boothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedBooth = IntentUtils.getBoothFromIntent(result.data)
                updateBooth()
            }
        }

    private fun updateVillage() {
        if (selectedVillage != null) {
            binding.etVillage.setText(selectedVillage!!.toString())
//            if (selectedBooth == null || selectedBooth?.villageNo != selectedVillage?.villageNo) {
//                selectedBooth = Booth().apply {
//                    id = 0
//                    boothNo = 0
//                    boothName = getString(R.string.all)
//                    boothNameEng = getString(R.string.all)
//                }
//            }
//            updateBooth()
        }
    }

    private fun updateBooth() {
        if (selectedBooth != null) {
            binding.etBooth.setText(selectedBooth!!.toString())
        }
    }

    interface FilterListener {
        fun onFilterApply(reportFilter: ReportFilter)
        fun onResetFilter()
    }
}