package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityCommitteeListBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.ui.adapters.CommitteeListAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.IntentConstants
import com.ems.lite.admin.utils.SimpleDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CommitteeListActivity : BaseActivity() {
    companion object {
        fun startActivityForResult(
            activity: Activity, villageNo: Long?, boothNo: Long?,
            launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, CommitteeListActivity::class.java).apply {
                putExtra(IntentConstants.VILLAGE_NO, villageNo)
                putExtra(IntentConstants.BOOTH_NO, boothNo)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: ActivityCommitteeListBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val voterList: ArrayList<Voter> = arrayListOf()

    private lateinit var voterListAdapter: CommitteeListAdapter
    private var villageNo: Long = 0
    private var boothNo: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommitteeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.booth_committee))
        initAdapter()
        initClickListener()
        init()
    }

    private fun init() {
        villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
        boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
        loadCommitteeList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadCommitteeList() {
        val type =
            when {
                binding.rbAdmin.isChecked -> {
                    Enums.Committee.ADMIN.toString()
                }

                binding.rbMember.isChecked -> {
                    Enums.Committee.MEMBER.toString()
                }

                else -> {
                    ""
                }
            }
        CoroutineScope(Dispatchers.Main).launch {
            val list =
                commonViewModel.getDB().voterDao()
                    .getCommitteeList(villageNo, boothNo, type)
            voterList.clear()
            if (!list.isNullOrEmpty()) {
                voterList.addAll(list)
            }
            voterListAdapter.notifyDataSetChanged()
        }
    }

    private fun initClickListener() {
        binding.rgCommittee.setOnCheckedChangeListener { _, _ -> loadCommitteeList() }
    }

    private fun initAdapter() {
        voterListAdapter =
            CommitteeListAdapter(voterList, object : CommitteeListAdapter.ItemListener {
                override fun onVoterClick(voter: Voter) {
                    val intent =
                        Intent(this@CommitteeListActivity, VoterDetailsActivity::class.java)

                    intent.putExtra(IntentConstants.ID, voter._id)
                    updateLauncher.launch(intent)
                }

                private var voter: Voter? = null
                override fun onCallClick(voter: Voter) {
                    if (!voter.mobileNo.isNullOrEmpty()) {
                        if (ContextCompat.checkSelfPermission(
                                this@CommitteeListActivity, android.Manifest.permission.CALL_PHONE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            this.voter = voter
                            permissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
                        } else {
                            val intent =
                                Intent(
                                    Intent.ACTION_CALL,
                                    Uri.parse("tel:" + Uri.encode(voter.mobileNo))
                                )
                            startActivity(intent)
                        }
                    }
                }

                private val permissionLauncher =
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                        if (it) {
                            val intent =
                                Intent(
                                    Intent.ACTION_CALL,
                                    Uri.parse("tel:" + Uri.encode(voter?.mobileNo))
                                )
                            startActivity(intent)
                        } else {
                            CommonUtils.showToast(
                                this@CommitteeListActivity, getString(R.string.permission_denied)
                            )
                        }
                    }
            })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.scheduleLayoutAnimation()
        binding.recyclerView.addItemDecoration(
            com.ems.lite.admin.utils.SimpleDividerItemDecoration(this)
        )
        binding.recyclerView.adapter = voterListAdapter
    }

    private val updateLauncher =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                loadCommitteeList()
            }
        }


}