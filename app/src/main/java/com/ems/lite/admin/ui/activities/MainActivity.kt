package com.ems.lite.admin.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.ems.lite.admin.BuildConfig
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityMainBinding
import com.ems.lite.admin.model.request.SaveVoterListRequest
import com.ems.lite.admin.model.request.UpdatedVoterListRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            activity.finish()
        }
    }

    private var isUpdate: String? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
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
        initClickListener()
        syncTables()
    }

    private fun initClickListener() {
        binding.onClickLister = this
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbEnglish) {
                Prefs.lang = Enums.Language.en.toString()
            } else {
                Prefs.lang = BuildConfig.OTHER_LANGUAGE
            }
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_search_voter -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }

            R.id.tv_report -> {
                startActivity(Intent(this, ReportActivity::class.java))
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
                            this@MainActivity,
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
                val intent = Intent(this, UserListActivity::class.java)
                startActivity(intent)
            }

            R.id.tv_export_to_excel -> {
                ExportToExcelFilterActivity.startActivity(this)
            }

            R.id.tv_setting -> {
                SettingActivity.startActivity(this)
            }
        }
    }

    var offset: Long = 0
    private fun getUpdatedVoterList() {
        if (offset != -1L) {
            if (CommonUtils.isNetworkAvailable(this)) {
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
                            CommonUtils.showToast(this, response.error!!.message)
                        }
                    }
            } else {
                CommonUtils.showToast(this, getString(R.string.no_internet_connection))
            }
        } else {
            showHideProgress(false)
            AlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.sync_data))
                .setMessage(getString(R.string.imported_data_successfully))
                .setPositiveButton(getString(R.string.done), null).show()
        }
    }

    private fun getBoothList() {
        if (CommonUtils.isNetworkAvailable(this)) {
//            CustomProgressDialog.showProgressDialog(this)
            boothViewModel.getBoothMasterList().observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.boothList.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            boothViewModel.insertBooth(response.boothList!!)
                            Prefs.isBoothSync = true
                            finish()
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

    private fun saveVoterList(list: List<Voter>) {
        if (CommonUtils.isNetworkAvailable(this)) {
            //  CustomProgressDialog.showProgressDialog(this)
            val action = "saveVoterList"
            val req = SaveVoterListRequest()
            val voterList: ArrayList<Voter> = arrayListOf()
            voterList.addAll(list)
            req.voterList = voterList
            voterViewModel.saveVoterlist(action, req).observe(this) { response ->
                //  CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(getString(R.string.sync_data))
                        .setMessage(getString(R.string.sync_data_msg))
                        .setPositiveButton(getString(R.string.done), null).show()

                    voterList.forEach { it.updated = 0 }
                    voterViewModel.insertUpdatedVoter(voterList)
//                    finish()
                } else if (response?.error != null) {
                    CommonUtils.showToast(this, response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }
}