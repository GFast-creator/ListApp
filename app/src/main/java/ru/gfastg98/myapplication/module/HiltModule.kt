package ru.gfastg98.myapplication.module

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.gfastg98.myapplication.R
import ru.gfastg98.myapplication.module.CONSTANTS.CHANNEL_ID
import javax.inject.Singleton

object CONSTANTS {
    const val NOTIFICATION_ID = 1990
    const val CHANNEL_ID = "1991"
    const val CHANNEL_NAME = "list_app"
}

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Singleton
    @Provides
    fun notificationBuilder(@ApplicationContext context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("List_app")
            .setSmallIcon(R.drawable.baseline_find_in_page_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    @Singleton
    @Provides
    fun vibratorManager(app: Application): VibratorManager {
        return app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }

    @Singleton
    @Provides
    fun notificationManager(app: Application): NotificationManager {

        return (app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CONSTANTS.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "channel for app"
                }
            )
        }
    }
}