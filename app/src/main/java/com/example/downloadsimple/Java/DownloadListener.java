package com.example.downloadsimple.Java;

/**
 * Created by 张垚杰 on 2018/1/16.
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
