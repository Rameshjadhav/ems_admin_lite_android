package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.RelativeListActivityBinding
import com.ems.lite.admin.model.Relative
import com.ems.lite.admin.model.request.RelativeListRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.ui.adapters.RelativeListAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.IntentConstants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.Semaphore

class RelativeListActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity, cardNo: String?) {
            Intent(activity, RelativeListActivity::class.java).apply {
                putExtra(IntentConstants.VOTER, cardNo)
            }.run {
                activity.startActivity(this)
            }
        }
        fun startActivityForResult(activity: Activity, cardNo: String?,launcher: ActivityResultLauncher<Intent>) {
            Intent(activity, RelativeListActivity::class.java).apply {
                putExtra(IntentConstants.VOTER, cardNo)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: RelativeListActivityBinding
    private val relativeList: ArrayList<Relative> = arrayListOf()
    private val semaphore = Semaphore(1)
    private var cardNo: String? = null
    private var offset: Long = 0
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RelativeListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.relative))
        initAdapter()
        initObserver()
        initClick()
        init()
    }

    private fun init() {
        cardNo = intent.getStringExtra(IntentConstants.VOTER)
        offset = 0
        getRelativeList()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        lifecycleScope.launch {
            voterViewModel.relativeListState.collect {
                when (it.status) {
                    Status.LOADING -> {
//                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.llLoadMore.visibility = View.GONE
                        if (it.data != null && it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            val response = it.data
                            if (offset == 0L) {
                                relativeList.clear()
                            }
                            if (!response.list.isNullOrEmpty()) {
                                relativeList.addAll(response.list!!)
                            }
                            if (loading) {
                                binding.adapter?.notifyItemInserted(relativeList.size)
                            } else {
                                binding.adapter?.notifyDataSetChanged()
                            }
                            offset = response.nextOffset
                        }
                        loading = false
                        setNoResult()
                        semaphore.release()
                        if (it.data?.keyword != null && !it.data.keyword!!.equals(
                                binding.etvoterno.text.toString().trim(), true
                            )
                        ) {
                            getRelativeList()
                        }
                    }
                    Status.ERROR -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.llLoadMore.visibility = View.GONE
                        loading = false
                        setNoResult()
                        semaphore.release()
                        CommonUtils.showErrorMessage(this@RelativeListActivity, it.message)
                    }
                }
            }

        }
    }

    private fun setNoResult() {
        if (relativeList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
//            binding.llNoResult.tvNoResult.text = getString(R.string.no_product_found)
        }
    }

    private fun getRelativeList() {
        if (semaphore.tryAcquire()) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (offset == 0L) {
                    if (!binding.swipeRefreshLayout.isRefreshing)
                        binding.viewFlipper.displayedChild = 0
                } else if (loading) {
                    binding.llLoadMore.visibility = View.VISIBLE
                }
                voterViewModel.getRelativeList(
                    RelativeListRequest(
                        cardNo,
                        binding.etvoterno.text.toString().trim(),
                        offset
                    )
                )
            } else {
                if (!loading && !binding.swipeRefreshLayout.isRefreshing) {
                    binding.viewFlipper.displayedChild = 3
                } else {
                    CommonUtils.showErrorMessage(
                        this,
                        getString(R.string.no_internet_connection)
                    )
                }
                binding.swipeRefreshLayout.isRefreshing = false
                binding.llLoadMore.visibility = View.GONE
                loading = false
            }
        }
    }

    private fun initClick() {
        binding.onClickListener = this
        initNoInternet(binding.noInternetLayout) {
            getRelativeList()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            loading = false
            getRelativeList()
        }

        binding.etvoterno.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                offset = 0
                loading = false
                getRelativeList()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_add -> {
                AddEditRelativeActivity.startActivityForResult(
                    this, cardNo, null, addRelativeLauncher
                )
            }
        }
    }

    private fun initAdapter() {
        binding.adapter = RelativeListAdapter(relativeList).apply {
            relativeListener = object : RelativeListAdapter.RelativeListener {
                override fun onItemSelect(relative: Relative) {
                    AddEditRelativeActivity.startActivityForResult(
                        this@RelativeListActivity, cardNo, relative, addRelativeLauncher
                    )
                }

                override fun onCallClick(mobile: String?) {
                    callNumber = mobile
                    makePhoneCall()
                }
            }
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefreshLayout.isEnabled =
                    (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) // 0 is for first item position
                if (dy > 0 && relativeList.isNotEmpty() && offset > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        getRelativeList()
                    }
                }
            }
        })
    }

    private val addRelativeLauncher =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                offset = 0
                getRelativeList()
            }
        }
}