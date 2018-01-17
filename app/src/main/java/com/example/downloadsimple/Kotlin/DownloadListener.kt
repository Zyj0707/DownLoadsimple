package com.example.downloadsimple.Kotlin

/**
 * Created by 张垚杰 on 2018/1/16.
 */
interface DownloadListener {
     fun onProgress(progress: Int?)
     fun onSuccess()
     fun onFailed()
     fun onPaused()
     fun onCanceled()
}
