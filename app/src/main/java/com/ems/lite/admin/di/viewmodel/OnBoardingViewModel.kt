package com.ems.lite.admin.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ems.lite.admin.R
import com.ems.lite.admin.di.ResourcesProvider
import com.ems.lite.admin.di.repository.OnBoardingRepository
import com.ems.lite.admin.model.request.LoginRequest
import com.ems.lite.admin.model.request.UserRequest
import com.ems.lite.admin.model.response.LoginResponse
import com.ems.lite.admin.network.ApiResponseState
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val onBoardingRepository: OnBoardingRepository
) : ViewModel() {
    val loginState = MutableStateFlow(
        ApiResponseState(Status.LOADING, LoginResponse())
    )
    val userState =
        MutableStateFlow(ApiResponseState(Status.LOADING, LoginResponse(), "", 0))

    fun login(request: LoginRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                loginState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection), 100
                )
            }

            else -> {
                loginState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.login(request).catch {
                        loginState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        loginState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getUser(request: UserRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                userState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                userState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.getMe(request).catch {
                        userState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        userState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }
}