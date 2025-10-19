package com.ems.lite.admin.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivitySurwayReportBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.Survey
import com.ems.lite.admin.ui.adapters.SurveyListAdapter
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.IntentConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MobileCasteReportActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity, wardNo: Long?, boothNo: Long?) {
            Intent(activity, MobileCasteReportActivity::class.java).apply {
                putExtra(IntentConstants.VILLAGE_NO, wardNo)
                putExtra(IntentConstants.BOOTH_NO, boothNo)
            }.run {
                activity.startActivity(this)
            }
        }
    }

    private lateinit var binding: ActivitySurwayReportBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val surveyList: ArrayList<Survey> = arrayListOf()

    private lateinit var surwayListAdapter: SurveyListAdapter
    private var villageNo: Long = 0
    private var boothNo: Long = 0

    private var offset: Int = 0
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurwayReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.mobile_caste_count))
        initAdapter()
        initClick()
        init()
    }

    private fun init() {
        villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
        boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
        offset = 0
        getSurwayReportList()
    }


    private fun setNoResult() {
        if (surveyList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
//            binding.llNoResult.tvNoResult.text = getString(R.string.no_product_found)
        }
    }

    private fun getSurwayReportList() {
        if (offset == 0) {
        } else if (loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }

        CoroutineScope(Dispatchers.Main).launch {
            val cList = commonViewModel.getDB().voterDao()
                .getCountByMobileCaste(
                    boothNo, binding.etvoterno.text.toString().trim(),
                    offset * 30
                )
            if (offset == 0)
                surveyList.clear()
            if (!cList.isNullOrEmpty()) {
                surveyList.addAll(cList)
                offset += 1
            } else {
                offset = -1
            }
            if (loading) {
                surwayListAdapter.notifyItemInserted(surveyList.size)
            } else {
                surwayListAdapter.notifyDataSetChanged()
            }
            binding.viewFlipper.displayedChild = 1
            binding.llLoadMore.visibility = View.GONE
            loading = false
            setNoResult()
        }

    }

    private fun initClick() {
        binding.onClickListener = this
        initNoInternet(binding.noInternetLayout) {
            getSurwayReportList()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            loading = false
            getSurwayReportList()
        }

        binding.etvoterno.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                offset = 0
                loading = false
                getSurwayReportList()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

        }
    }

    private fun initAdapter() {
        surwayListAdapter = SurveyListAdapter(surveyList, object : SurveyListAdapter.ItemListener {
            override fun onSurwayClick(survey: Survey) {
            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.scheduleLayoutAnimation()
//        binding.recyclerView.addItemDecoration(
//            SimpleDividerItemDecoration(this)
//        )
        binding.recyclerView.adapter = surwayListAdapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefreshLayout.isEnabled =
                    (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) // 0 is for first item position
                if (dy > 0 && surveyList.isNotEmpty() && offset > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        getSurwayReportList()
                    }
                }
            }
        })
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.excel_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.excel_menu) {
//            CustomProgressDialog.showProgressDialog(this)
//            CoroutineScope(Dispatchers.Main).launch {
//                var fileName = "Report"
//                val countList = commonViewModel.getDB().voterDao()
//                    .getCountByMobileCaste(
//                        villageNo, binding.etvoterno.text.toString().trim(),
//                        offset * 30
//                    )
//                if (!countList.isNullOrEmpty()) {
//                    val headerMessage =
//                        "Village - $villageNo\nBooth - $boothNo"
//                    prepareExcelFile(fileName, headerMessage, ArrayList(countList))
//                } else {
//                    CustomProgressDialog.dismissProgressDialog()
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
}