package com.ems.lite.admin.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.DialogEnterPasswordBinding
import com.ems.lite.admin.databinding.LauncherActivityBinding
import com.ems.lite.admin.di.viewmodel.OnBoardingViewModel
import com.ems.lite.admin.model.User
import com.ems.lite.admin.model.request.SaveVoterListRequest
import com.ems.lite.admin.model.request.UserRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LauncherActivity : BaseActivity() {


    private var list: List<Voter>? = null
    private lateinit var binding: LauncherActivityBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.launcher_activity)
        initObserver()
        init()
    }

    private fun init() {
        val user = Prefs.user
        if (user != null) {
            if (CommonUtils.isNetworkAvailable(this)) {
                onBoardingViewModel.getUser(UserRequest(user.userId))
            } else {
                takeAction(user, false)
            }
        }
        if (Prefs.password.isNullOrEmpty()) {
            showEnterPasswordDialog()
        } else {
            val setting = Prefs.setting
            if (setting == null) {
                getAppSettings()
            } else {
                binding.image = setting.mainBanner
                downloadShareImage()
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.userState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            if (it.data?.user != null) {
                                Prefs.user = it.data.user
                                takeAction(it.data.user, false)
                            }
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                    }
                }
            }
        }
    }

    private fun takeAction(user: User?, isButtonClicked: Boolean) {
        if (user != null && user.isActive == 0) {
            AlertDialog.Builder(this@LauncherActivity)
                .setTitle(getString(R.string.account_suspended))
                .setMessage(getString(R.string.account_suspended_msg))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { _, _ -> finish() }
                .show()
        } else if (isButtonClicked) {
            getSyncVoterList()
        }
    }

    private fun getAppSettings() {
        if (CommonUtils.isNetworkAvailable(this)) {
            CustomProgressDialog.showProgressDialog(this)
            voterViewModel.getAppSetting().observe(this) { response ->
                CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    Prefs.setting = response.info
                    binding.image = response.info?.mainBanner
                    Prefs.shareImageUrl = response.info?.shareImage
                    Prefs.printImageUrl = response.info?.printImage
                    Prefs.votingDate = response.info?.votingDate
                    Prefs.votingTime = response.info?.votingTime
                    Prefs.footerMessage = response.info?.massage
                    downloadShareImage()
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

    private fun showEnterPasswordDialog() {
        Dialog(this).apply {
            setCancelable(false)
            val editDocumentBinding: DialogEnterPasswordBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.dialog_enter_password, null, false
            )
            setContentView(editDocumentBinding.root)
            val onClickListener = View.OnClickListener { v ->
                when (v!!.id) {
                    R.id.save_btn -> {
                        if (editDocumentBinding.edtPassword.text.toString().trim()
                                .isEmpty()
                        ) {
                            CommonUtils.showToast(
                                this@LauncherActivity,
                                getString(R.string.pls_enter_password)
                            )
                        } else if (editDocumentBinding.edtPassword.text.toString().trim()
                                .length < 3
                        ) {
                            CommonUtils.showToast(
                                this@LauncherActivity,
                                getString(R.string.pls_enter_correct_password)
                            )
                        } else {
                            val number =
                                (editDocumentBinding.edtPassword.text.toString().trim()
                                    .toInt() - 777)
                            if (number > 0) {
                                Prefs.password = number.toString()
                                init()
                                dismiss()
                            } else {
                                CommonUtils.showToast(
                                    this@LauncherActivity,
                                    getString(R.string.incorrect_password)
                                )
                            }
                        }
                    }
                }
            }
            editDocumentBinding.onClickListener = onClickListener
        }.run {
            show()
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }

    private fun displayError(message: String?) {
        CommonUtils.showToast(this, getString(R.string.no_internet_connection))
    }


    private fun displayProgressBar(isDisplayed: Boolean) {
//        progressbar.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }

    fun btnLogin(view: View) {
        val user = Prefs.user
        if (user == null) {
            startActivityForResult(Intent(this, UnlockKeyActivity::class.java), 1)
        } else {
            takeAction(user, true)
        }
    }

    private var isRecordSending = false
    private fun getSyncVoterList() {
        CoroutineScope(Dispatchers.Main).launch {
            val list = voterViewModel.getDB().voterDao().getUpdatedVoter("1")
            if (!list.isNullOrEmpty()) {
                saveVoterList(list)
            } else if (isRecordSending) {
                showHideProgress(false)
                isRecordSending = false
                HomeActivity.startActivity(this@LauncherActivity)
            } else {
                HomeActivity.startActivity(this@LauncherActivity)
            }
        }
    }

    private fun saveVoterList(list: List<Voter>) {
        if (CommonUtils.isNetworkAvailable(this)) {
//              CustomProgressDialog.showProgressDialog(this)
            if (!isRecordSending)
                showHideProgress(true)
            isRecordSending = true
            val action = "saveVoterListv2"
            val req = SaveVoterListRequest()
            val voterList: ArrayList<Voter> = arrayListOf()
            voterList.addAll(list)
            req.voterList = voterList
            voterViewModel.saveVoterlist(action, req).observe(this) { response ->
                //  CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
//                    AlertDialog.Builder(this@MainActivity)
//                        .setTitle(getString(R.string.sync_data))
//                        .setMessage(getString(R.string.sync_data_msg))
//                        .setPositiveButton(getString(R.string.done), null).show()
                    voterList.forEach {
                        it.updated = 0
                    }
                    voterViewModel.insertUpdatedVoter(voterList)
                    Handler(Looper.getMainLooper()).postDelayed({
                        getSyncVoterList()
                    }, 300)
//                    finish()
                } else if (response?.error != null) {
                    showHideProgress(false)
                    HomeActivity.startActivity(this@LauncherActivity)
                }
            }
        } else {
            HomeActivity.startActivity(this@LauncherActivity)
        }
    }
}