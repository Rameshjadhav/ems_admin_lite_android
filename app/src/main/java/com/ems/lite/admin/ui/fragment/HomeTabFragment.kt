package com.ems.lite.admin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.ems.lite.admin.BuildConfig
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.HomeTabFragmentBinding
import com.ems.lite.admin.di.viewmodel.BoothViewModel
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.request.SaveVoterListRequest
import com.ems.lite.admin.model.request.UpdatedVoterListRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.ui.activities.*
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeTabFragment : BaseFragment(), View.OnClickListener {
    companion object {
        fun onNewInstance(): HomeTabFragment {
            return HomeTabFragment()
        }
    }

    private lateinit var binding: HomeTabFragmentBinding
    private val voterViewModel: VoterViewModel by viewModels()
    private val boothViewModel: BoothViewModel by viewModels()
    private var isUpdate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_tab_fragment, container, false)
        init()
        initListener()
        return binding.root
    }

    private fun init() {
        if (Prefs.lang == Enums.Language.en.toString()) {
            binding.rbEnglish.isChecked = true
        } else {
            binding.rbOther.isChecked = true
        }
        when (BuildConfig.OTHER_LANGUAGE) {
            Enums.Language.mr.toString() -> {
                binding.rbOther.text = getString(R.string.marathi)
            }

            Enums.Language.kn.toString() -> {
                binding.rbOther.text = getString(R.string.kannada)
            }

            Enums.Language.te.toString() -> {
                binding.rbOther.text = getString(R.string.telugu)
            }

            Enums.Language.ta.toString() -> {
                binding.rbOther.text = getString(R.string.tamil)
            }
        }

    }

    private fun initListener() {
        binding.onClickLister = this
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbEnglish) {
                Prefs.lang = Enums.Language.en.toString()
            } else {
                Prefs.lang = BuildConfig.OTHER_LANGUAGE
            }
            requireActivity().startActivity(requireActivity().intent)
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_search_voter -> {
                startActivity(Intent(requireActivity(), SearchActivity::class.java))
            }

            R.id.tv_report -> {
                startActivity(Intent(requireActivity(), ReportActivity::class.java))
            }

            R.id.tv_import_data -> {
                offset = 0
                getUpdatedVoterList()
            }

            R.id.tv_export_data -> {
                isUpdate = "1"
                CoroutineScope(Dispatchers.Main).launch {
                    val list = voterViewModel.getDB().voterDao().getUpdatedVoter(isUpdate!!)
                    if (!list.isNullOrEmpty()) {
                        saveVoterList(list)
                    } else {
                        CommonUtils.showToast(
                            requireActivity(),
                            getString(R.string.no_updated_records)
                        )
//                        finish()
                    }
                }
            }

            R.id.tv_update_booth -> {
                getBoothList()
            }

            R.id.tv_activate_users -> {
                val intent = Intent(requireActivity(), UserListActivity::class.java)
                startActivity(intent)
            }

//            R.id.tv_export_to_excel -> {
//                ExportToExcelFilterActivity.startActivity(requireActivity())
//            }

            R.id.tv_setting -> {
                SettingActivity.startActivity(requireActivity())
            }
        }
    }

    var offset: Long = 0
    private fun getUpdatedVoterList() {
        if (offset != -1L) {
            if (CommonUtils.isNetworkAvailable(requireActivity())) {
//            CustomProgressDialog.showProgressDialog(this)
                if (offset == 0L)
                    showHideProgress(true)
                voterViewModel.getUpdatedVoterList(UpdatedVoterListRequest(offset))
                    .observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()
                        if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
//                        AlertDialog.Builder(this@MainActivity)
//                            .setTitle(getString(R.string.sync_data))
//                            .setMessage(getString(R.string.imported_data_successfully))
//                            .setPositiveButton(getString(R.string.done), null).show()
                            if (!response.list.isNullOrEmpty()) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    response.list!!.forEach { it.updated = 0 }
                                    voterViewModel.insertUpdatedVoter(response.list!!)
                                }
                            }
                            offset = response.nextOffset
                            getUpdatedVoterList()
                        } else if (response?.error != null) {
                            CommonUtils.showToast(requireActivity(), response.error!!.message)
                        }
                    }
            } else {
                CommonUtils.showToast(requireActivity(), getString(R.string.no_internet_connection))
            }
        } else {
            showHideProgress(false)
            AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.sync_data))
                .setMessage(getString(R.string.imported_data_successfully))
                .setPositiveButton(getString(R.string.done), null).show()
        }
    }

    private fun saveVoterList(list: List<Voter>) {
        if (CommonUtils.isNetworkAvailable(requireActivity())) {
            //  CustomProgressDialog.showProgressDialog(this)
            val action = "saveVoterList"
            val req = SaveVoterListRequest()
            val voterList: ArrayList<Voter> = arrayListOf()
            voterList.addAll(list)
            req.voterList = voterList
            voterViewModel.saveVoterlist(action, req).observe(this) { response ->
                //  CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle(getString(R.string.sync_data))
                        .setMessage(getString(R.string.sync_data_msg))
                        .setPositiveButton(getString(R.string.done), null).show()

                    voterList.forEach { it.updated = 0 }
                    voterViewModel.insertUpdatedVoter(voterList)
//                    finish()
                } else if (response?.error != null) {
                    CommonUtils.showToast(requireActivity(), response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(requireActivity(), getString(R.string.no_internet_connection))
        }
    }

    private fun getBoothList() {
        if (CommonUtils.isNetworkAvailable(requireActivity())) {
//            CustomProgressDialog.showProgressDialog(this)
            boothViewModel.getBoothMasterList().observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.boothList.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            boothViewModel.insertBooth(response.boothList!!)
                            Prefs.isBoothSync = true
                            requireActivity().finish()
                        }
                    }
                } else if (response?.error != null) {
                    CommonUtils.showToast(requireActivity(), response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(requireActivity(), getString(R.string.no_internet_connection))
        }
    }

}