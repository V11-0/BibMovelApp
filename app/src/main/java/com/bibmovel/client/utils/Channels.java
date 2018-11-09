package com.bibmovel.client.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Created by vinibrenobr11 on 03/11/18 at 15:58
 */
public abstract class Channels {

    /**
     * Cria um canal de notificção e o retorna
     *
     * @return NotificationChannel Download
     */
    @RequiresApi(Build.VERSION_CODES.O)
    public static NotificationChannel getDownloadChannel() {

        NotificationChannel d = new NotificationChannel(Values.Notification.CHANNEL_DOWNLOAD_ID
                , Values.Notification.CHANNEL_DOWNLOAD_NAME, NotificationManager.IMPORTANCE_LOW);

        d.setSound(null, null);
        d.enableVibration(false);
        d.enableLights(false);

        return d;
    }
}
