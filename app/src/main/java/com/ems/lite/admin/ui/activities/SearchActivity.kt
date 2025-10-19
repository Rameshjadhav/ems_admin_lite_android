package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.SearchActivityBinding
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.interfaces.DialogClickListener
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.ui.adapters.VoterListAdapter
import com.ems.lite.admin.utils.AlertDialogManager
import com.ems.lite.admin.utils.CommonUtils
import com.ems.lite.admin.utils.IntentConstants
import com.ems.lite.admin.utils.IntentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: SearchActivityBinding
    private val commonViewModel: VoterViewModel by viewModels()
    private val voterList: ArrayList<Voter> = arrayListOf()
    private var selectedVillage: Village? = null
    private var selectedBooth: Booth? = null
    private var offset = 0
    private var loading = false
    private var cardNo: String? = null
    private var surname: String? = null
    private var houseNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.search_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.search_voter))
        initAdapter()
        initClick()
        init()
    }

    private fun init() {
        selectedVillage = Village().apply {
            id = 0
            villageNo = 0
            villageName = getString(R.string.all)
            villageNameEng = getString(R.string.all)
        }
        updateVillage()
        cardNo = intent.getStringExtra(IntentConstants.CARD_NO)
        surname = intent.getStringExtra(IntentConstants.SURNAME)
        if (!cardNo.isNullOrEmpty() || !surname.isNullOrEmpty()) { // Come for add voter under imp voter
            val villageNo = intent.getLongExtra(IntentConstants.VILLAGE_NO, 0)
            val boothNo = intent.getLongExtra(IntentConstants.BOOTH_NO, 0)
            if (villageNo != 0L) {
                CoroutineScope(Dispatchers.Main).launch {
                    val village = commonViewModel.getDB().villageDao().get(villageNo)
                    if (village != null) {
                        selectedVillage = village
                        updateVillage()
                    }
                }
            }
            if (boothNo != 0L) {
                CoroutineScope(Dispatchers.Main).launch {
                    val booth = commonViewModel.getDB().BoothDao().get(boothNo)
                    if (booth != null) {
                        selectedBooth = booth
                        updateBooth()
                    }
                }
            }
            if (!cardNo.isNullOrEmpty()) {
                binding.lblAddByVoterNo.visibility = View.VISIBLE
                binding.llAddVoter.visibility = View.VISIBLE
            }
            houseNo = intent.getStringExtra(IntentConstants.HOUSE_NO)
        }
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
            val list = when (binding.rgSearchBy.checkedRadioButtonId) {
                R.id.rb_voter_no -> {
                    commonViewModel.getDB().voterDao()
                        .searchVoterByVoterNo(
                            search, selectedVillage!!.villageNo,
                            selectedBooth!!.boothNo, offset * 20
                        )
                }

                R.id.rb_card_no -> {
                    commonViewModel.getDB().voterDao()
                        .searchVoterByCardNo(
                            search,
                            selectedVillage!!.villageNo, selectedBooth!!.boothNo, offset * 20
                        )
                }

                R.id.rb_house_no -> {
                    commonViewModel.getDB().voterDao()
                        .searchVoterByHouseNo(
                            search,
                            selectedVillage!!.villageNo, selectedBooth!!.boothNo, offset * 20
                        )
                }

                else -> {
                    commonViewModel.getDB().voterDao()
                        .searchVoter(
                            binding.etFirstName.text.toString().trim(),
                            binding.etMiddleName.text.toString().trim(),
                            binding.etLastName.text.toString().trim(),
                            selectedVillage!!.villageNo,
                            selectedBooth!!.boothNo,
                            offset * 20
                        )
                }
            }

            if (offset == 0)
                voterList.clear()
            if (!list.isNullOrEmpty()) {
                voterList.addAll(list)
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

        }
    }

    private fun initClick() {
        binding.onClickListener = this
        binding.rgSearchBy.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_voter_name -> {
                    binding.crNameSearch.visibility = View.VISIBLE
                    binding.etSearch.visibility = View.GONE
                    binding.etSearch.setText("")
                }

                R.id.rb_voter_no,
                R.id.rb_card_no,
                R.id.rb_house_no -> {
                    binding.crNameSearch.visibility = View.GONE
                    binding.etSearch.visibility = View.VISIBLE
                    binding.etFirstName.setText("")
                    binding.etMiddleName.setText("")
                    binding.etLastName.setText("")
                }
            }
        }
        binding.etSearch.addTextChangedListener(textWatcher)
        binding.etFirstName.addTextChangedListener(textWatcher)
        binding.etMiddleName.addTextChangedListener(textWatcher)
        binding.etLastName.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            offset = 0
            searchVoter()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_filter -> {
                if (binding.crFilter.visibility == View.GONE) {
                    binding.crFilter.visibility = View.VISIBLE
                    binding.ivArrow.setImageResource(R.drawable.up_arrow_black)
                } else {
                    binding.crFilter.visibility = View.GONE
                    binding.ivArrow.setImageResource(R.drawable.down_arrow_black)
                }
            }

            R.id.et_village -> {
                SelectVillageListActivity.startActivityForResult(
                    this, selectedVillage, villageLauncher
                )
            }

            R.id.et_booth -> {
                if (selectedVillage != null) {
                    SelectBoothListActivity.startActivityForResult(
                        this, selectedVillage!!.villageNo, selectedBooth, boothLauncher
                    )
                } else {
                    CommonUtils.showToast(this, getString(R.string.pls_select_ward_first))
                }
            }

            R.id.btn_add_voter -> {
                var from = 0
                var to = 0
                if (binding.etVoterFrom.text.toString().trim().isNotEmpty()) {
                    from = binding.etVoterFrom.text.toString().trim().toInt()
                }
                if (binding.etVoterTo.text.toString().trim().isNotEmpty()) {
                    to = binding.etVoterTo.text.toString().trim().toInt()
                }
                if (binding.etVoterFrom.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_from_voter_no))
                } else if (binding.etVoterTo.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_to_voter_no))
                } else if (from > to) {
                    CommonUtils.showToast(
                        this, getString(R.string.from_voter_must_be_less_than_to_voter)
                    )
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        commonViewModel.getDB().voterDao()
                            .updateVoterUnderImpVoter(
                                selectedVillage?.villageNo ?: 0L,
                                selectedBooth?.boothNo ?: 0L,
                                binding.etVoterFrom.text.toString().trim().toInt(),
                                binding.etVoterTo.text.toString().trim().toInt(),
                                cardNo
                            )
                        setResult(Activity.RESULT_OK)
                        CommonUtils.showToast(
                            this@SearchActivity, getString(R.string.voter_added_successfully)
                        )
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        binding.adapter = VoterListAdapter(voterList).apply {
            voterClickListener = object : VoterListAdapter.VoterClickListener {
                override fun onItemClick(voter: Voter) {
                    if (!cardNo.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            commonViewModel.getDB().voterDao()
                                .updateVoterUnderImpVoter(voter._id, cardNo)
                            setResult(Activity.RESULT_OK)
                            CommonUtils.showToast(
                                this@SearchActivity,
                                getString(R.string.voter_added_successfully)
                            )
                        }
                    } else if (!surname.isNullOrEmpty()) {
                        AlertDialogManager.showConfirmationDialog(
                            this@SearchActivity,
                            getString(R.string.app_name),
                            getString(R.string.are_you_sure_want_to_add_this_voter_in_this_family),
                            getString(R.string.yes),
                            button1Drawable = R.drawable.rc_theme_filled_c25,
                            button2Message = getString(R.string.no),
                            button2Drawable = R.drawable.rc_red_filled_c25,
                            dialogClickListener = object : DialogClickListener {
                                override fun onButton1Clicked() {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        commonViewModel.getDB().voterDao()
                                            .updateVoterUnderFamily(voter._id, houseNo)
                                        voter.houseNo = houseNo
                                        setResult(Activity.RESULT_OK, Intent().apply {
                                            putExtra(IntentConstants.VOTER, voter)
                                        })
                                        CommonUtils.showToast(
                                            this@SearchActivity,
                                            getString(R.string.voter_added_successfully)
                                        )
                                        finish()
                                    }
                                }

                                override fun onButton2Clicked() {}

                                override fun onCloseClicked() {}

                            }
                        )
                    } else {
                        val intent = Intent(this@SearchActivity, VoterDetailsActivity::class.java)
                        intent.putExtra(IntentConstants.ID, voter._id)
                        updateLauncher.launch(intent)
                    }
                }

                override fun onCallClick(mobileNo: String?) {
                    callNumber = mobileNo?.replace("/", "")
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
                        searchVoter()
                    }
                }
            }
        })
    }

    private fun updateVillage() {
        if (selectedVillage != null) {
            binding.etVillage.setText(selectedVillage!!.toString())
            updateVillageCount()
            if (selectedBooth == null || selectedBooth?.villageNo != selectedVillage?.villageNo) {
                selectedBooth = Booth().apply {
                    id = 0
                    boothNo = 0
                    boothName = getString(R.string.all)
                    boothNameEng = getString(R.string.all)
                }
            }
            updateBooth()
        }
    }

    private fun updateVillageCount() {
        CoroutineScope(Dispatchers.Main).launch {
            val count = commonViewModel.getDB().voterDao()
                .getVillageTotalCount(
                    selectedVillage?.villageNo ?: 0
                )
            binding.tvVillageCount.text = count.toString()
            binding.tvBoothCount.text = count.toString()
            setToolBarSubTitle(count.toString())
        }
    }

    private fun updateBooth() {
        if (selectedBooth != null) {
            binding.etBooth.setText(selectedBooth!!.getName())
            updateBoothCount()
        }
        offset = 0
        searchVoter()
    }

    private fun updateBoothCount() {
        CoroutineScope(Dispatchers.Main).launch {
            val count = commonViewModel.getDB().voterDao()
                .getBoothTotalCount(
                    selectedVillage?.villageNo ?: 0,
                    selectedBooth?.boothNo ?: 0
                )
            binding.tvBoothCount.text = count.toString()
            setToolBarSubTitle(count.toString())
        }
    }

    private val villageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedVillage = IntentUtils.getVillageFromIntent(result.data)
                updateVillage()
            }
        }

    private val boothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedBooth = IntentUtils.getBoothFromIntent(result.data)
                updateBooth()
            }
        }
    private val updateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                offset = 0
                searchVoter()
            }
        }
}