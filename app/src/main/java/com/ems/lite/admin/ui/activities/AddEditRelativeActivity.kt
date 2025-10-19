package com.ems.lite.admin.ui.activities


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.AddEditRelativeActivityBinding
import com.ems.lite.admin.model.Relative
import com.ems.lite.admin.model.Taluka
import com.ems.lite.admin.model.request.SaveRelativeRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.*
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.ui.adapters.SpinnerAdapter
import com.ems.lite.admin.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

open class AddEditRelativeActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivityForResult(
            activity: Activity, cardNo: String?, relative: Relative?,
            addSurveyLauncher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, AddEditRelativeActivity::class.java).apply {
                putExtra(IntentConstants.VOTER, cardNo)
                putExtra(IntentConstants.RELATIVE, relative)
            }.run {
                addSurveyLauncher.launch(this)
            }
        }
    }

    private lateinit var binding: AddEditRelativeActivityBinding
    private val talukaList: ArrayList<Taluka> = arrayListOf()
    private val professionList: ArrayList<Profession> = arrayListOf()
    private var cardNo: String? = null
    private var relative: Relative? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.add_edit_relative_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.relative))
        initObserver()
        initSpinnerAdapter()
        initClickListener()
        init()
    }

    private fun init() {
        voterViewModel.getRelativeTalukaMaster()
        cardNo = intent.getStringExtra(IntentConstants.VOTER)
        relative = intent.getParcelableExtra(IntentConstants.RELATIVE)
        binding.relative = relative
        if (relative != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (relative?.professionNo != 0L) {
                    if (professionList.isNotEmpty()) {
                        val profession =
                            professionList.find { it.professionNo == relative?.professionNo }
                        if (profession != null) {
                            binding.professionSpinner.setSelection(
                                professionList.indexOf(profession)
                            )
                        }
                    }
                }
            }, 300)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        lifecycleScope.launch {
            voterViewModel.talukaListState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            talukaList.clear()
                            if (!it.data?.list.isNullOrEmpty()) {
                                talukaList.add(
                                    Taluka()
                                        .apply {
                                            talukaName = getString(R.string.select_taluka)
                                        })
                                talukaList.addAll(it.data?.list!!)
                                if (!relative?.talId.isNullOrEmpty()) {
                                    val index =
                                        talukaList.indexOf(talukaList.find { org ->
                                            org.talId.equals(relative?.talId, true)
                                        })
                                    if (index > -1) {
                                        Handler(Looper.getMainLooper()).postDelayed(
                                            { binding.talukaSpinner.setSelection(index) }, 300
                                        )
                                    }
                                }
                            }
                            binding.talukaAdapter?.notifyDataSetChanged()
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@AddEditRelativeActivity, it.message)
                    }
                }
            }
        }
        lifecycleScope.launch {
            voterViewModel.saveRelativeState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            CommonUtils.showToast(
                                this@AddEditRelativeActivity,
                                getString(R.string.relative_saved_successfully)
                            )
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@AddEditRelativeActivity, it.message)
                    }
                }
            }
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_save -> {
                if (isValidInput()) {
                    saveOfficials()
                }
            }

        }
    }

    private fun isValidInput(): Boolean {
        return when {

            binding.talukaSpinner.selectedItemPosition == 0 -> {
                CommonUtils.showToast(this, getString(R.string.pls_select_taluka))
                return false
            }

            binding.etVillageName.text.toString().trim().isEmpty() -> {
                CommonUtils.showToast(this, getString(R.string.pls_enter_village_name))
                false
            }

            binding.etRelativeName.text.toString().trim().isEmpty() -> {
                CommonUtils.showToast(this, getString(R.string.pls_enter_relative_name))
                false
            }

            binding.etContactNumber.text.toString().trim().isEmpty() -> {
                CommonUtils.showToast(this, getString(R.string.pls_enter_contact_number))
                false
            }

            binding.etContactNumber.text.toString().trim().isNotEmpty()
                    && !CommonUtils.isValidMobile(
                binding.etContactNumber.text.toString().trim()
            ) -> {
                CommonUtils.showToast(this, getString(R.string.pls_enter_valid_number))
                false
            }

            binding.etRelationWithHead.text.toString().trim().isEmpty() -> {
                CommonUtils.showToast(this, getString(R.string.pls_enter_relation_with_family_head))
                false
            }

            else -> {
                true
            }
        }
    }

    private fun saveOfficials() {
        val user = Prefs.user
        val request = SaveRelativeRequest(
            relative?.relId, cardNo,
            talukaList[binding.talukaSpinner.selectedItemPosition].talId,
            binding.etVillageName.text.toString().trim(),
            binding.etRelativeName.text.toString().trim(),
            binding.etContactNumber.text.toString().trim(),
            binding.etRelationWithHead.text.toString().trim(),
            professionList[binding.professionSpinner.selectedItemPosition].professionNo,
            user?.userId
        )
        voterViewModel.saveRelative(request)
    }

    private fun initSpinnerAdapter() {
        binding.talukaAdapter = SpinnerAdapter(
            this, R.layout.spinner_item, talukaList
        )
        binding.talukaSpinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View, position: Int, id: Long
                ) {
                    CommonUtils.updateDisabledPositionInSpinner(
                        context, parent, position, binding.talukaAdapter!!.disabledPosition
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
        binding.professionAdapter = SpinnerAdapter(
            this, R.layout.spinner_item, professionList
        )
        binding.professionSpinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View, position: Int, id: Long
                ) {
                    CommonUtils.updateDisabledPositionInSpinner(
                        context, parent, position, binding.professionAdapter!!.disabledPosition
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            val list = voterViewModel.getDB().ProfessionDao().getAll()
            if (!list.isNullOrEmpty()) {
                professionList.clear()
                professionList.addAll(list)
                binding.professionAdapter?.notifyDataSetChanged()
            }
        }
    }
}