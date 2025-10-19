package com.ems.lite.admin.di.repository

import com.ems.lite.admin.model.request.LoginRequest
import com.ems.lite.admin.model.request.UserRequest
import com.ems.lite.admin.model.response.LoginResponse
import com.ems.lite.admin.network.ApiResponseState
import com.ems.lite.admin.network.NetworkService
import com.ems.lite.admin.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnBoardingRepository @Inject constructor(private val networkService: NetworkService) {

    suspend fun login(request: LoginRequest): Flow<ApiResponseState<LoginResponse>> {
        return flow {
            val response = networkService.api.login(request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMe(request: UserRequest): Flow<ApiResponseState<LoginResponse>> {
        return flow {
            val response = networkService.api.getMe(request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        CommonUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

}