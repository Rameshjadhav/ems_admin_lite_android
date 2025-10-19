package com.ems.lite.admin.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityVoterListUnderImpVoterBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.ui.adapters.VoterListUnderImpVoterAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.IntentConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoterListUnderImpVoterActivity : BaseActivity() {
    companion object {
        fun startActivityForResult(
            activity: Activity, villageNo: Long, boothNo: Long,
            cardNo: String?, isFromInflencer: Boolean,
            updateLauncher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, VoterListUnderImpVoterActivity::class.java).apply {
                putExtra(IntentConstants.VILLAGE_NO, villageNo)
                putExtra(IntentConstants.BOOTH_NO, boothNo)
                putExtra(IntentConstants.CARD_NO, cardNo)
                putExtra(IntentConstants.FROM, isFromInflencer)
            }.run { updateLauncher.launch(this) }
        }
    }

    private lateinit var binding: ActivityVoterListUnderImpVoterBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val voterList: ArrayList<Voter> = arrayListOf()

    private lateinit var voterListAdapter: VoterListUnderImpVoterAdapter
    private var villageNo: Long = 0
    private var boothNo: Long = 0
    private var cardNo: String? = null
    private var voter: Voter? = null
    private var isFromInflencer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoterListUnderImpVoterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
//        setToolBarTitle(getString(R.string.booth_committee))
        initAdapter()
        init()
    }

    private fun init() {
        cardNo = intent.getStringExtra(IntentConstants.CARD_NO)
        villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
        boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
        isFromInflencer = intent.getBooleanExtra(IntentConstants.FROM, false)
        CoroutineScope(Dispatchers.Main).launch {
            voter = commonViewModel.getDB().voterDao().getVoterByCardNo(cardNo)
            setToolBarTitle(voter?.getFullName())
            loadVoterList()
        }
    }

    private fun loadVoterList() {
        CoroutineScope(Dispatchers.Main).launch {
            val list =
                if (isFromInflencer) {
                    commonViewModel.getDB().voterDao().getVoterListUnderImpVoter(
                        villageNo, boothNo, voter?.cardNo
                    )
                } else {
                    commonViewModel.getDB().voterDao()
                        .getVoterListUnderImpVoter(voter?.cardNo)
                }
            voterList.clear()
            if (!list.isNullOrEmpty()) {
                voterList.addAll(list)
            }
            voterListAdapter.notifyDataSetChanged()
        }
    }

    private fun initAdapter() {
        voterListAdapter =
            VoterListUnderImpVoterAdapter(
                voterList,
                object : VoterListUnderImpVoterAdapter.ItemListener {
                    override fun onVoterClick(voter: Voter) {
                        val intent =
                            Intent(
                                this@VoterListUnderImpVoterActivity,
                                VoterDetailsActivity::class.java
                            )

                        intent.putExtra(IntentConstants.ID, voter._id)
                        updateLauncher.launch(intent)
                    }

                    override fun onDeleteClick(voter: Voter) {
                        CoroutineScope(Dispatchers.Main).launch {
                            commonViewModel.getDB().voterDao()
                                .updateVoterUnderImpVoter(voter._id, null)
                            setResult(Activity.RESULT_OK)
                            CommonUtils.showToast(
                                this@VoterListUnderImpVoterActivity,
                                getString(R.string.voter_removed_successfully)
                            )
                            loadVoterList()
                        }
                    }

                    private var voter: Voter? = null
                    override fun onCallClick(voter: Voter) {
                        if (!voter.mobileNo.isNullOrEmpty()) {
                            if (ContextCompat.checkSelfPermission(
                                    this@VoterListUnderImpVoterActivity,
                                    android.Manifest.permission.CALL_PHONE
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
                                    this@VoterListUnderImpVoterActivity,
                                    getString(R.string.permission_denied)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.call_add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.call_meu -> {
                if (!voter?.mobileNo.isNullOrEmpty()) {
                    if (ContextCompat.checkSelfPermission(
                            this@VoterListUnderImpVoterActivity,
                            android.Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
                    } else {
                        val intent =
                            Intent(
                                Intent.ACTION_CALL,
                                Uri.parse("tel:" + Uri.encode(voter?.mobileNo))
                            )
                        startActivity(intent)
                    }
                }
            }

            R.id.add_meu -> {
                Intent(this, SearchActivity::class.java).apply {
                    putExtra(IntentConstants.CARD_NO, cardNo)
                    putExtra(IntentConstants.VILLAGE_NO, villageNo)
                    putExtra(IntentConstants.BOOTH_NO, boothNo)
                }.run {
                    updateLauncher.launch(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
                    this@VoterListUnderImpVoterActivity,
                    getString(R.string.permission_denied)
                )
            }
        }
    private val updateLauncher =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                loadVoterList()
            }
        }


}