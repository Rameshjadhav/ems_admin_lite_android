package com.ems.lite.admin.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityUnlockKeyBinding
import com.ems.lite.admin.di.viewmodel.OnBoardingViewModel
import com.ems.lite.admin.model.request.LoginRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UnlockKeyActivity : BaseActivity() {
    private lateinit var binding: ActivityUnlockKeyBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    var usercode: Long = 0
    var unlockcode: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_unlock_key)
        initObservers()
        generateCode()

    }

    private fun initObservers() {
        lifecycleScope.launch {
            onBoardingViewModel.loginState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null
                            && (it.code == ResponseStatus.STATUS_CODE_SUCCESS
                                    || it.code == ResponseStatus.STATUS_CODE_CREATED)
                        ) {
                            val user = it.data.user
                            if (user != null) {
                                if (user.isActive == 1) {
                                    Prefs.user = user
                                    HomeActivity.startActivity(this@UnlockKeyActivity)
                                } else {
                                    CommonUtils.showErrorMessage(
                                        this@UnlockKeyActivity,
                                        getString(R.string.account_suspended)
                                    )
                                }
                            }
                        } else if (it.code == 203) {
                            CommonUtils.showErrorMessage(
                                this@UnlockKeyActivity,
                                it.data?.getMessage()
                            )
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showErrorMessage(this@UnlockKeyActivity, it.message)
                    }
                }
            }
        }
    }

    private fun generateCode() {
        binding.etMob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty())
                    createcode()

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }

    private fun createcode() {
        val mob = binding.etMob.getText().toString()
        usercode = mob.toLong() + mob.toLong() + 777
        binding.etYourCode.setText(usercode.toString())
        val tempvalue: Long = 1991
        unlockcode = usercode + tempvalue
    }

    fun btnstart(view: View) {
        if (binding.etMob.text.toString().trim().isEmpty()) {
            CommonUtils.showErrorMessage(this, getString(R.string.pls_enter_mobile_no))
        } else if (binding.etMob.text.toString().trim().length != 10) {
            CommonUtils.showErrorMessage(this, getString(R.string.pls_enter_valid_mobile_number))
        } /*else if (binding.etUnlockcode.text.toString().trim().isEmpty()) {
            CommonUtils.showErrorMessage(this, getString(R.string.pls_enter_unlock_code))
        } */else {
            onBoardingViewModel.login(
                LoginRequest(
                    binding.etMob.text.toString().trim(),
                    binding.etYourCode.text.toString().trim()
                )
            )
        }

    }


    /*  val uncode = binding.etUnlockcode.getText().toString()
      val mob = binding.etMob.getText().toString()
      val yourcode = binding.etYourcode.getText().toString()*/


}