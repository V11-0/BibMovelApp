package com.bibmovel.client.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

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

        ResultReceiver receiver = intent.getParcelableExtra("receiver");

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
                            File file = downloadFile(smbFile, Values.Path.DOWNLOAD_BOOKS);
                            PendingIntent pendingIntent = getBookPendingIntent(file);

                            buildNotification(file.getName() + " Baixado", null, pendingIntent);

                            Bundle bundle = new Bundle();
                            bundle.putString("path", file_name);

                            receiver.send(1, bundle);

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

    private PendingIntent getBookPendingIntent(File file) {

        TaskStackBuilder mTask = TaskStackBuilder.create(getBaseContext());

        // Seta um pending intent na notificação, para que quando o usuário clique
        // na notificação, ela abra a tela de instalação
        Intent pdf_intent = new Intent(Intent.ACTION_VIEW);
        Uri file_uri;

        // Para inferior a versão 7.0 do Android
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            file_uri = Uri.fromFile(file);
        else {
            // Para Versão 7.0 ou superior do Android
            file_uri = FileProvider.getUriForFile(getBaseContext(),
                    BuildConfig.APPLICATION_ID + ".provider", file);
        }

        pdf_intent.setDataAndType(file_uri, "application/pdf");
        pdf_intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        pdf_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        mTask.addNextIntent(pdf_intent);

        return mTask.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
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
                .setSubText("Download Concluído")
                .setProgress(0, 0, false)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentIntent(intent)
                .setOngoing(false)
                .setAutoCancel(true);

        mNotifierManager.notify(0, mBuilder.build());
    }

    private File downloadFile(SmbFile smbFile, String dir) throws IOException, InterruptedException {

        smbFile.connect();
        String file_name = dir + "/" + smbFile.getName();

        if (dir.contains(Environment.DIRECTORY_DOWNLOADS)) {

            File bibmovel_dir = new File(dir);

            if (!bibmovel_dir.exists())
                bibmovel_dir.mkdir();
        }

        mBuilder.setContentTitle("Baixando")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setOngoing(true);

        mNotifierManager.notify(0, mBuilder.build());

        double size = smbFile.length();

        Thread download = new Thread(() -> {

            try {

                BufferedInputStream in = new BufferedInputStream(new SmbFileInputStream(smbFile));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file_name));

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

        File file = new File(dir + "/" + smbFile.getName());

        while (download.isAlive()) {

            double file_lenght = file.length();
            double progress = (file_lenght / size) * 100;

            mBuilder.setProgress(100, (int) progress, false)
                    .setSubText(nf.format(progress) + "%");

            mNotifierManager.notify(0, mBuilder.build());

            Thread.sleep(1000);
        }

        return file;
    }
}
