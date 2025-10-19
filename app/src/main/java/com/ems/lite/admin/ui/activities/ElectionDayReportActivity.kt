package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityElectionDayReportBinding
import com.ems.lite.admin.model.request.VoterListRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.ui.adapters.VoterListAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.IntentConstants
import com.ems.lite.admin.utils.IntentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ElectionDayReportActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(
            activity: Activity, village: Village?, booth: Booth?
        ) {
            Intent(activity, ElectionDayReportActivity::class.java).apply {
                putExtra(IntentConstants.VILLAGE, village)
                putExtra(IntentConstants.BOOTH, booth)
            }.run {
                activity.startActivity(this)
            }
        }
    }

    private lateinit var binding: ActivityElectionDayReportBinding
    private val voterList: ArrayList<Voter> = arrayListOf()
    private var village: Village? = null
    private var booth: Booth? = null

    private lateinit var voterListAdapter: VoterListAdapter
    private var offset: Long = 0
    private var loading = false
    private var selectedStatus: String? = Enums.Status.SELECT.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElectionDayReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.election_day))
        initObserver()
        initClickListener()
        initAdapter()
        init()
    }

    private fun init() {
        village = IntentUtils.getVillageFromIntent(intent)
        booth = IntentUtils.getBoothFromIntent(intent)
        updateStatusView()
    }

    private fun getVillageList() {
        CoroutineScope(Dispatchers.Main).launch {
            val list = voterViewModel.getDB().villageDao().getAll()
            if (!list.isNullOrEmpty()) {
                village = list.find { it.villageNo == village?.villageNo }
                updateVillage()
            }
        }
    }

    private fun reloadList() {
        offset = 0
        getVoterList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        lifecycleScope.launch {
            voterViewModel.voterListState.collect {
                when (it.status) {
                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {
                        binding.llLoadMore.visibility = View.GONE
                        if (it.code == ResponseStatus.STATUS_CODE_SUCCESS && it.data != null) {
                            if (offset == 0L)
                                voterList.clear()
                            if (!it.data.list.isNullOrEmpty()) {
                                voterList.addAll(it.data.list!!)
                            }
                            if (loading) {
                                voterListAdapter.notifyItemInserted(voterList.size)
                            } else {
                                voterListAdapter.notifyDataSetChanged()
                            }
                            offset = it.data.nextOffset
                            binding.tvReportCount.text = it.data.totalCount.toString()
                        }
                        setNoResult()
                        loading = false
                    }

                    Status.ERROR -> {
                        setNoResult()
                        binding.llLoadMore.visibility = View.GONE
                        loading = false
                    }
                }
            }
        }
    }

    private fun setNoResult() {
        if (voterList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
//            binding.llNoResult.tvNoResult.text = getString(R.string.no_product_found)
        }
    }

    private fun getVoterList() {
        if (CommonUtils.isNetworkAvailable(this)) {
            if (loading) {
                binding.llLoadMore.visibility = View.VISIBLE
            } else {
                binding.viewFlipper.displayedChild = 0
            }
            voterViewModel.getVoterList(
                VoterListRequest(
                    village?.villageNo,
                    booth?.boothNo,
                    if (binding.rbVoted.isChecked) "voted" else "nonvoted",
                    if (selectedStatus != Enums.Status.SELECT.toString()) selectedStatus else "0",
                    offset
                )
            )
        } else {
            if (offset == 0L)
                binding.viewFlipper.displayedChild = 3
            else
                CommonUtils.showToast(this, getString(R.string.no_internet_connection))
            loading = false
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        initNoInternet(binding.noInternetLayout) {
            getVoterList()
        }
        binding.rgVotedNonVoted.setOnCheckedChangeListener { _, _ -> reloadList() }
    }

    private fun updateStatusView() {
        binding.selectedImg.setImageResource(
            if (!selectedStatus.isNullOrEmpty()) {
                when (selectedStatus) {
                    Enums.Status.GREEN.toString() -> R.drawable.green
                    Enums.Status.YELLOW.toString() -> R.drawable.yellow
                    Enums.Status.OTHER.toString() -> R.drawable.other
                    Enums.Status.RED.toString() -> R.drawable.red
                    else -> R.drawable.nocolor
                }
            } else {
                R.drawable.nocolor
            }
        )
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_village -> {
                SelectVillageListActivity.startActivityForResult(
                    this, village, villageLauncher
                )
            }

            R.id.et_booth -> {
                if (village != null) {
                    SelectBoothListActivity.startActivityForResult(
                        this, village!!.villageNo, booth, boothLauncher, true
                    )
                } else {
                    CommonUtils.showToast(this, getString(R.string.pls_select_ward_first))
                }
            }

            R.id.imgNoColor -> {
                selectedStatus = Enums.Status.SELECT.toString()
                updateStatusView()
                reloadList()
            }

            R.id.imgGreen -> {
                selectedStatus = Enums.Status.GREEN.toString()
                updateStatusView()
                reloadList()
            }

            R.id.imgYellow -> {
                selectedStatus = Enums.Status.YELLOW.toString()
                updateStatusView()
                reloadList()
            }

            R.id.imgOther -> {
                selectedStatus = Enums.Status.OTHER.toString()
                updateStatusView()
                reloadList()
            }

            R.id.imgRed -> {
                selectedStatus = Enums.Status.RED.toString()
                updateStatusView()
                reloadList()
            }
        }
    }

    private fun initAdapter() {
        voterListAdapter =
            VoterListAdapter(voterList).apply {
                voterClickListener = object : VoterListAdapter.VoterClickListener {
                    override fun onItemClick(voter: Voter) {
                        val intent =
                            Intent(this@ElectionDayReportActivity, VoterDetailsActivity::class.java)
                        intent.putExtra(IntentConstants.ID, voter._id)
                        startActivity(intent)
                    }

                    override fun onCallClick(mobileNo: String?) {
                        callNumber = mobileNo?.replace("/", "")
                        makePhoneCall()
                    }
                }
            }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.scheduleLayoutAnimation()
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
                        getVoterList()
                    }
                }
            }
        })
    }

    private fun updateVillage() {
        if (village != null) {
            binding.etVillage.setText(village!!.toString())
            if (booth == null || booth?.villageNo != village?.villageNo) {
                booth = Booth().apply {
                    id = 0
                    boothNo = 0
                    boothName = getString(R.string.all)
                    boothNameEng = getString(R.string.all)
                }
            }
            updateBooth()
        }
    }

    private fun updateBooth() {
        if (booth != null) {
            binding.etBooth.setText(booth!!.getName())
        }
        reloadList()
    }

    private val villageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                village = IntentUtils.getVillageFromIntent(result.data)
                updateVillage()
            }
        }

    private val boothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                booth = IntentUtils.getBoothFromIntent(result.data)
                updateBooth()
            }
        }
}