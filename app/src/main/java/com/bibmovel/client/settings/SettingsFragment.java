package com.bibmovel.client.settings;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.bibmovel.client.BuildConfig;
import com.bibmovel.client.R;
import com.bibmovel.client.utils.Channels;
import com.bibmovel.client.utils.ConnectionFactory;
import com.bibmovel.client.utils.Values;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        Preference update = findPreference("pref_update");
        update.setSummary("Versão " + BuildConfig.VERSION_NAME);
        update.setOnPreferenceClickListener(preference -> {

            Snackbar.make(getView(), "Verificando por Atualizações", Snackbar.LENGTH_LONG).show();

            DownloadUpdate downloadUpdate = new DownloadUpdate(new WeakReference<>(getContext()));
            downloadUpdate.execute(null, null, null);

            return true;
        });

        Preference about = findPreference("pref_licenses");
        about.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
            return false;
        });
    }

    // TODO: 03/11/18 É uma boa isso virar um service
    private static class DownloadUpdate extends AsyncTask<Void, Void, File> {

        private final WeakReference<Context> weakContext;
        private NotificationManager mNotifierManager;
        private NotificationCompat.Builder mBuilder;

        private DownloadUpdate(WeakReference<Context> weakContext) {
            this.weakContext = weakContext;
            mNotifierManager = (NotificationManager)
                    weakContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                mNotifierManager.createNotificationChannel(Channels.getDownloadChannel());

            mBuilder = new NotificationCompat.Builder(weakContext.get()
                    , Values.Notification.CHANNEL_DOWNLOAD_ID);
        }

        @Override
        protected void onPostExecute(File file) {

            if (file == null)
                Toast.makeText(weakContext.get(), "Seu App já está atualizado", Toast.LENGTH_LONG).show();
            else {

                PendingIntent pendingIntent;
                TaskStackBuilder mTask = TaskStackBuilder.create(weakContext.get());

                // Seta um pending intent na notificação, para que quando o usuário clique
                // na notificação, ela abra a tela de instalação
                Intent promptInstall = new Intent(Intent.ACTION_VIEW);

                // Para inferior a versão 7.0 do Android
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

                    promptInstall.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");

                } else {
                    // Para Versão 7.0 ou superior do Android
                    Uri uri = FileProvider.getUriForFile(weakContext.get(),
                            BuildConfig.APPLICATION_ID + ".provider", file);

                    promptInstall.setDataAndType(uri, "application/vnd.android.package-archive");
                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                mTask.addNextIntent(promptInstall);
                pendingIntent = mTask.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentTitle("Atualização Baixada")
                        .setContentText("Toque para Instalar")
                        .setProgress(0, 0, false)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentIntent(pendingIntent)
                        .setOngoing(false)
                        .setAutoCancel(true);

                mNotifierManager.notify(0, mBuilder.build());
            }
        }

        @Override
        protected File doInBackground(Void... voids) {

            // TODO: 30/10/2018 Verificar updates
            try {

                SmbFile updates_dir = ConnectionFactory.getSmbConnection("Updates/");
                SmbFile[] files = updates_dir.listFiles();

                if (Integer.parseInt(files[0].getName()) > BuildConfig.VERSION_CODE) {

                    SmbFile update = files[1];
                    // TODO: 03/11/18 Pedir confirmação do usuário para download

                    if (update.getName().equals("app-release.apk")) {

                        update.connect();
                        File file = new File(weakContext.get().getFilesDir() + "/app-release.apk");

                        mBuilder.setContentTitle("Baixando Atualização")
                                .setSmallIcon(android.R.drawable.stat_sys_download)
                                .setOngoing(true);

                        mNotifierManager.notify(0, mBuilder.build());

                        double size = update.length();

                        Thread download = new Thread(() -> {

                            try {

                                BufferedInputStream in = new BufferedInputStream(new SmbFileInputStream(update));
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

                            Thread.sleep(2 * 1000);
                        }

                        return file;

                    } else
                        throw new UnsupportedOperationException("Update file do not match");
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
