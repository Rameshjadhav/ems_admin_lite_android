package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
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
import com.ems.lite.admin.databinding.VillageListActivityBinding
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.ui.adapters.VillageListAdapter
import com.ems.lite.admin.utils.IntentConstants
import com.ems.lite.admin.utils.IntentUtils
import com.ems.lite.admin.utils.SimpleDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectVillageListActivity : BaseActivity() {

    companion object {
        fun startActivityForResult(
            activity: Activity, village: Village?,
            launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, SelectVillageListActivity::class.java).apply {
                IntentUtils.putVillageToIntent(this, village)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: VillageListActivityBinding
    private val villageArrayList: ArrayList<Village> = arrayListOf()
    private var village: Village? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.village_list_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.select_village))
        initClick()
        initAdapter()
        init()
    }

    private fun init() {
        village = IntentUtils.getVillageFromIntent(intent)
        binding.adapter!!.selectedVillage = village
        getVillageList()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getVillageList() {
        if (!binding.swipeRefreshLayout.isRefreshing)
            binding.viewFlipper.displayedChild = 0
        CoroutineScope(Dispatchers.Main).launch {
            val slist =
                voterViewModel.getDB().villageDao()
                    .getAll(binding.etSearch.text.toString().trim())
            binding.swipeRefreshLayout.isRefreshing = false
            villageArrayList.clear()
            if (!slist.isNullOrEmpty()) {
                if (binding.etSearch.text.toString().trim().isEmpty()) {
                    val village = Village().apply {
                        id = 0
                        villageNo = 0
                        villageName = getString(R.string.all)
                        villageNameEng = getString(R.string.all)
                    }
                    villageArrayList.add(village)
                }
                villageArrayList.addAll(slist)
            }
            binding.adapter!!.notifyDataSetChanged()
            setNoResult()
        }
    }

    private fun setNoResult() {
        if (villageArrayList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
            binding.llNoResult.tvNoResult.text = getString(R.string.no_ward_found)
        }
    }

    private fun initClick() {
        initNoInternet(binding.noInternetLayout) {
            getVillageList()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            getVillageList()
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getVillageList()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }

    private fun initAdapter() {
        binding.adapter =
            VillageListAdapter(villageArrayList).apply {
                villageClickListener = object : VillageListAdapter.VillageClickListener {
                    override fun onItemClick(village: Village) {
                        setResult(Activity.RESULT_OK, Intent().apply {
                            IntentUtils.putVillageToIntent(this, village)
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
            getVillageList()
        }
    }
}