package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityCountDetailsBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.table.CountBy
import com.ems.lite.admin.ui.adapters.CountListAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.IntentConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CountDetailsActivity : BaseActivity() {
    companion object {
        fun startActivityForResult(
            activity: Activity, reportType: String?,
            villageNo: Long?, boothNo: Long?,
            boothName: String?,
            isFromSelect: Boolean,
            launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, CountDetailsActivity::class.java).apply {
                putExtra(IntentConstants.VALUE, reportType)
                putExtra(IntentConstants.VILLAGE_NO, villageNo)
                putExtra(IntentConstants.BOOTH_NO, boothNo)
                putExtra(IntentConstants.BOOTH_NAME, boothName)
                putExtra(IntentConstants.FROM, isFromSelect)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: ActivityCountDetailsBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val countList: ArrayList<CountBy> = arrayListOf()
    private var reportType: String? = null
    private var villageNo: Long = 0
    private var boothNo: Long = 0
    private var boothName: String? = null
    private var offset = 0
    private var loading = false
    private var isFromSelect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        initAdapter()
        initClickListener()
        init()
    }

    private fun init() {
        villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
        boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
        reportType = intent.getStringExtra(IntentConstants.VALUE)
        boothName = intent.getStringExtra(IntentConstants.BOOTH_NAME)
        isFromSelect = intent.getBooleanExtra(IntentConstants.FROM, false)
        binding.etSearch.visibility = View.GONE
        binding.rgFamilyCompleted.visibility = View.GONE
        binding.crNameSearch.visibility = View.GONE
        when (reportType) {
            Enums.ReportType.FAMILY.toString() -> {
                setToolBarTitle(getString(R.string.list_by_family))
                binding.rgFamilyCompleted.visibility = View.VISIBLE
                binding.crNameSearch.visibility = View.VISIBLE
                binding.etSearch.visibility = View.GONE
                binding.etSearch.hint = getString(R.string.search_surname)
            }

            Enums.ReportType.SURNAME.toString() -> {
                if (isFromSelect) {
                    setToolBarTitle(getString(R.string.select_surname))
                } else {
                    setToolBarTitle(getString(R.string.list_by_surname))
                }
            }

            Enums.ReportType.ADDRESS.toString() -> {
                setToolBarTitle(getString(R.string.list_by_address))
                binding.etSearch.visibility = View.VISIBLE
                binding.etSearch.hint = getString(R.string.address)
            }

            Enums.ReportType.STATUS.toString() -> {
                setToolBarTitle(getString(R.string.list_by_status))
            }

            Enums.ReportType.CAST.toString() -> {
                setToolBarTitle(getString(R.string.list_by_cast))
            }

            Enums.ReportType.OUTSTATION.toString() -> {
                setToolBarTitle(getString(R.string.list_by_out_of_villege))
            }

            Enums.ReportType.PROFESSION.toString() -> {
                setToolBarTitle(getString(R.string.list_by_profession))
            }

            Enums.ReportType.IMP_VOTER.toString() -> {
                setToolBarTitle(getString(R.string.imp_voter))
                binding.etSearch.visibility = View.VISIBLE
                binding.etSearch.hint = getString(R.string.search)
            }
        }
        reloadList()
    }

    private fun reloadList() {
        offset = 0
        loading = false
        search()
    }

    private fun search() {
        when (reportType) {
            Enums.ReportType.FAMILY.toString() -> {
                loadCountByFamily()
            }

            Enums.ReportType.SURNAME.toString() -> {
                loadCountBySurname()
            }

            Enums.ReportType.ADDRESS.toString() -> {
                loadCountByAddress()
            }

            Enums.ReportType.STATUS.toString() -> {
                loadCountByStatus()
            }

            Enums.ReportType.CAST.toString() -> {
                loadCountByCast()
            }

            Enums.ReportType.OUTSTATION.toString() -> {
                loadCountByStation()
            }

            Enums.ReportType.PROFESSION.toString() -> {
                loadCountByProfession()
            }

            Enums.ReportType.IMP_VOTER.toString() -> {
                loadCountByImpVoter()
            }
        }
    }

    private fun loadCountByFamily() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val completedCount = commonViewModel.getDB().voterDao()
                .getFamilyCount(
                    villageNo, boothNo, 1
                )
            val incompletedCount = commonViewModel.getDB().voterDao()
                .getFamilyCount(
                    villageNo, boothNo, 0
                )
            val unknownCount = commonViewModel.getDB().voterDao()
                .getFamilyCount(
                    villageNo, boothNo, 2
                )

            binding.rb1.text = "${getString(R.string.completed_family)}\n ($completedCount)"
            binding.rb2.text = "${getString(R.string.incompleted_family)}\n ($incompletedCount)"
            binding.rb3.text = "${getString(R.string.unknown_voter)}\n($unknownCount)"
        }
        CoroutineScope(Dispatchers.Main).launch {
            val cList = if (binding.etFirstName.text.toString().trim().isNotEmpty()
                || binding.etMiddleName.text.toString().trim().isNotEmpty()
                || binding.etLastName.text.toString().trim().isNotEmpty()
            ) commonViewModel.getDB().voterDao()
                .getCountByFamily(
                    villageNo, boothNo, binding.etFirstName.text.toString().trim(),
                    binding.etMiddleName.text.toString().trim(),
                    binding.etLastName.text.toString().trim(),
                    if (binding.rb3.isChecked) 2 else if (binding.rb1.isChecked) 1 else 0,
                    offset * 30
                )
            else
                commonViewModel.getDB().voterDao()
                    .getCountByFamily(
                        villageNo, boothNo,
                        if (binding.rb3.isChecked) 2 else if (binding.rb1.isChecked) 1 else 0,
                        offset * 30
                    )
            fillResultAndNotify(cList)
        }
    }

    private fun loadCountBySurname() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val searchString = if (binding.etSearch.text.toString().trim()
                    .isNotEmpty()
            ) "${binding.etSearch.text.toString().trim()}%" else ""
            val cList =
                commonViewModel.getDB().voterDao()
                    .getCountBYSurname(
                        villageNo, boothNo, searchString, offset * 30
                    )
            fillResultAndNotify(cList)
        }
    }

    private fun loadCountByProfession() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val cList =
                commonViewModel.getDB().voterDao()
                    .getCountByProfession(villageNo, boothNo, offset * 30)
            fillResultAndNotify(cList)
        }
    }

    private fun loadCountByImpVoter() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val searchString = if (binding.etSearch.text.toString().trim()
                    .isNotEmpty()
            ) "${binding.etSearch.text.toString().trim()}%" else ""
            val cList = commonViewModel.getDB().voterDao()
                .getCountByImpVoter(
                    villageNo, boothNo,
                    searchString, offset * 30
                )
            fillResultAndNotify(cList)
        }
    }

    private fun loadCountByStation() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val cList = commonViewModel.getDB().voterDao()
                .getCountByStation(villageNo, boothNo, offset * 30)
            fillResultAndNotify(cList)
        }
    }

    private fun loadCountByCast() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val cList =
                commonViewModel.getDB().voterDao()
                    .getCountByCast(villageNo, boothNo, offset * 30)
            fillResultAndNotify(cList)
        }

    }

    private fun loadCountByStatus() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val cList =
                commonViewModel.getDB().voterDao()
                    .getCountByStatus(villageNo, boothNo, offset * 30)
            fillResultAndNotify(cList)
        }
    }

    private fun loadCountByAddress() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val searchString = binding.etSearch.text.toString().trim()
            val cList =
                commonViewModel.getDB().voterDao()
                    .getCountBYAddress(
                        villageNo, boothNo, searchString, offset * 30
                    )
            fillResultAndNotify(cList)
        }
    }

    private fun initClickListener() {
        binding.rgFamilyCompleted.setOnCheckedChangeListener { _, _ ->
            offset = 0
            loadCountByFamily()
        }
        binding.etSearch.addTextChangedListener(textWatcher)
        binding.etFirstName.addTextChangedListener(textWatcher)
        binding.etMiddleName.addTextChangedListener(textWatcher)
        binding.etLastName.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            offset = 0
            search()
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillResultAndNotify(vList: List<CountBy>?) {
        if (offset == 0)
            countList.clear()
        if (!vList.isNullOrEmpty()) {
            countList.addAll(vList)
            offset += 1
        } else {
            offset = -1
        }
        if (loading) {
            binding.adapter!!.notifyItemInserted(countList.size)
        } else {
            binding.adapter!!.notifyDataSetChanged()
        }
        binding.llLoadMore.visibility = View.GONE
        loading = false

    }

    private fun initAdapter() {
        binding.adapter =
            CountListAdapter(countList).apply {
                countClickListener = object : CountListAdapter.CountClickListener {
                    override fun onItemClick(count: CountBy) {
                        if (reportType == Enums.ReportType.SURNAME.toString() && isFromSelect) {
                            setResult(RESULT_OK, Intent().apply {
                                putExtra(IntentConstants.SURNAME, count.getDisplayName())
                            })
                            finish()
                        } else if (reportType == Enums.ReportType.FAMILY.toString()) {
                            val intent =
                                Intent(this@CountDetailsActivity, FamilyActivity::class.java)
                            intent.putExtra(IntentConstants.VOTER_ID, count.id)
                            updateLauncher.launch(intent)

                        } else if (reportType == Enums.ReportType.IMP_VOTER.toString()) {
                            VoterListUnderImpVoterActivity.startActivityForResult(
                                this@CountDetailsActivity,
                                villageNo, boothNo,
                                count.cardNo, false, updateLauncher
                            )
                        } else {
                            ReportDetailsActivity.startActivityForResult(
                                this@CountDetailsActivity,
                                reportType, villageNo, boothNo, boothName,
                                count.getDisplayName(), updateLauncher
                            )
                        }
                    }
                }
            }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                binding.swipeRefreshLayout.isEnabled =
//                    (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) // 0 is for first item position
                if (dy > 0 && countList.isNotEmpty() && offset > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        search()
                    }
                }
            }
        })
    }

    val updateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                reloadList()
            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.excel_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.excel_menu) {
            CustomProgressDialog.showProgressDialog(this)
            CoroutineScope(Dispatchers.Main).launch {
                val searchString = if (binding.etSearch.text.toString().trim()
                        .isNotEmpty()
                ) "${binding.etSearch.text.toString().trim()}%" else ""
                var fileName = "Report"
                val countList = when (reportType) {
                    Enums.ReportType.FAMILY.toString() -> {
                        fileName = "Family_wise_report"
                        commonViewModel.getDB().voterDao().getCountByFamily(
                            villageNo, boothNo, searchString
                        )
                    }

                    Enums.ReportType.SURNAME.toString() -> {
                        fileName = "Surname_wise_report"
                        commonViewModel.getDB().voterDao()
                            .getCountBYSurname(
                                villageNo, boothNo, searchString
                            )

                    }

                    Enums.ReportType.ADDRESS.toString() -> {
                        fileName = "Address_wise_report"
                        commonViewModel.getDB().voterDao()
                            .getCountBYAddress(
                                villageNo, boothNo, searchString
                            )
                    }

                    Enums.ReportType.STATUS.toString() -> {
                        fileName = "Status_wise_report"
                        commonViewModel.getDB().voterDao()
                            .getCountByStatus(villageNo, boothNo)
                    }

                    Enums.ReportType.CAST.toString() -> {
                        fileName = "Cast_wise_report"
                        commonViewModel.getDB().voterDao()
                            .getCountByCast(villageNo, boothNo)
                    }

                    Enums.ReportType.OUTSTATION.toString() -> {
                        fileName = "Outstation_report"
                        commonViewModel.getDB().voterDao()
                            .getCountByStation(villageNo, boothNo)
                    }

                    Enums.ReportType.PROFESSION.toString() -> {
                        fileName = "Profession_wise_report"
                        commonViewModel.getDB().voterDao()
                            .getCountByProfession(villageNo, boothNo)
                    }

                    Enums.ReportType.IMP_VOTER.toString() -> {
                        fileName = "Influencer_wise_report"
                        commonViewModel.getDB().voterDao()
                            .getCountByImpVoter(villageNo, boothNo)
                    }

                    else -> {
                        ArrayList()
                    }
                }
                if (!countList.isNullOrEmpty()) {
                    val headerMessage =
                        "Village - $villageNo\nColony - $boothName"
                    prepareCountExcelFile(fileName, headerMessage, ArrayList(countList))
                } else {
                    CustomProgressDialog.dismissProgressDialog()
                    CommonUtils.showToast(
                        this@CountDetailsActivity, getString(R.string.no_records_to_export)
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}