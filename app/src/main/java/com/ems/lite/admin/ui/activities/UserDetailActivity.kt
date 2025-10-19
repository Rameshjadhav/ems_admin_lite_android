package com.ems.lite.admin.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.databinding.DataBindingUtil
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.UserDetailActivityBinding
import com.ems.lite.admin.model.User
import com.ems.lite.admin.model.request.UpdateUserRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.IntentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivityForResult(
            activity: Activity, user: User?, launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, UserDetailActivity::class.java).apply {
                IntentUtils.putUserToIntent(this, user)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: UserDetailActivityBinding
    private var user: User? = null
    private var selectedVillage: Village? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.user_detail_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.user_detail))
        initClick()
        init()
    }

    private fun init() {
        user = IntentUtils.getUserFromIntent(intent)
        setUserDetails()
    }

    private fun setUserDetails() {
        if (user != null) {
            binding.etName.setText(if (!user?.name.isNullOrEmpty()) user?.name else "")
            selectedVillage = Village().apply {
                id = user!!.villageNo
                villageNo = user!!.villageNo
                villageName = user!!.villageName
                villageNameEng = user!!.villageName
            }
            updateVillage(true)
            updateBooth(true)
            binding.activateToggle.isChecked = (user!!.isActive == 1)
        }
    }

    private fun updateVillage(isFromFirst: Boolean) {
        if (selectedVillage != null) {
            if (selectedVillage!!.id != 0L) {
                binding.etVillage.setText(selectedVillage!!.villageName)
//                binding.crBooth.visibility = View.VISIBLE
            } else {
                binding.etVillage.setText(getString(R.string.all))
//                binding.crBooth.visibility = View.GONE
            }
            updateBooth(isFromFirst)
        } else {
            binding.etVillage.setText("")
        }
    }

    private fun updateBooth(isFromFirst: Boolean) {
        if (isFromFirst) {
            when (user?.type) {
                Enums.Type.SINGLE.toString() -> {
                    binding.rbSingle.isChecked = true
                }

                Enums.Type.FROM_TO.toString() -> {
                    binding.rbFromTo.isChecked = true
                }

                Enums.Type.CUSTOM.toString() -> {
                    binding.rbCustom.isChecked = true
                }

                else -> {
                    binding.rbAll.isChecked = true
                }
            }
        } else {
            binding.rbAll.isChecked = true
        }
        binding.etSingle.setText(if (isFromFirst && user?.boothNo != 0L) user?.boothNo!!.toString() else "")
        binding.etBoothFrom.setText(if (isFromFirst && user?.from != null && user?.from != 0) user?.from!!.toString() else "")
        binding.etBoothTo.setText(if (isFromFirst && user?.to != null && user?.to != 0) user?.to!!.toString() else "")
        binding.etCustom.setText(if (isFromFirst && !user?.booths.isNullOrEmpty()) user?.booths else "")
        updateBoothTotal()
    }

    private fun updateBoothTotal() {
        CoroutineScope(Dispatchers.Main).launch {
            val count =
                voterViewModel.getDB().BoothDao().getBoothTotalByWard(
                    selectedVillage?.villageNo ?: 0
                )
            binding.tvBoothCount.text = count.toString()
        }
    }


    private fun initClick() {
        binding.onClickListener = this
        binding.rgBooth.setOnCheckedChangeListener { _, _ ->
            when (binding.rgBooth.checkedRadioButtonId) {
                R.id.rb_single -> {
                    binding.etSingle.visibility = View.VISIBLE
                    binding.etBoothFrom.visibility = View.GONE
                    binding.etBoothTo.visibility = View.GONE
                    binding.etCustom.visibility = View.GONE
                }

                R.id.rb_from_to -> {
                    binding.etSingle.visibility = View.GONE
                    binding.etBoothFrom.visibility = View.VISIBLE
                    binding.etBoothTo.visibility = View.VISIBLE
                    binding.etCustom.visibility = View.GONE
                }

                R.id.rb_custom -> {
                    binding.etSingle.visibility = View.GONE
                    binding.etBoothFrom.visibility = View.GONE
                    binding.etBoothTo.visibility = View.GONE
                    binding.etCustom.visibility = View.VISIBLE
                }

                else -> {
                    binding.etSingle.visibility = View.GONE
                    binding.etBoothFrom.visibility = View.GONE
                    binding.etBoothTo.visibility = View.GONE
                    binding.etCustom.visibility = View.GONE
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_village -> {
                SelectVillageListActivity.startActivityForResult(
                    this, selectedVillage, villageLauncher
                )
            }

            R.id.ll_btn_save -> {
                var from = 0
                var to = 0
                if (binding.rbFromTo.isChecked) {
                    if (binding.etBoothFrom.text.toString().trim().isNotEmpty()) {
                        from = binding.etBoothFrom.text.toString().trim().toInt()
                    }
                    if (binding.etBoothTo.text.toString().trim().isNotEmpty()) {
                        to = binding.etBoothTo.text.toString().trim().toInt()
                    }
                }
                if (/*selectedVillage?.villageNo != 0L &&*/ binding.rbSingle.isChecked
                    && binding.etSingle.text.toString().trim().isEmpty()
                ) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_booth_no))
                } else if (/*selectedVillage?.villageNo != 0L &&*/ binding.rbFromTo.isChecked
                    && binding.etBoothFrom.text.toString().trim().isEmpty()
                ) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_from_booth))
                } else if (/*selectedVillage?.villageNo != 0L &&*/ binding.rbFromTo.isChecked
                    && binding.etBoothTo.text.toString().trim().isEmpty()
                ) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_to_booth))
                } else if (/*selectedVillage?.villageNo != 0L &&*/ binding.rbFromTo.isChecked && from > to
                ) {
                    CommonUtils.showToast(
                        this, getString(R.string.from_booth_must_be_less_than_to_booth)
                    )
                } else if (/*selectedVillage?.villageNo != 0L &&*/ binding.rbCustom.isChecked
                    && binding.etCustom.text.toString().trim().isEmpty()
                ) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_booth_no))
                } else if (!CommonUtils.isNetworkAvailable(this)) {
                    CommonUtils.showToast(this, getString(R.string.no_internet_connection))
                } else {
                    CustomProgressDialog.showProgressDialog(this)
                    val type =
                        if (selectedVillage?.villageNo == 0L) {
                            Enums.Type.ALL.toString()
                        } else {
                            when (binding.rgBooth.checkedRadioButtonId) {
                                R.id.rb_single -> {
                                    Enums.Type.SINGLE.toString()
                                }

                                R.id.rb_from_to -> {
                                    Enums.Type.FROM_TO.toString()
                                }

                                R.id.rb_custom -> {
                                    Enums.Type.CUSTOM.toString()
                                }

                                else -> {
                                    Enums.Type.ALL.toString()
                                }
                            }
                        }
                    if (user != null) {
                        val request = UpdateUserRequest(
                            user!!.userId,
                            selectedVillage?.villageNo ?: 0,
                            if (binding.etSingle.text.toString().trim().isNotEmpty())
                                binding.etSingle.text.toString().trim().toLong()
                            else 0,
                            from, to,
                            binding.etCustom.text.toString().trim(),
                            type,
                            if (binding.activateToggle.isChecked) 1 else 0
                        )
                        voterViewModel.updateUser(request).observe(this) { response ->
                            CustomProgressDialog.dismissProgressDialog()
                            if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                                CommonUtils.showToast(
                                    this, getString(R.string.user_updated_successfully)
                                )
                                setResult(Activity.RESULT_OK)
                                finish()
                            } else if (response?.error != null) {
                                CommonUtils.showToast(this, response.error!!.message)
                            }
                        }
                    }
                }
            }
        }
    }

    private val villageLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedVillage = IntentUtils.getVillageFromIntent(result.data)
            updateVillage(false)
        }
    }
}