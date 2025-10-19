package com.ems.lite.admin.di.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ems.lite.admin.model.request.VillageListRequest
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.response.VillageListResponse
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.network.NetworkService
import com.ems.lite.admin.room.ElectionDatabase
import com.ems.lite.admin.utils.CommonUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VillageRepository @Inject constructor(
    private val db: ElectionDatabase,
    private val networkService: NetworkService
) {
    fun getDB(): ElectionDatabase {
        return db
    }

    suspend fun insertVillage(list: List<Village>) {
        return db.villageDao().insert(list)
    }

    fun getVillageMasterList(request: VillageListRequest): LiveData<VillageListResponse?> {
        val data = MutableLiveData<VillageListResponse?>()
        networkService.api.getVillageMasterList(request)
            .enqueue(object : Callback<VillageListResponse> {
                override fun onResponse(
                    call: Call<VillageListResponse>,
                    response: Response<VillageListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: VillageListResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = VillageListResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = VillageListResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<VillageListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: VillageListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = VillageListResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }
}