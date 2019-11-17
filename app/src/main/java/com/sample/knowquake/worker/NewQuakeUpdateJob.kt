package com.sample.knowquake.worker

import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.sample.knowquake.R
import com.sample.knowquake.api.ApiService
import com.sample.knowquake.rx.SchedulerProvider
import com.sample.knowquake.util.TimeUtils
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject


class NewQuakeUpdateJob(
    @NonNull context: Context,
    @NonNull workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val schedulerProvider: SchedulerProvider
) : RxWorker(context, workerParams) {

    override fun createWork(): Single<Result> {
        return apiService.lastAnHourEarthQuake()
            .flatMap { Single.just(it.features) }
            .map { it.first() }
            .map {
                showNotification(
                    it.properties.title,
                    TimeUtils.getUnixTimestampToDate(it.properties.time, TimeUtils.DATE_TIME_FORMAT_2, true)
                )
                Result.success()
            }.onErrorReturnItem(Result.failure())
    }

    override fun getBackgroundScheduler(): Scheduler {
        return schedulerProvider.executorIo()
    }

    private fun showNotification(task: String, desc: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "new_quake_channel"
        val channelName = "New EarthQuake"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        // It just quite simple notification builder and while tapping the notification it doesn't navigate to any activity.
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(task)
            .setContentText(desc)
            .setSmallIcon(R.mipmap.ic_launcher)
        manager.notify(1, builder.build()) // Notificatio just replace existing one if you want to show each notification as new increment the id.
    }

    class Factory @Inject constructor(
        private val apiService: ApiService,
        private val schedulerProvider: SchedulerProvider
    ) : DaggerWorkerFactory.ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return NewQuakeUpdateJob(appContext, params, apiService, schedulerProvider)
        }
    }

}