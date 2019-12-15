package com.bibmovel.client.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;

import com.bibmovel.client.utils.ConnectionFactory;
import com.bibmovel.client.utils.Values;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * Created by vinibrenobr11 on 21/11/18 at 09:44
 */
public class UploadService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            File to_upload = (File) intent.getSerializableExtra("file");
            SmbFile smbFile = ConnectionFactory.getSmbConnection(Values.Path.BOOKS_PATH + to_upload.getName());
            int read;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                Files.copy(to_upload.toPath(), smbFile.getOutputStream());
            else {

                try (FileInputStream fis = new FileInputStream(to_upload);
                     SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFile)) {

                    final byte[] b = new byte[16 * 1024];

                    while ((read = fis.read(b, 0, b.length)) > 0)
                        smbfos.write(b, 0, read);

                    smbfos.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}