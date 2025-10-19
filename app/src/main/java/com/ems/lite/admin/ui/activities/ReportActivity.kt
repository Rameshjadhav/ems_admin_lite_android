package com.ems.lite.admin.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityReportBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.report.model.ReportFilter
import com.ems.lite.admin.report.model.ReportOption
import com.ems.lite.admin.report.ui.fragment.ReportFilterDialogFragment
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.ems.lite.admin.report.ui.adapters.ReportOptionAdapter

@AndroidEntryPoint
class ReportActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityReportBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val reportOptionList: ArrayList<ReportOption> = arrayListOf()
    private var reportFilter: ReportFilter = ReportFilter()

    private var totalVoterCount: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.report))
        initClickListener()
        initAdapter()
        init()
    }

    private fun init() {
        val cardNo = Prefs.user?.cardNo
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.election_day_report
            title = getString(R.string.election_day)
            type = Enums.ReportType.ELECTION_DAY.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.family_wise
            title = getString(R.string.list_by_family)
            type = Enums.ReportType.FAMILY.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.alphabetical
            title = getString(R.string.list_by_alphabet)
            type = Enums.ReportType.ALPHABETICAL.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.voter_list
            title = getString(R.string.list_by_voter_no)
            type = Enums.ReportType.VOTER_NO.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.surname_list
            title = getString(R.string.list_by_surname)
            type = Enums.ReportType.SURNAME.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.address
            title = getString(R.string.list_by_address)
            type = Enums.ReportType.ADDRESS.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.mobile_report
            title = getString(R.string.list_by_mobile_no)
            type = Enums.ReportType.MOBILE.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.influencer
            title = getString(R.string.influencer)
            type = Enums.ReportType.INFLUENCER.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.castwise_list
            title = getString(R.string.list_by_cast)
            type = Enums.ReportType.CAST.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.cast_survey
            title = getString(R.string.caste_survey)
            type = Enums.ReportType.CAST_SURVEY.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.voter_status
            title = getString(R.string.list_by_status)
            type = Enums.ReportType.STATUS.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.age_list
            title = getString(R.string.list_by_age)
            type = Enums.ReportType.AGE.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.profession
            title = getString(R.string.list_by_profession)
            type = Enums.ReportType.PROFESSION.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.outstation_voter
            title = getString(R.string.list_by_out_of_villege)
            type = Enums.ReportType.OUTSTATION.toString()
        })

        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.duplicate
            title = getString(R.string.list_by_duplication)
            type = Enums.ReportType.DUPLICATE.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.dead_voter
            title = getString(R.string.dead_voter_list)
            type = Enums.ReportType.DEAD.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.relative_list
            title = getString(R.string.relative_list)
            type = Enums.ReportType.RELATIVE.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.karykarta
            title = getString(R.string.karykarta)
            type = Enums.ReportType.IMP_VOTER.toString()
        })
        reportOptionList.add(ReportOption().apply {
            icon = R.drawable.booth_report
            title = getString(R.string.booth_committee)
            type = Enums.ReportType.BOOTH_COMMITTEE.toString()
        })
        binding.adapter?.notifyDataSetChanged()
        CoroutineScope(Dispatchers.Main).launch {
            val list = commonViewModel.getDB().villageDao().getAll()
            if (!list.isNullOrEmpty()) {
                reportFilter.village = list[0]
                updateVillage()
                updateVillageCount()
            }
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_village -> {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(ReportFilterDialogFragment.TAG)
                val reportFilterDialogFragment: ReportFilterDialogFragment =
                    ReportFilterDialogFragment.onNewInstance(
                        reportFilter, true,
                        object : ReportFilterDialogFragment.FilterListener {
                            override fun onFilterApply(reportFilter: ReportFilter) {
                                updateVillageCount()
                                updateBoothCount()
                            }

                            override fun onResetFilter() {
                                reportFilter.village = null
                                reportFilter.booth = null
                            }
                        })
                reportFilterDialogFragment.show(fragmentTransaction, ReportFilterDialogFragment.TAG)
            }
        }
    }

    private fun initAdapter() {
        binding.adapter = ReportOptionAdapter(reportOptionList).apply {
            reportOptionClickListener = object : ReportOptionAdapter.ReportOptionClickListener {
                override fun onItemClick(reportOption: ReportOption) {
                    when (reportOption.type) {
                        Enums.ReportType.ELECTION_DAY.toString() -> {
                            ElectionDayReportActivity.startActivity(
                                this@ReportActivity, reportFilter.village, reportFilter.booth
                            )
                        }

                        Enums.ReportType.FAMILY.toString(), Enums.ReportType.SURNAME.toString(), Enums.ReportType.ADDRESS.toString(),
                        Enums.ReportType.CAST.toString(), Enums.ReportType.STATUS.toString(),
                        Enums.ReportType.PROFESSION.toString(), Enums.ReportType.OUTSTATION.toString(),
                        Enums.ReportType.IMP_VOTER.toString() -> {
                            goToCountDetailScreen(reportOption.type)
                        }

                        Enums.ReportType.ALPHABETICAL.toString(), Enums.ReportType.MOBILE.toString(),
                        Enums.ReportType.VOTER_NO.toString(), Enums.ReportType.CAST_SURVEY.toString(),
                        Enums.ReportType.DUPLICATE.toString(), Enums.ReportType.DEAD.toString() -> {
                            goToReportDetailScreen(reportOption.type)
                        }

                        Enums.ReportType.INFLUENCER.toString() -> {
                            InfluencerListActivity.startActivityForResult(
                                this@ReportActivity,
                                reportFilter.village?.villageNo, reportFilter.booth?.boothNo,
                                resultLauncher
                            )
                        }

                        Enums.ReportType.AGE.toString() -> {
                            AgeWiseVoterListActivity.startActivityForResult(
                                this@ReportActivity,
                                reportFilter.village?.villageNo, reportFilter.booth?.boothNo,
                                reportFilter.booth?.getName(),
                                resultLauncher
                            )
                        }

                        Enums.ReportType.RELATIVE.toString() -> {
                            RelativeCountListActivity.startActivityForResult(
                                this@ReportActivity,
                                reportFilter.village?.villageNo, reportFilter.booth?.boothNo,
                                reportFilter.booth?.getName(),
                                resultLauncher
                            )
                        }

                        Enums.ReportType.BOOTH_COMMITTEE.toString() -> {
                            goToCommitteeListScreen()
                        }

                        Enums.ReportType.MOBILE_CAST_COUNT.toString() -> {
                            MobileCasteReportActivity.startActivity(
                                this@ReportActivity, reportFilter.village?.villageNo,
                                reportFilter.booth?.boothNo
                            )
                        }
                    }
                }

            }
        }
    }

    private fun goToCountDetailScreen(reportType: String) {
        CountDetailsActivity.startActivityForResult(
            this@ReportActivity,
            reportType,
            reportFilter.village?.villageNo,
            reportFilter.booth?.boothNo,
            reportFilter.booth?.boothName,
            false,
            resultLauncher
        )
    }

    private fun goToReportDetailScreen(from: String) {
        ReportDetailsActivity.startActivityForResult(
            this,
            from,
            reportFilter.village?.villageNo,
            reportFilter.booth?.boothNo,
            reportFilter.booth?.boothName,
            null,
            resultLauncher
        )
    }

    private fun goToCommitteeListScreen() {
        CommitteeListActivity.startActivityForResult(
            this, reportFilter.village?.villageNo, reportFilter.booth?.boothNo, resultLauncher
        )
    }


    private fun updateVillage() {
        binding.etVillage.setText(if (reportFilter.village != null) reportFilter.village!!.toString() else "")
    }

    private fun updateVillageCount() {
        if (reportFilter.village != null)
            CoroutineScope(Dispatchers.Main).launch {
                val count = commonViewModel.getDB().voterDao()
                    .getVillageTotalCount(
                        reportFilter.village?.villageNo ?: 0
                    )
                setToolBarSubTitle(count.toString())
            }
    }

    private fun updateBoothCount() {
        if (reportFilter.booth != null)
            CoroutineScope(Dispatchers.Main).launch {
                val count = commonViewModel.getDB().voterDao()
                    .getBoothTotalCount(
                        reportFilter.village?.villageNo ?: 0, reportFilter.booth?.boothNo ?: 0
                    )
                setToolBarSubTitle(count.toString())
            }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        }

}