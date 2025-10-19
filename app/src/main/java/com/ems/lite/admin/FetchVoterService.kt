package com.ems.lite.admin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.room.Room
import com.ems.lite.admin.app.MyApplication
import com.ems.lite.admin.di.ResourcesProvider
import com.ems.lite.admin.di.repository.VoterRepository
import com.ems.lite.admin.di.viewmodel.VoterViewModel
import com.ems.lite.admin.model.response.ResponseStatus
import com.ems.lite.admin.network.NetworkService
import com.ems.lite.admin.network.Url
import com.ems.lite.admin.room.ElectionDatabase
import com.ems.lite.admin.ui.activities.HomeActivity
import com.ems.lite.admin.utils.Logger
import com.ems.lite.admin.utils.Prefs
import com.ems.lite.admin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FetchVoterService : LifecycleService() {
    private var voterViewModel: VoterViewModel? = null
    private var voterRepository: VoterRepository? = null
    private var offset: Long = 0

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val networkService = NetworkService(this, Url.HOST)
        voterRepository = VoterRepository(
            ElectionDatabase.getDatabase(this), networkService
        )
        voterViewModel = VoterViewModel(ResourcesProvider(this), voterRepository!!)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(9998, getNotification(this))
        CoroutineScope(Dispatchers.Main).launch {
            var totalDbCount = voterViewModel!!.getDB().voterDao().getAllCount()
            var limit = Prefs.limit
            if (totalDbCount > 0) {
                offset = ((totalDbCount / limit) - 1)
            }
            fetchVoterApi()
        }
        return START_NOT_STICKY
    }

    private fun fetchVoterApi() {
        voterViewModel?.getVoterMasterList(offset)?.observe(this) { response ->
            if (response != null && response.statusCode == ResponseStatus.STATUS_CODE_SUCCESS) {
                if (!response.list.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        voterViewModel?.insertOccupation(response.list!!)
                    }
                    offset = response.nextOffset
                    Prefs.totalCount = response.totalCount
                    Prefs.limit = response.limit
                    if (offset > 0) {
                        fetchVoterApi()
                    } else {
                        Prefs.isVoterSync = true
                        stopSelf()
                    }
                }
            } else {
                stopSelf()
            }
            Logger.d("FetchVoterService", "${offset}")
        }
    }

    private fun getNotification(context: Context): Notification {
        val intent = Intent(context, HomeActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var channelId: String? = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId =
                createNotificationChannel(MyApplication.CHANNEL_ID, getString(R.string.app_name))
        }
        val notificationBuilder = NotificationCompat.Builder(context, channelId!!).apply {
            setContentText("Fetching Voters...")
            priority = 2
            setSmallIcon(R.mipmap.ic_launcher_new)
            setShowWhen(false)
            setOngoing(true)
            setContentIntent(pendingIntent)
        }
        return notificationBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chanel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.BLACK
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chanel)
        return channelId
    }
}