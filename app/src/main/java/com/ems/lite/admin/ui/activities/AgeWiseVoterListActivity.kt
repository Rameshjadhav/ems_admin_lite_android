package com.ems.lite.admin.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityAgeWiseVoterListBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.ui.adapters.VoterListAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.IntentConstants
import com.ems.lite.admin.utils.SimpleDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AgeWiseVoterListActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivityForResult(
            activity: Activity, villageNo: Long?, boothNo: Long?,
            boothName: String?, launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, AgeWiseVoterListActivity::class.java).apply {
                putExtra(IntentConstants.VILLAGE_NO, villageNo)
                putExtra(IntentConstants.BOOTH_NO, boothNo)
                putExtra(IntentConstants.BOOTH_NAME, boothName)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: ActivityAgeWiseVoterListBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val voterList: ArrayList<Voter> = arrayListOf()

    private lateinit var voterListAdapter: VoterListAdapter
    private var villageNo: Long = 0
    private var boothNo: Long = 0
    private var offset = 0
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgeWiseVoterListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.list_by_age))
        initAdapter()
        initClick()
        init()
    }

    private fun init() {
        villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
        boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
        if (boothNo != 0L)
            binding.etBoothNo.setText(boothNo.toString())
        offset = 0
        searchVoter()
    }

    private fun searchVoter() {
        val voterno = binding.etvoterno.text.toString()
        if (loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val split = voterno.split(" ")
            val search1: String
            var search2 = ""
            var search3 = ""
            if (split.size >= 3) {
                search1 = split[0]
                search2 = split[1]
                search3 = split[2]
            } else if (split.size >= 2) {
                search1 = split[0]
                search2 = split[1]
            } else if (split.isNotEmpty()) {
                search1 = split[0]
            } else {
                search1 = voterno
            }

            var gender = ""
            if (binding.rbMale.isChecked) {
                gender = Enums.Gender.M.toString()
            } else if (binding.rbFemale.isChecked) {
                gender = Enums.Gender.F.toString()
            }

            val list = commonViewModel.getDB().voterDao()
                .searchVoterByAge(
                    search1, search2, search3,
                    villageNo, binding.etBoothNo.text.toString().trim(),
                    binding.etVoterId.text.toString().trim(),
                    binding.etHouseNo.text.toString().trim(),
                    gender, binding.etFrom.text.toString().trim(),
                    binding.etTo.text.toString().trim(),
                    offset * 30
                )
            if (offset == 0)
                voterList.clear()
            if (!list.isNullOrEmpty()) {
                voterList.addAll(list)
                offset += 1
            } else {
                offset = -1
            }
            if (loading) {
                voterListAdapter.notifyItemInserted(voterList.size)
            } else {
                voterListAdapter.notifyDataSetChanged()
            }
            binding.llLoadMore.visibility = View.GONE
            loading = false

        }
    }

    private fun initClick() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_search -> {
                if (binding.etFrom.text.toString().trim()
                        .isNotEmpty() && binding.etTo.text.toString().trim().isEmpty()
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
                    offset = 0
                    searchVoter()
                }
            }

            R.id.btn_reset -> {
                binding.etvoterno.setText("")
                binding.etVoterId.setText("")
                binding.etHouseNo.setText("")
                binding.etBoothNo.setText(if (boothNo != 0L) boothNo.toString() else "")
                binding.rbBoth.isChecked = true
                binding.etFrom.setText("")
                binding.etTo.setText("")
                offset = 0
                searchVoter()
            }
        }
    }

    private fun initAdapter() {
        voterListAdapter = VoterListAdapter(voterList).apply {
            voterClickListener = object : VoterListAdapter.VoterClickListener {
                override fun onItemClick(voter: Voter) {
                    val intent =
                        Intent(this@AgeWiseVoterListActivity, VoterDetailsActivity::class.java)
                    intent.putExtra(IntentConstants.ID, voter._id)
                    detailLauncher.launch(intent)
                }

                override fun onCallClick(mobileNo: String?) {
                    callNumber = mobileNo?.replace("/","")
                    makePhoneCall()
                }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.scheduleLayoutAnimation()
        binding.recyclerView.addItemDecoration(
            com.ems.lite.admin.utils.SimpleDividerItemDecoration(this)
        )
        binding.recyclerView.adapter = voterListAdapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                binding.swipeRefreshLayout.isEnabled =
//                    (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) // 0 is for first item position
                if (dy > 0 && voterList.isNotEmpty() && offset > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        searchVoter()
                    }
                }
            }
        })
    }

    private val detailLauncher =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                offset = 0
                searchVoter()
            }
        }
}