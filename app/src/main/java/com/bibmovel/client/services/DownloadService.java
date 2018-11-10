package com.bibmovel.client.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.bibmovel.client.BuildConfig;
import com.bibmovel.client.utils.Channels;
import com.bibmovel.client.utils.ConnectionFactory;
import com.bibmovel.client.utils.Values;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.FileProvider;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

/**
 * Created by vinibrenobr11 on 09/11/18 at 18:57
 */
public class DownloadService extends IntentService {

    private NotificationManager mNotifierManager;
    private NotificationCompat.Builder mBuilder;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        mNotifierManager = (NotificationManager)
                getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotifierManager != null)
            mNotifierManager.createNotificationChannel(Channels.getDownloadChannel());

        mBuilder = new NotificationCompat.Builder(getBaseContext()
                , Values.Notification.CHANNEL_DOWNLOAD_ID);

        try {

            SmbFile dir;
            SmbFile[] files;
            boolean isBook = intent.getBooleanExtra("isBook", true);

            if (isBook) {

                dir = ConnectionFactory.getSmbConnection(Values.Path.BOOKS_PATH);
                files = dir.listFiles();

                String file_name = intent.getStringExtra("bookName");

                for (SmbFile smbFile : files) {

                    if (smbFile.getName().equals(file_name)) {
                        try {
                            downloadFile(smbFile, Values.Path.DOWNLOAD_BOOKS);
                            buildNotification("Livro Baixado", null, null);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }

            } else {

                dir = ConnectionFactory.getSmbConnection(Values.Path.UPDATES_PATH);
                files = dir.listFiles();

                SmbFile update = files[1];

                if (update.getName().equals("app-release.apk")) {
                    try {
                        File update_file = downloadFile(update, getFilesDir().getPath());
                        promptInstall(update_file);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else
                    throw new UnsupportedOperationException("Update file do not match");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void promptInstall(File file) {

        PendingIntent pendingIntent;
        TaskStackBuilder mTask = TaskStackBuilder.create(getBaseContext());

        // Seta um pending intent na notificação, para que quando o usuário clique
        // na notificação, ela abra a tela de instalação
        Intent promptInstall = new Intent(Intent.ACTION_VIEW);

        // Para inferior a versão 7.0 do Android
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

            promptInstall.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");

        } else {
            // Para Versão 7.0 ou superior do Android
            Uri uri = FileProvider.getUriForFile(getBaseContext(),
                    BuildConfig.APPLICATION_ID + ".provider", file);

            promptInstall.setDataAndType(uri, "application/vnd.android.package-archive");
            promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        mTask.addNextIntent(promptInstall);
        pendingIntent = mTask.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        buildNotification("Atualização Baixada", "Toque para Instalar", pendingIntent);
    }

    private void buildNotification(String title, String text, PendingIntent intent) {

        mBuilder.setContentTitle(title)
                .setContentText(text)
                .setProgress(0, 0, false)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentIntent(intent)
                .setOngoing(false)
                .setAutoCancel(true);

        mNotifierManager.notify(0, mBuilder.build());
    }

    private File downloadFile(SmbFile smbFile, String dir) throws IOException, InterruptedException {

        smbFile.connect();
        File file = new File(dir + "/" + smbFile.getName());

        if (file.createNewFile()) {

            mBuilder.setContentTitle("Baixando")
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setOngoing(true);

            mNotifierManager.notify(0, mBuilder.build());

            double size = smbFile.length();

            Thread download = new Thread(() -> {

                try {

                    BufferedInputStream in = new BufferedInputStream(new SmbFileInputStream(smbFile));
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

                    byte[] buffer = new byte[4096];

                    int len; //Read length

                    while ((len = in.read(buffer, 0, buffer.length)) != -1)
                        out.write(buffer, 0, len);

                    out.flush(); //The refresh buffer output stream

                    in.close();
                    out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            download.start();

            DecimalFormat nf = new DecimalFormat("0.00");

            while (download.isAlive()) {

                double file_lenght = file.length();
                double progress = (file_lenght / size) * 100;

                mBuilder.setProgress(100, (int) progress, false)
                        .setContentInfo(nf.format(progress) + "%");

                mNotifierManager.notify(0, mBuilder.build());

                Thread.sleep(1000);
            }

            return file;

        } else {
            Log.d("FILE", "Livro já existe");
        }
        return null;
    }
}
