package com.ems.lite.admin.di.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ems.lite.admin.R
import com.ems.lite.admin.di.ResourcesProvider
import com.ems.lite.admin.di.repository.VoterRepository
import com.ems.lite.admin.model.request.*
import com.ems.lite.admin.model.response.*
import com.ems.lite.admin.model.response.VoterListResponse
import com.ems.lite.admin.model.table.Cast
import com.ems.lite.admin.model.table.Designation
import com.ems.lite.admin.model.table.Profession
import com.ems.lite.admin.model.table.Religion
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.network.ApiResponseState
import com.ems.lite.admin.network.Status
import com.ems.lite.admin.room.ElectionDatabase
import com.ems.lite.admin.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoterViewModel
@Inject
constructor(
    private val resourcesProvider: ResourcesProvider,
    private val voterRepository: VoterRepository
) : ViewModel() {
    val voterListState =
        MutableStateFlow(ApiResponseState(Status.LOADING, VoterListResponse(), "", 0))
    val voterDetailState =
        MutableStateFlow(ApiResponseState(Status.LOADING, VoterDetailResponse(), "", 0))
    val userListState =
        MutableStateFlow(ApiResponseState(Status.LOADING, UserListResponse(), "", 0))
    val relativeListState =
        MutableStateFlow(ApiResponseState(Status.LOADING, RelativeListResponse()))
    val talukaListState =
        MutableStateFlow(ApiResponseState(Status.LOADING, TalukaListResponse()))
    val saveRelativeState =
        MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))
    val relativeCountListState =
        MutableStateFlow(ApiResponseState(Status.LOADING, RelativeCountListResponse()))

    fun getDB(): ElectionDatabase {
        return voterRepository.getDB()
    }

    fun getAppSetting(): LiveData<AppSettingResponse?> {
        return voterRepository.getAppSetting()
    }

    fun getVoterMasterList(offset: Long): LiveData<VoterListResponse?> {
        return voterRepository.getVoterMasterList(offset)
    }

    fun saveVoter(request: SaveVoterRequest): LiveData<VoterListResponse?> {
        return voterRepository.saveVoter(request)
    }

    fun getUpdatedVoterList(request: UpdatedVoterListRequest): LiveData<VoterListResponse?> {
        return voterRepository.getVoterUpdatedList(request)
    }

    fun getCastList(): LiveData<CastListResponse?> {
        return voterRepository.getCastList()
    }

    fun getProfessionList(): LiveData<ProfessionListResponse?> {
        return voterRepository.getProfessionList()
    }

    fun getDesignationMasters(): LiveData<DesignationListResponse?> {
        return voterRepository.getDesignationMasters()
    }

    fun getReligionMasters(): LiveData<ReligionListResponse?> {
        return voterRepository.getReligionMasters()
    }

    fun insertOccupation(list: ArrayList<Voter>) = viewModelScope.launch {
        voterRepository.insertOccupation(list)
    }

    fun insertUpdatedVoter(list: ArrayList<Voter>) = viewModelScope.launch {
        voterRepository.insertUpdatedVoter(list)
    }

    fun insertCast(list: ArrayList<Cast>) = viewModelScope.launch {
        voterRepository.insertCast(list)
    }

    fun insertDesignation(list: ArrayList<Designation>) = viewModelScope.launch {
        voterRepository.insertDesignation(list)
    }

    fun insertProfession(list: ArrayList<Profession>) = viewModelScope.launch {
        voterRepository.insertProfession(list)
    }

    fun insertReligion(list: ArrayList<Religion>) = viewModelScope.launch {
        voterRepository.insertReligion(list)
    }

    fun saveVoterlist(action: String, request: SaveVoterListRequest): LiveData<VoterListResponse?> {
        return voterRepository.saveVoterList(action, request)
    }

    fun getUserList(request: SearchUserListRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                userListState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                userListState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    voterRepository.getUserList(request).catch {
                        userListState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        userListState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }


    fun updateUser(request: UpdateUserRequest): LiveData<CommonResponse?> {
        return voterRepository.updateUser(request)
    }

    fun getVoterList(request: VoterListRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                voterListState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                voterListState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    voterRepository.getVoterList(request).catch {
                        voterListState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        voterListState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getRelativeList(request: RelativeListRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                relativeListState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                relativeListState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    voterRepository.getRelativeList(request).catch {
                        relativeListState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        relativeListState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getRelativeTalukaMaster() {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                talukaListState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                talukaListState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    voterRepository.getRelativeTalukaMaster().catch {
                        talukaListState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        talukaListState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun saveRelative(request: SaveRelativeRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                saveRelativeState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                saveRelativeState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    voterRepository.saveRelative(request).catch {
                        saveRelativeState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        saveRelativeState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getRelativeCountList(request: com.ems.lite.admin.model.request.RelativeCountListRequest) {
        when {
            (!CommonUtils.isNetworkAvailable(resourcesProvider.context)) -> {
                relativeCountListState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                relativeCountListState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    voterRepository.getRelativeCountList(request).catch {
                        relativeCountListState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        relativeCountListState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

}