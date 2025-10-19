package com.ems.lite.admin.di.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ems.lite.admin.di.ResourcesProvider
import com.ems.lite.admin.di.repository.VillageRepository
import com.ems.lite.admin.model.request.VillageListRequest
import com.ems.lite.admin.model.response.*
import com.ems.lite.admin.model.table.*
import com.ems.lite.admin.room.ElectionDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VillageViewModel
@Inject
constructor(
    private val resourcesProvider: ResourcesProvider,
    private val villageRepository: VillageRepository
) : ViewModel() {

    fun getDB(): ElectionDatabase {
        return villageRepository.getDB()
    }

    fun getVillageMasterList(request: VillageListRequest): LiveData<VillageListResponse?> {
        return villageRepository.getVillageMasterList(request)
    }

    fun insertVillage(list: ArrayList<Village>) = viewModelScope.launch {
        villageRepository.insertVillage(list)
    }
}