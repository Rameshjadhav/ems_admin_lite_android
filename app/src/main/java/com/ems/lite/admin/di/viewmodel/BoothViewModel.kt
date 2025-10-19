package com.ems.lite.admin.di.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ems.lite.admin.di.ResourcesProvider
import com.ems.lite.admin.di.repository.BoothRepository
import com.ems.lite.admin.model.response.BoothListResponse
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.room.ElectionDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoothViewModel
@Inject
constructor(
    private val resourcesProvider: ResourcesProvider,
    private val boothRepository: BoothRepository
) : ViewModel() {
    fun getDB(): ElectionDatabase {
        return boothRepository.getDB()
    }

    fun getBoothMasterList(): LiveData<BoothListResponse?> {
        return boothRepository.getBoothMasterList()
    }

    fun insertBooth(list: ArrayList<Booth>) = viewModelScope.launch {
        boothRepository.insertBooth(list)
    }

    fun insertUpdatedBooth(list: ArrayList<Booth>) = viewModelScope.launch {
        boothRepository.insertupdatedBooth(list)
    }
}