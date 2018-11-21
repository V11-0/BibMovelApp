package com.bibmovel.client.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by vinibrenobr11 on 20/11/18 at 21:54
 */
public class DownloadResultReceiver extends ResultReceiver {

    private ResulReceiverCallback callback;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public DownloadResultReceiver(Handler handler, ResulReceiverCallback callback) {
        super(handler);
        this.callback = callback;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (resultCode == 1)
            callback.onDownloaded(resultData.getString("path"));
    }
}
