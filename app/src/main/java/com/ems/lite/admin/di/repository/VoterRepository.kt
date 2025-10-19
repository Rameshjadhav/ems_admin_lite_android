package com.ems.lite.admin.di.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ems.lite.admin.model.request.*
import com.ems.lite.admin.model.response.*
import com.ems.lite.admin.model.response.VoterListResponse
import com.ems.lite.admin.model.table.Cast
import com.ems.lite.admin.model.table.Designation
import com.ems.lite.admin.model.table.Profession
import com.ems.lite.admin.model.table.Religion
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.network.ApiResponseState
import com.ems.lite.admin.network.NetworkService
import com.ems.lite.admin.room.ElectionDatabase
import com.ems.lite.admin.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Ramesh on 10/05/2021
 */
@Singleton
class VoterRepository @Inject constructor(
    private val db: ElectionDatabase,
    private val networkService: NetworkService
) {
    fun getDB(): ElectionDatabase {
        return db
    }

    suspend fun insertOccupation(list: List<Voter>) {
        return db.voterDao().insert(list)
    }

    suspend fun insertUpdatedVoter(list: List<Voter>) {
        return db.voterDao().insert(list)
    }

    suspend fun insertCast(list: List<Cast>) {
        return db.CastDao().insert(list)
    }

    suspend fun insertDesignation(list: List<Designation>) {
        return db.designationDao().insert(list)
    }

    suspend fun insertProfession(list: List<Profession>) {
        return db.ProfessionDao().insert(list)
    }

    suspend fun insertReligion(list: List<Religion>) {
        return db.religionDao().insert(list)
    }


    fun getVoterUpdatedList(request: UpdatedVoterListRequest): LiveData<VoterListResponse?> {
        val data = MutableLiveData<VoterListResponse?>()
        networkService.api.getUpdatedVoterList(request)
            .enqueue(object : Callback<VoterListResponse> {
                override fun onResponse(
                    call: Call<VoterListResponse>,
                    response: Response<VoterListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: VoterListResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = VoterListResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = VoterListResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<VoterListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: VoterListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = VoterListResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }

    fun saveVoterList(action: String, request: SaveVoterListRequest): LiveData<VoterListResponse?> {
        val data = MutableLiveData<VoterListResponse?>()
        networkService.api.saveVoterList(action, request)
            .enqueue(object : Callback<VoterListResponse> {
                override fun onResponse(
                    call: Call<VoterListResponse>,
                    response: Response<VoterListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: VoterListResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {

                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = VoterListResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = VoterListResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<VoterListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: VoterListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = VoterListResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }


    fun getVoterMasterList(offset: Long): LiveData<VoterListResponse?> {
        val data = MutableLiveData<VoterListResponse?>()
        networkService.api.getVoterMasterList(offset)
            .enqueue(object : Callback<VoterListResponse> {
                override fun onResponse(
                    call: Call<VoterListResponse>,
                    response: Response<VoterListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: VoterListResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = VoterListResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = VoterListResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<VoterListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: VoterListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = VoterListResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }

    fun saveVoter( request: SaveVoterRequest): LiveData<VoterListResponse?> {
        val data = MutableLiveData<VoterListResponse?>()
        networkService.api.saveVoter(request)
            .enqueue(object : Callback<VoterListResponse> {
                override fun onResponse(
                    call: Call<VoterListResponse>,
                    response: Response<VoterListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: VoterListResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {

                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = VoterListResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = VoterListResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<VoterListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: VoterListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = VoterListResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }

    fun getCastList(): LiveData<CastListResponse?> {
        val data = MutableLiveData<CastListResponse?>()
        networkService.api.getCastList()
            .enqueue(object : Callback<CastListResponse> {
                override fun onResponse(
                    call: Call<CastListResponse>,
                    response: Response<CastListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: CastListResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = CastListResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = CastListResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<CastListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: CastListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = CastListResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }

    fun getProfessionList(): LiveData<ProfessionListResponse?> {
        val data = MutableLiveData<ProfessionListResponse?>()
        networkService.api.getProfessionList()
            .enqueue(object : Callback<ProfessionListResponse> {
                override fun onResponse(
                    call: Call<ProfessionListResponse>,
                    response: Response<ProfessionListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: ProfessionListResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = ProfessionListResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = ProfessionListResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<ProfessionListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: ProfessionListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = ProfessionListResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }

    fun getDesignationMasters(): LiveData<DesignationListResponse?> {
        val data = MutableLiveData<DesignationListResponse?>()
        networkService.api.getDesignationMasters()
            .enqueue(object : Callback<DesignationListResponse> {
                override fun onResponse(
                    call: Call<DesignationListResponse>,
                    response: Response<DesignationListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var designationListResponse: DesignationListResponse?
                    if (response.isSuccessful) {
                        designationListResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        designationListResponse = DesignationListResponse()
                        designationListResponse.error = error
                    }
                    if (designationListResponse == null) {
                        designationListResponse = DesignationListResponse()
                    }
                    designationListResponse.statusCode = response.code()
                    data.postValue(designationListResponse)
                }

                override fun onFailure(call: Call<DesignationListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var designationListResponse: DesignationListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        designationListResponse = DesignationListResponse()
                        designationListResponse!!.statusCode =
                            ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(designationListResponse)
                }
            })
        return data
    }

    fun getReligionMasters(): LiveData<ReligionListResponse?> {
        val data = MutableLiveData<ReligionListResponse?>()
        networkService.api.getReligionMasters()
            .enqueue(object : Callback<ReligionListResponse> {
                override fun onResponse(
                    call: Call<ReligionListResponse>,
                    response: Response<ReligionListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var designationListResponse: ReligionListResponse?
                    if (response.isSuccessful) {
                        designationListResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        designationListResponse = ReligionListResponse()
                        designationListResponse.error = error
                    }
                    if (designationListResponse == null) {
                        designationListResponse = ReligionListResponse()
                    }
                    designationListResponse.statusCode = response.code()
                    data.postValue(designationListResponse)
                }

                override fun onFailure(call: Call<ReligionListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var designationListResponse: ReligionListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        designationListResponse = ReligionListResponse()
                        designationListResponse!!.statusCode =
                            ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(designationListResponse)
                }
            })
        return data
    }

    suspend fun getUserList(request: SearchUserListRequest): Flow<ApiResponseState<UserListResponse>> {
        return flow {
            val response = networkService.api.getUserList(request)
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
    fun updateUser(request: UpdateUserRequest): LiveData<CommonResponse?> {
        val data = MutableLiveData<CommonResponse?>()
        networkService.api.updateUser(request)
            .enqueue(object : Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var commonResponse: CommonResponse?
                    if (response.isSuccessful) {
                        commonResponse = response.body()
                    } else {

                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        commonResponse = CommonResponse()
                        commonResponse.error = error
                    }
                    if (commonResponse == null) {
                        commonResponse = CommonResponse()
                    }
                    commonResponse.statusCode = response.code()
                    data.postValue(commonResponse)
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: CommonResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = CommonResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }

    fun getAppSetting(): LiveData<AppSettingResponse?> {
        val data = MutableLiveData<AppSettingResponse?>()
        networkService.api.getAppSetting()
            .enqueue(object : Callback<AppSettingResponse> {
                override fun onResponse(
                    call: Call<AppSettingResponse>,
                    response: Response<AppSettingResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: AppSettingResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = AppSettingResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = AppSettingResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<AppSettingResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: AppSettingResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = AppSettingResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }
    suspend fun getVoterList(request: VoterListRequest): Flow<ApiResponseState<VoterListResponse>> {
        return flow {
            val response = networkService.api.getVoterList(request)
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

    suspend fun getRelativeList(request: RelativeListRequest): Flow<ApiResponseState<RelativeListResponse>> {
        return flow {
            val response = networkService.api.getRelativeList(request)
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

    suspend fun getRelativeTalukaMaster(): Flow<ApiResponseState<TalukaListResponse>> {
        return flow {
            val response = networkService.api.getRelativeTalukaMaster()
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

    suspend fun saveRelative(request: SaveRelativeRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.saveRelative(request)
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
    suspend fun getRelativeCountList(request: com.ems.lite.admin.model.request.RelativeCountListRequest): Flow<ApiResponseState<RelativeCountListResponse>> {
        return flow {
            val response = networkService.api.getRelativeCountList(request)
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