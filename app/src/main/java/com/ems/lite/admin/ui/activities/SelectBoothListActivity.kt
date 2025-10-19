package com.ems.lite.admin.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.BoothListActivityBinding
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.ui.adapters.BoothListAdapter
import com.ems.lite.admin.utils.IntentConstants
import com.ems.lite.admin.utils.IntentUtils
import com.ems.lite.admin.utils.SimpleDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectBoothListActivity : BaseActivity() {
    companion object {
        fun startActivityForResult(
            activity: Activity,
            villageNo: Long?,
            booth: Booth?,
            launcher: ActivityResultLauncher<Intent>,
            showAllOption: Boolean = true
        ) {
            Intent(activity, SelectBoothListActivity::class.java).apply {
                putExtra(IntentConstants.VILLAGE_NO, villageNo)
                IntentUtils.putBoothToIntent(this, booth)
                putExtra(IntentConstants.SHOW_ALL_OPTION, showAllOption)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: BoothListActivityBinding
    private val boothArrayList: ArrayList<Booth> = arrayListOf()
    private var villageNo: Long = 0
    private var booth: Booth? = null
    private var showAllOption = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.booth_list_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.select_booth))
        initClick()
        initAdapter()
        init()
    }

    private fun init() {
        intent.run {
            showAllOption = getBooleanExtra(IntentConstants.SHOW_ALL_OPTION, false)
            villageNo = getLongExtra(IntentConstants.VILLAGE_NO, 0)
            booth = IntentUtils.getBoothFromIntent(intent)
            binding.adapter!!.selectedBooth = booth
        }
        getBoothList()
    }

    private fun getBoothList() {
        if (!binding.swipeRefreshLayout.isRefreshing)
            binding.viewFlipper.displayedChild = 0
        CoroutineScope(Dispatchers.Main).launch {
            val slist = voterViewModel.getDB().BoothDao()
                .getAllByVillageNo(villageNo, binding.etSearch.text.toString().trim())
            binding.swipeRefreshLayout.isRefreshing = false
            boothArrayList.clear()
            if (!slist.isNullOrEmpty()) {
                if (binding.etSearch.text.toString().trim().isEmpty() && showAllOption) {
                    val booth = Booth().apply {
                        id = 0
                        boothNo = 0
                        boothName = getString(R.string.all)
                        boothNameEng = getString(R.string.all)
                    }
                    boothArrayList.add(booth)
                }
                boothArrayList.addAll(slist)
            }
            binding.adapter!!.notifyDataSetChanged()
            setNoResult()
        }
    }

    private fun setNoResult() {
        if (boothArrayList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
            binding.llNoResult.tvNoResult.text = getString(R.string.no_booth_found)
        }
    }

    private fun initClick() {
        initNoInternet(binding.noInternetLayout) {
            getBoothList()
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getBoothList()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            getBoothList()
        }
    }

    private fun initAdapter() {
        binding.adapter = BoothListAdapter(boothArrayList).apply {
            boothClickListener = object : BoothListAdapter.BoothClickListener {
                override fun onItemClick(booth: Booth) {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        IntentUtils.putBoothToIntent(this, booth)
                    })
                    finish()
                }
            }
        }
        binding.recyclerView.addItemDecoration(
            com.ems.lite.admin.utils.SimpleDividerItemDecoration(this)
        )
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefreshLayout.isEnabled =
                    (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) // 0 is for first item position
                /*if (dy > 0 && newsList.isNotEmpty() && offset > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        getProductList()
                    }
                }*/
            }
        })
    }

    val userDetailLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            getBoothList()
        }
    }
}