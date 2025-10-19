package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.InfluencerListActivityBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.Influencer
import com.ems.lite.admin.ui.adapters.InfluencerListAdapter
import com.ems.lite.admin.utils.IntentConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfluencerListActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivityForResult(
            activity: Activity, villageNo: Long?, boothNo: Long?,
            launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, InfluencerListActivity::class.java).apply {
                putExtra(IntentConstants.VILLAGE_NO, villageNo)
                putExtra(IntentConstants.BOOTH_NO, boothNo)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: InfluencerListActivityBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val influencerList: ArrayList<Influencer> = arrayListOf()
    private var villageNo: Long = 0
    private var boothNo: Long = 0
    private var offset = 0
    private var loading = false

    private var cardNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.influencer_list_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.influencer))
        initAdapter()
        initClick()
        init()
    }

    private fun init() {
        villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
        boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
        offset = 0
        searchVoter()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchVoter() {

        val search = binding.etSearch.text.toString()
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val list = commonViewModel.getDB().voterDao()
                .getInfluencerList(
                    search, offset * 30
                )

            if (offset == 0)
                influencerList.clear()
            if (!list.isNullOrEmpty()) {
                influencerList.addAll(list)
                offset += 1
            } else {
                offset = -1
            }
            if (loading) {
                binding.adapter!!.notifyItemInserted(influencerList.size)
            } else {
                binding.adapter!!.notifyDataSetChanged()
            }
            binding.llLoadMore.visibility = View.GONE
            loading = false

        }
    }

    private fun initClick() {
        binding.onClickListener = this
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                offset = 0
                searchVoter()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

        }
    }

    private fun initAdapter() {
        binding.adapter = InfluencerListAdapter(influencerList).apply {
            voterClickListener = object : InfluencerListAdapter.VoterClickListener {
                override fun onItemClick(influencer: Influencer) {
                    VoterListUnderImpVoterActivity.startActivityForResult(
                        this@InfluencerListActivity,
                        influencer.villageNo, influencer.boothNo,
                        influencer.refVoterNo, true, updateLauncher
                    )
                }

                override fun onCallClick(influencer: Influencer) {
                    callNumber = influencer.mobileNo?.replace("/", "")
                    makePhoneCall()
                }

                override fun onWhatsAppClick(influencer: Influencer) {

                }
            }
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                binding.swipeRefreshLayout.isEnabled =
//                    (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) // 0 is for first item position
                if (dy > 0 && influencerList.isNotEmpty() && offset > 0) {
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

    private val updateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                offset = 0
                searchVoter()
            }
        }
}