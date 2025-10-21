package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityReportDetailsBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.table.Cast
import com.ems.lite.admin.model.table.CountBy
import com.ems.lite.admin.model.table.Profession
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.ui.adapters.VoterListAdapter
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.IntentConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ReportDetailsActivity : BaseActivity() {
    companion object {
        fun startActivityForResult(
            activity: Activity, reportType: String?,
            villageNo: Long?, boothNo: Long?,
            boothName: String?, countBy: CountBy?,
            launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, ReportDetailsActivity::class.java).apply {
                putExtra(IntentConstants.VALUE, reportType)
                putExtra(IntentConstants.VILLAGE_NO, villageNo)
                putExtra(IntentConstants.BOOTH_NO, boothNo)
                putExtra(IntentConstants.BOOTH_NAME, boothName)
                putExtra(IntentConstants.TYPE, countBy)
            }.run {
                launcher.launch(this)
            }
        }
    }

    private lateinit var binding: ActivityReportDetailsBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val voterList: ArrayList<Voter> = arrayListOf()
    private val castList: ArrayList<Cast> = arrayListOf()
    private var cast: Cast? = null
    private var profession: Profession? = null
    private var professionId = ""
    private var castId = ""
    private var reportType: String? = null
    private var villageNo: Long = 0
    private var boothNo: Long = 0
    private var countBy: CountBy? = null
    private var boothName: String? = null
    private var offset = 0
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        initClickListener()
        initAdapter()
        initCastSpinner()
        init()
    }

    private fun init() {
        villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
        boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
        boothName = intent.getStringExtra(IntentConstants.BOOTH_NAME)
        reportType = intent.getStringExtra(IntentConstants.VALUE)
        countBy = intent.getParcelableExtra(IntentConstants.TYPE)
        binding.llUpdate.visibility = View.GONE
        binding.llCount.visibility = View.GONE
        when (reportType) {
            Enums.ReportType.FAMILY.toString() -> {
                setToolBarTitle(getString(R.string.list_by_family))
                loadVoterListOrder()
            }

            Enums.ReportType.ALPHABETICAL.toString() -> {
                setToolBarTitle(getString(R.string.list_by_alphabet))
                loadVoterListOrder()
            }

            Enums.ReportType.SURNAME.toString() -> {
                setToolBarTitle(getString(R.string.list_by_surname))
                binding.llUpdate.visibility = View.VISIBLE
                binding.llCount.visibility = View.VISIBLE
                binding.etSearch.visibility = View.VISIBLE
                binding.rb1.text = getString(R.string.cast_applied)
                binding.rb2.text = getString(R.string.cast_not_applied)
                loadVoterListBySurname()
            }

            Enums.ReportType.VOTER_NO.toString() -> {
                setToolBarTitle(getString(R.string.list_by_voter_no))
                loadVoterListVno()
            }

            Enums.ReportType.ADDRESS.toString() -> {
                setToolBarTitle(getString(R.string.list_by_address))
                loadVoterListByAddress()
            }

            Enums.ReportType.STATUS.toString() -> {
                setToolBarTitle(getString(R.string.list_by_status))
                binding.etSearch.visibility = View.VISIBLE
                loadVoterListByStatus()
            }

            Enums.ReportType.CAST.toString() -> {
                setToolBarTitle(getString(R.string.list_by_cast))
                loadVoterListByCast()
            }

            Enums.ReportType.CAST_SURVEY.toString() -> {
                setToolBarTitle(getString(R.string.caste_survey))
                binding.llCount.visibility = View.VISIBLE
                binding.rb1.text = getString(R.string.cast_applied)
                binding.rb2.text = getString(R.string.cast_not_applied)
                loadVoterListByCastSurvey()
            }

            Enums.ReportType.OUTSTATION.toString() -> {
                setToolBarTitle(getString(R.string.list_by_out_of_villege))
                loadVoterListByStation()
            }

            Enums.ReportType.MOBILE.toString() -> {
                setToolBarTitle(getString(R.string.list_by_mobile_no))
                binding.llCount.visibility = View.VISIBLE
                binding.rb1.text = getString(R.string.with_mobile)
                binding.rb2.text = getString(R.string.without_mobile)
                loadVoterListByMobile()
            }

            Enums.ReportType.PROFESSION.toString() -> {
                setToolBarTitle(getString(R.string.list_by_profession))
                loadVoterListByProfession()
            }

            Enums.ReportType.DUPLICATE.toString() -> {
                setToolBarTitle(getString(R.string.list_by_duplication))
                loadVoterListByDuplication()
            }

            Enums.ReportType.DEAD.toString() -> {
                setToolBarTitle(getString(R.string.dead_voter_list))
                loadVoterListByDead()
            }

            "MOBILE_CASTE_COUNT" -> {
                setToolBarTitle(getString(R.string.mobile_caste_count))
                loadVoterListByMobileCaste()
            }
        }
    }

    private fun loadVoterListByProfession() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            profession =
                commonViewModel.getDB().ProfessionDao().getProfessionId(countBy?.getDisplayName()!!)
            professionId = profession?.professionNo.toString()
            val vList = commonViewModel.getDB().voterDao()
                .getVoterByProfession(
                    villageNo, boothNo, professionId, offset * 30
                )
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByDuplication() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val count = commonViewModel.getDB().voterDao()
                .getCountByDuplication(villageNo, boothNo)
            setToolBarSubTitle((count / 2).toString())
            val vList =
                commonViewModel.getDB().voterDao()
                    .getByDuplication(villageNo, boothNo, offset * 30)
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByDead() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val vList =
                commonViewModel.getDB().voterDao()
                    .getDeadVoterList(villageNo, boothNo, offset * 30)
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByMobileCaste() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val vList =
                commonViewModel.getDB().voterDao()
                    .getByMobileCaste(villageNo, boothNo, offset * 30)
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByMobile() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            if (offset == 0) {
                val count =
                    if (binding.rb1.isChecked) {
                        commonViewModel.getDB().voterDao()
                            .getAllMobileCount(villageNo, boothNo)
                    } else {
                        commonViewModel.getDB().voterDao()
                            .getAllMobileEmptyCount(villageNo, boothNo)
                    }
                binding.tvReportCount.text = count.toString()
            }
            val vList =
                if (binding.rb1.isChecked) {
                    commonViewModel.getDB().voterDao()
                        .getAllMobile(villageNo, boothNo, offset * 30)
                } else {
                    commonViewModel.getDB().voterDao()
                        .getAllMobileEmpty(villageNo, boothNo, offset * 30)
                }
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByStation() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val vList =
                commonViewModel.getDB().voterDao()
                    .getVoterByStation(
                        villageNo, boothNo, countBy?.getDisplayName()!!, offset * 30
                    )
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByCast() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            cast = commonViewModel.getDB().CastDao().getCastId(countBy?.getDisplayName()!!)
            castId = cast?.castNo.toString()
            val vList =
                commonViewModel.getDB().voterDao()
                    .getVoterByCast(villageNo, boothNo, castId, offset * 30)
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByCastSurvey() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {

            if (offset == 0) {
                val count =
                    if (binding.rb1.isChecked) {
                        commonViewModel.getDB().voterDao()
                            .getVoterByCastAppliedCount(
                                villageNo, boothNo
                            )
                    } else {
                        commonViewModel.getDB().voterDao()
                            .getVoterByCastNotAppliedCount(
                                villageNo, boothNo
                            )
                    }
                binding.tvReportCount.text = count.toString()
            }
            val vList =
                if (binding.rb1.isChecked) {
                    commonViewModel.getDB().voterDao()
                    commonViewModel.getDB().voterDao()
                        .getVoterByCastApplied(
                            villageNo, boothNo, offset * 30
                        )
                } else {
                    commonViewModel.getDB().voterDao()
                    commonViewModel.getDB().voterDao()
                        .getVoterByCastNotApplied(
                            villageNo, boothNo, offset * 30
                        )
                }

            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByStatus() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        val searchingName = binding.etSearch.text.toString().trim()
        CoroutineScope(Dispatchers.Main).launch {
            val vList =
                commonViewModel.getDB().voterDao()
                    .getVoterByStatus(
                        villageNo, boothNo, searchingName, countBy?.getDisplayName()!!, offset * 30
                    )
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListByAddress() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val vList =
                commonViewModel.getDB().voterDao()
                    .getVoterByAddress(
                        villageNo, boothNo, countBy?.getDisplayName()!!, offset * 30
                    )
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListBySurname() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            if (offset == 0) {
                val count =
                    commonViewModel.getDB().voterDao()
                        .getVoterCountBySurname(
                            villageNo, boothNo, countBy?.nameEng!!, binding.rb1.isChecked
                        )
                binding.tvReportCount.text = count.toString()
            }
            val vList =
                commonViewModel.getDB().voterDao()
                    .getVoterListBySurname(
                        villageNo, boothNo,
                        binding.etSearch.text.toString().trim(),
                        countBy?.nameEng!!, binding.rb1.isChecked, offset * 30
                    )
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListVno() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val vList = commonViewModel.getDB().voterDao()
                .getAllOrderVno(villageNo, boothNo, offset * 30)
            fillResultAndNotify(vList)
        }
    }

    private fun loadVoterListOrder() {
        if (offset != 0 && loading) {
            binding.llLoadMore.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val vList = commonViewModel.getDB().voterDao()
                .getAllOrder(villageNo, boothNo, offset * 30)
            fillResultAndNotify(vList)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillResultAndNotify(vList: List<Voter>?) {
        if (offset == 0)
            voterList.clear()
        if (!vList.isNullOrEmpty()) {
            voterList.addAll(vList)
            offset += 1
        } else {
            offset = -1
        }
        if (loading) {
            binding.adapter!!.notifyItemInserted(voterList.size)
        } else {
            binding.adapter!!.notifyDataSetChanged()
        }
        binding.llLoadMore.visibility = View.GONE
        loading = false
        binding.viewFlipper.displayedChild = if (voterList.isNotEmpty()) 0 else 1
    }

    private fun initClickListener() {
        binding.rgVotedNonVoted.setOnCheckedChangeListener { _, _ ->
            when (reportType) {
                Enums.ReportType.SURNAME.toString() -> {
                    offset = 0
                    loadVoterListBySurname()
                }

                Enums.ReportType.MOBILE.toString() -> {
                    offset = 0
                    loadVoterListByMobile()
                }

                Enums.ReportType.CAST_SURVEY.toString() -> {
                    offset = 0
                    loadVoterListByCastSurvey()
                }
            }
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                offset = 0
                search()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.onClickListener = View.OnClickListener {
            when (it.id) {
                R.id.btn_update -> {
                    val cast = castList[binding.spCast.selectedItemPosition]
                    if (cast.castNo != 0L) {
                        CustomProgressDialog.showProgressDialog(this)
                        CoroutineScope(Dispatchers.Main).launch {
                            val count =
                                commonViewModel.getDB().voterDao()
                                    .updateCastBySurnameWard(
                                        countBy?.nameEng!!,
                                        villageNo,
                                        boothNo,
                                        cast.castNo
                                    )
                            Handler(Looper.getMainLooper()).postDelayed({
                                if (count > 0) {
                                    CommonUtils.showToast(
                                        this@ReportDetailsActivity,
                                        getString(R.string.changes_applied_successfully)
                                    )
                                }
                                offset = 0
                                loadVoterListBySurname()
                                CustomProgressDialog.dismissProgressDialog()
                            }, 1000)
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        binding.adapter = VoterListAdapter(voterList).apply {
            voterClickListener = object : VoterListAdapter.VoterClickListener {
                override fun onItemClick(voter: Voter) {
                    val intent =
                        Intent(this@ReportDetailsActivity, VoterDetailsActivity::class.java)

                    intent.putExtra(IntentConstants.ID, voter._id)
                    updateLauncher.launch(intent)
                }

                override fun onCallClick(mobileNo: String?) {
                    callNumber = mobileNo
                    makePhoneCall()
                }
            }
        }
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
                        search()
                    }
                }
            }
        })
    }

    private fun search() {
        when (reportType) {
            Enums.ReportType.FAMILY.toString() -> {
                loadVoterListOrder()
            }

            Enums.ReportType.ALPHABETICAL.toString() -> {
                loadVoterListOrder()
            }

            Enums.ReportType.SURNAME.toString() -> {
                loadVoterListBySurname()
            }

            Enums.ReportType.VOTER_NO.toString() -> {
                loadVoterListVno()
            }

            Enums.ReportType.ADDRESS.toString() -> {
                loadVoterListByAddress()
            }

            Enums.ReportType.STATUS.toString() -> {
                loadVoterListByStatus()
            }

            Enums.ReportType.CAST.toString() -> {
                loadVoterListByCast()
            }

            Enums.ReportType.CAST_SURVEY.toString() -> {
                loadVoterListByCastSurvey()
            }

            Enums.ReportType.OUTSTATION.toString() -> {
                loadVoterListByStation()
            }

            Enums.ReportType.MOBILE.toString() -> {
                loadVoterListByMobile()
            }

            Enums.ReportType.PROFESSION.toString() -> {
                loadVoterListByProfession()
            }

            Enums.ReportType.DUPLICATE.toString() -> {
                loadVoterListByDuplication()
            }

            Enums.ReportType.DEAD.toString() -> {
                loadVoterListByDead()
            }

            "MOBILE_CASTE_COUNT" -> {
                loadVoterListByMobileCaste()
            }
        }
    }

    private fun initCastSpinner() {
        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, castList)
        binding.spCast.adapter = arrayAdapter
        CoroutineScope(Dispatchers.Main).launch {
            val list = commonViewModel.getDB().CastDao().getAll()
            if (!list.isNullOrEmpty()) {
                castList.clear()
                castList.addAll(list)
                arrayAdapter.notifyDataSetChanged()
            }
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
                var fileName = "Report"
                val voterList = when (reportType) {
                    Enums.ReportType.FAMILY.toString() -> {
                        fileName = "Family_wise_report"
                        commonViewModel.getDB().voterDao()
                            .getAllOrder(villageNo, boothNo)
                    }

                    Enums.ReportType.ALPHABETICAL.toString() -> {
                        fileName = "Alphabetical_report"
                        commonViewModel.getDB().voterDao()
                            .getAllOrder(villageNo, boothNo)
                    }

                    Enums.ReportType.SURNAME.toString() -> {
                        fileName = "Surname_report"
                        commonViewModel.getDB().voterDao()
                            .getVoterListBySurname(
                                villageNo, boothNo, countBy?.nameEng!!, binding.rb1.isChecked
                            )
                    }

                    Enums.ReportType.VOTER_NO.toString() -> {
                        fileName = "Voter_no_report"
                        commonViewModel.getDB().voterDao()
                            .getAllOrderVno(villageNo, boothNo)
                    }

                    Enums.ReportType.ADDRESS.toString() -> {
                        fileName = "Address_report"
                        commonViewModel.getDB().voterDao()
                            .getVoterByAddress(
                                villageNo, boothNo, countBy?.getDisplayName()!!
                            )
                    }

                    Enums.ReportType.STATUS.toString() -> {
                        fileName = "Voter_Status_report"
                        commonViewModel.getDB().voterDao()
                            .getVoterByStatus(villageNo, boothNo, countBy?.getDisplayName()!!)
                    }

                    Enums.ReportType.CAST.toString() -> {
                        fileName = "Caste_report"
                        commonViewModel.getDB().voterDao()
                            .getVoterByCast(villageNo, boothNo, castId)
                    }

                    Enums.ReportType.CAST_SURVEY.toString() -> {
                        fileName = "Cast_Survey_report"
                        if (binding.rb1.isChecked) {
                            commonViewModel.getDB().voterDao()
                                .getVoterByCastApplied(
                                    villageNo, boothNo
                                )
                        } else {
                            commonViewModel.getDB().voterDao()
                                .getVoterByCastNotApplied(
                                    villageNo, boothNo
                                )
                        }

                    }

                    Enums.ReportType.OUTSTATION.toString() -> {
                        fileName = "OutStation_report"
                        commonViewModel.getDB().voterDao()
                            .getVoterByStation(
                                villageNo, boothNo, countBy?.getDisplayName()!!
                            )
                    }

                    Enums.ReportType.MOBILE.toString() -> {
                        fileName = "Mobile_report"
                        if (binding.rb1.isChecked) {
                            commonViewModel.getDB().voterDao()
                                .getAllMobile(villageNo, boothNo)
                        } else {
                            commonViewModel.getDB().voterDao()
                                .getAllMobileEmpty(villageNo, boothNo)
                        }
                    }

                    Enums.ReportType.PROFESSION.toString() -> {
                        fileName = "Profession_report"
                        commonViewModel.getDB().voterDao()
                            .getVoterByProfession(
                                villageNo, boothNo, professionId
                            )
                    }

                    Enums.ReportType.DUPLICATE.toString() -> {
                        fileName = "Duplicate_report"
                        commonViewModel.getDB().voterDao()
                            .getByDuplication(villageNo, boothNo)
                    }

                    "MOBILE_CASTE_COUNT" -> {
                        fileName = "Mobile_caste_report"
                        commonViewModel.getDB().voterDao()
                            .getByMobileCaste(villageNo, boothNo)
                    }

                    else -> {
                        ArrayList()
                    }
                }
                if (!voterList.isNullOrEmpty()) {
                    val headerMessage =
                        "Village - $villageNo\nBooth - $boothNo"
                    prepareExcelFile(fileName, headerMessage, ArrayList(voterList))
                } else {
                    CustomProgressDialog.dismissProgressDialog()
                    CommonUtils.showToast(
                        this@ReportDetailsActivity, getString(R.string.no_records_to_export)
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    val updateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
}