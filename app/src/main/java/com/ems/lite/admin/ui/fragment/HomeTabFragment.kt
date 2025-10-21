package com.ems.lite.admin.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.ems.lite.admin.BuildConfig
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.HomeTabFragmentBinding
import com.ems.lite.admin.di.viewmodel.BoothViewModel
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.interfaces.DialogClickListener
import com.ems.lite.admin.model.HomeOption
import com.ems.lite.admin.model.request.SaveVoterListRequest
import com.ems.lite.admin.model.request.VoterMasterListRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.ui.activities.*
import com.ems.lite.admin.ui.adapters.HomeGridAdapter
import com.ems.lite.admin.utils.AlertDialogManager
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.CustomProgressDialog
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeTabFragment : BaseFragment() {
    companion object {
        fun onNewInstance(): HomeTabFragment {
            return HomeTabFragment()
        }
    }

    private lateinit var binding: HomeTabFragmentBinding
    private val voterViewModel: VoterViewModel by viewModels()
    private val boothViewModel: BoothViewModel by viewModels()
    private val homeOptionList: ArrayList<HomeOption> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_tab_fragment, container, false)
        initAdapter()
        init()
        return binding.root
    }

    private fun init() {
        loadList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadList() {
        homeOptionList.clear()
        homeOptionList.add(HomeOption().apply {
            title = getString(R.string.search_voter)
            type = Enums.HomeOptionType.SEARCH_VOTER.toString()
            icon = R.drawable.search_voter
        })
        homeOptionList.add(HomeOption().apply {
            title = getString(R.string.report)
            type = Enums.HomeOptionType.REPORT.toString()
            icon = R.drawable.report
        })
        homeOptionList.add(HomeOption().apply {
            title = getString(R.string.import_data)
            type = Enums.HomeOptionType.IMPORT_DATA.toString()
            icon = R.drawable.import_1
        })
        homeOptionList.add(HomeOption().apply {
            title = getString(R.string.refresh_masters)
            type = Enums.HomeOptionType.REFRESH_MASTERS.toString()
            icon = R.drawable.refresh
        })
        homeOptionList.add(HomeOption().apply {
            title = getString(R.string.activate_users)
            type = Enums.HomeOptionType.ACTIVATE_USERS.toString()
            icon = R.drawable.election_day
        })
        homeOptionList.add(HomeOption().apply {
            title = getString(R.string.settings)
            type = Enums.HomeOptionType.SETTINGS.toString()
            icon = R.drawable.settings
        })

        binding.adapter?.notifyDataSetChanged()
    }

    private var isImporting = false
    var offset: Long = 0
    private fun getUpdatedVoterList() {
        if (CommonUtils.isNetworkAvailable(requireActivity())) {
            isImporting = true
            CustomProgressDialog.showProgressDialog(requireActivity())
            val user = Prefs.user
            voterViewModel.getUserVoterUpdatedMaster(
                offset, VoterMasterListRequest(
                    user?.userId, user?.villageNo,
                    user?.boothNo, user?.from, user?.to, user?.booths
                )
            ).observe(this) { response ->
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.list.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            voterViewModel.insertUpdatedVoter(response.list!!)
                        }
                    }
                    offset = response.nextOffset
                    if (offset > 0) {
                        getUpdatedVoterList()
                    } else {
                        CustomProgressDialog.dismissProgressDialog()
                        isImporting = false
                        AlertDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.import_data))
                            .setMessage(getString(R.string.imported_data_successfully))
                            .setPositiveButton(getString(R.string.done), null).show()
                    }
                } else {
                    CustomProgressDialog.dismissProgressDialog()
                    isImporting = false
                }
            }
        } else {
            CommonUtils.showErrorMessage(
                requireActivity(), getString(R.string.no_internet_connection)
            )
        }
    }

    private fun saveVoterList(list: List<Voter>) {
        if (CommonUtils.isNetworkAvailable(requireActivity())) {
            //  CustomProgressDialog.showProgressDialog(this)
            val action = "saveVoterList"
            val req = SaveVoterListRequest()
            val voterList: ArrayList<Voter> = arrayListOf()
            voterList.addAll(list)
            req.voterList = voterList
            voterViewModel.saveVoterlist(action, req).observe(this) { response ->
                //  CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle(getString(R.string.sync_data))
                        .setMessage(getString(R.string.sync_data_msg))
                        .setPositiveButton(getString(R.string.done), null).show()

                    voterList.forEach { it.updated = 0 }
                    voterViewModel.insertUpdatedVoter(voterList)
//                    finish()
                } else if (response?.error != null) {
                    CommonUtils.showToast(requireActivity(), response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(requireActivity(), getString(R.string.no_internet_connection))
        }
    }

    private fun getBoothList() {
        if (CommonUtils.isNetworkAvailable(requireActivity())) {
//            CustomProgressDialog.showProgressDialog(this)
            boothViewModel.getBoothMasterList().observe(this) { response ->
//                CustomProgressDialog.dismissProgressDialog()
                if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                    if (!response.boothList.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            boothViewModel.insertBooth(response.boothList!!)
                            Prefs.isBoothSync = true
                            requireActivity().finish()
                        }
                    }
                } else if (response?.error != null) {
                    CommonUtils.showToast(requireActivity(), response.error!!.message)
                }
            }
        } else {
            CommonUtils.showToast(requireActivity(), getString(R.string.no_internet_connection))
        }
    }

    private fun initAdapter() {
        binding.adapter = HomeGridAdapter(homeOptionList).apply {
            homeOptionClickListener = object : HomeGridAdapter.HomeOptionClickListener {
                override fun onItemClick(homeOption: HomeOption) {
                    when (homeOption.type) {
                        Enums.HomeOptionType.SEARCH_VOTER.toString() -> {
                            startActivity(Intent(requireActivity(), SearchActivity::class.java))
                        }

                        Enums.HomeOptionType.REPORT.toString() -> {
                            startActivity(Intent(requireActivity(), ReportActivity::class.java))
                        }

                        Enums.HomeOptionType.IMPORT_DATA.toString() -> {
                            if (!isImporting) {
                                offset = 0
                                getUpdatedVoterList()
                            }
                        }

                        Enums.HomeOptionType.REFRESH_MASTERS.toString() -> {
                            Prefs.isVillageSync = false
                            Prefs.isBoothSync = false
                            Prefs.isCastSync = false
                            Prefs.isProfessionSync = false
                            Prefs.isDesignationSync = false
                            Prefs.isReligionSync = false
                            Prefs.isCastSync = false
                            (requireActivity() as HomeActivity).syncTables()
                        }

                        Enums.HomeOptionType.UPDATE_BOOTH.toString() -> {
                            getBoothList()
                        }

                        Enums.HomeOptionType.ACTIVATE_USERS.toString() -> {
                            val intent = Intent(requireActivity(), UserListActivity::class.java)
                            startActivity(intent)
                        }

                        Enums.HomeOptionType.SETTINGS.toString() -> {
                            SettingActivity.startActivity(requireActivity())
                        }
                    }
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.language_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val menuItem = menu.findItem(R.id.action_language)
        menuItem.icon = ContextCompat.getDrawable(
            requireActivity(),
            if (Prefs.lang != Enums.Language.en.toString()) R.drawable.lang_marathi else R.drawable.lang_english
        )
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_language -> {
                if (Prefs.lang != Enums.Language.en.toString()) {
                    Prefs.lang = Enums.Language.en.toString()
                } else {
                    Prefs.lang = BuildConfig.OTHER_LANGUAGE
                }
                requireActivity().startActivity(requireActivity().intent)
                requireActivity().finish()
                requireActivity().overridePendingTransition(0, 0)
            }

            R.id.action_logout -> {
                AlertDialogManager.showConfirmationDialog(
                    requireActivity(),
                    getString(R.string.app_name),
                    getString(R.string.are_you_sure_want_to_logout),
                    getString(R.string.yes),
                    button1Drawable = R.drawable.rc_theme_filled_c25,
                    button2Message = getString(R.string.no),
                    button2Drawable = R.drawable.rc_red_filled_c25,
                    dialogClickListener = object : DialogClickListener {
                        override fun onButton1Clicked() {
                            CoroutineScope(Dispatchers.Main).launch {
                                voterViewModel.getDB().CastDao().clear()
                                voterViewModel.getDB().villageDao().clear()
                                voterViewModel.getDB().BoothDao().clear()
                                voterViewModel.getDB().designationDao().clear()
                                voterViewModel.getDB().religionDao().clear()
                                voterViewModel.getDB().voterDao().clear()
                                Prefs.logout()
                                LauncherActivity.startActivity(requireActivity())
                                requireActivity().finish()
                            }
                        }

                        override fun onButton2Clicked() {
                        }

                        override fun onCloseClicked() {
                        }

                    }
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}