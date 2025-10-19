package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ActivityUserListBinding
import com.ems.lite.admin.model.User
import com.ems.lite.admin.model.request.SearchUserListRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.ui.adapters.UserListAdapter
import com.ems.lite.admin.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.Semaphore

@AndroidEntryPoint
class UserListActivity : BaseActivity() {
    private lateinit var binding: ActivityUserListBinding
    private val userArrayList: ArrayList<User> = arrayListOf()
    private val semaphore = Semaphore(1)
    private var offset: Long = 0
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.users))
        initClick()
        initObserver()
        initAdapter()
        getUserList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        lifecycleScope.launch {
            voterViewModel.userListState.collect {
                when (it.status) {
                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.llLoadMore.visibility = View.GONE
                        if (it.code == ResponseStatus.STATUS_CODE_SUCCESS && it.data != null) {
                            if (offset == 0L)
                                userArrayList.clear()
                            if (!it.data.list.isNullOrEmpty()) {
                                userArrayList.addAll(it.data.list!!)
                            }
                            if (loading) {
                                binding.adapter!!.notifyItemInserted(userArrayList.size)
                            } else {
                                binding.adapter!!.notifyDataSetChanged()
                            }
                            offset = it.data.nextOffset
                        }
                        setNoResult()
                        loading = false
                        semaphore.release()
                        if (it.data?.keyword != null && !it.data.keyword!!.equals(
                                binding.etSearch.text.toString().trim(), true
                            )
                        ) {
                            reloadList()
                        }
                    }

                    Status.ERROR -> {
                        setNoResult()
                        binding.llLoadMore.visibility = View.GONE
                        loading = false
                    }
                }
            }
        }
    }

    private fun reloadList() {
        offset = 0
        getUserList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getUserList() {
        if (semaphore.tryAcquire()) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (loading) {
                    binding.llLoadMore.visibility = View.VISIBLE
                } else if (!binding.swipeRefreshLayout.isRefreshing) {
                    binding.viewFlipper.displayedChild = 0
                }
                val keyword = binding.etSearch.text.toString()
                voterViewModel.getUserList(SearchUserListRequest(keyword, offset))
            } else {
                semaphore.release()
                if (!binding.swipeRefreshLayout.isRefreshing) {
                    binding.viewFlipper.displayedChild = 3
                } else {
                    CommonUtils.showToast(this, getString(R.string.no_internet_connection))
                }
                binding.swipeRefreshLayout.isRefreshing = false
                loading = false
            }
        }
    }

    private fun setNoResult() {
        if (userArrayList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
            binding.llNoResult.tvNoResult.text = getString(R.string.no_user_found)
        }
    }

    private fun initClick() {
        initNoInternet(binding.noInternetLayout) {
            getUserList()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            reloadList()
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                reloadList()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }

    private fun initAdapter() {
        binding.adapter = UserListAdapter(userArrayList).apply {
            userClickListener = object : UserListAdapter.UserClickListener {
                override fun onItemClick(user: User) {
                    UserDetailActivity.startActivityForResult(
                        this@UserListActivity, user, userDetailLauncher
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
                if (dy > 0 && userArrayList.isNotEmpty() && offset > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        getUserList()
                    }
                }
            }
        })
    }


    val userDetailLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            getUserList()
        }
    }
}