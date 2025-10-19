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
import com.ems.lite.admin.databinding.RelativeCountListActivityBinding
import com.ems.lite.admin.model.RelativeCount
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.ui.adapters.RelativeCountListAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.IntentConstants
import kotlinx.coroutines.launch
import java.util.concurrent.Semaphore

class RelativeCountListActivity : BaseActivity() {
    companion object {
        fun startActivityForResult(
            activity: Activity, villageNo: Long?, boothNo: Long?,
            boothName: String?, launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, RelativeCountListActivity::class.java).apply {
                putExtra(IntentConstants.VILLAGE_NO, villageNo)
                putExtra(IntentConstants.BOOTH_NO, boothNo)
                putExtra(IntentConstants.BOOTH_NAME, boothName)
            }.run {
                activity.startActivity(this)
            }
        }
    }

    private lateinit var binding: RelativeCountListActivityBinding
    private val relativeList: ArrayList<RelativeCount> = arrayListOf()
    private val semaphore = Semaphore(1)
    private var villageNo: Long = 0
    private var boothNo: Long = 0
    private var offset: Long = 0
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RelativeCountListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.relative))
        initAdapter()
        initObserver()
        initClick()
        init()
    }

    private fun init() {
        villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
        boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
        offset = 0
        getRelativeList()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        lifecycleScope.launch {
            voterViewModel.relativeCountListState.collect {
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
                        CommonUtils.showErrorMessage(this@RelativeCountListActivity, it.message)
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
                voterViewModel.getRelativeCountList(
                    com.ems.lite.admin.model.request.RelativeCountListRequest(
                        villageNo, boothNo, 0,
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

    private fun initAdapter() {
        binding.adapter = RelativeCountListAdapter(relativeList).apply {
            relativeListener = object : RelativeCountListAdapter.RelativeListener {
                override fun onItemSelect(relative: RelativeCount) {
                    RelativeListActivity.startActivityForResult(
                        this@RelativeCountListActivity, relative.voterCardNo, addRelativeLauncher
                    )
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