package com.ems.lite.admin.di.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ems.lite.admin.model.response.BoothListResponse
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.network.NetworkService
import com.ems.lite.admin.room.ElectionDatabase
import com.ems.lite.admin.utils.CommonUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoothRepository @Inject constructor(
    private val db: ElectionDatabase,
    private val networkService: NetworkService
) {
    fun getDB(): ElectionDatabase {
        return db
    }

    suspend fun insertBooth(list: List<Booth>) {
        return db.BoothDao().insert(list)
    }

    suspend fun insertupdatedBooth(list: List<Booth>) {
        return db.BoothDao().insert(list)
    }

    fun getBoothMasterList(): LiveData<BoothListResponse?> {
        val data = MutableLiveData<BoothListResponse?>()
        networkService.api.getBoothMasterList()
            .enqueue(object : Callback<BoothListResponse> {
                override fun onResponse(
                    call: Call<BoothListResponse>,
                    response: Response<BoothListResponse>
                ) {
                    //Successful Response from server
                    //Update the Live Data object
                    var scanQRResponse: BoothListResponse?
                    if (response.isSuccessful) {
                        scanQRResponse = response.body()
                    } else {
                        val error = CommonUtils.getErrorResponse(response.errorBody())
                        scanQRResponse = BoothListResponse()
                        scanQRResponse.error = error
                    }
                    if (scanQRResponse == null) {
                        scanQRResponse = BoothListResponse()
                    }
                    scanQRResponse.statusCode = response.code()
                    data.postValue(scanQRResponse)
                }

                override fun onFailure(call: Call<BoothListResponse>, t: Throwable) {

                    //Updating the response even for failure as the loader gets stuck
                    var scanQRResponse: BoothListResponse? = null
                    if (CommonUtils.isTimeOutError(t)) {
                        scanQRResponse = BoothListResponse()
                        scanQRResponse!!.statusCode = ResponseStatus.STATUS_CODE_ERROR_TIMEOUT
                    }
                    data.postValue(scanQRResponse)
                }
            })
        return data
    }
}