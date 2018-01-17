package com.example.downloadsimple.Kotlin

import android.os.AsyncTask
import android.os.Environment
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * Created by 张垚杰 on 2018/1/16.
 */
class DownloadTask(private val listener: DownloadListener) : AsyncTask<String, Int, Int>() {
    val TYPE_SUCCESS = 0
    val TYPE_FAILED = 1
    val TYPE_PAUSED = 2
    val TYPE_CANCELED = 3

    //private var listener = object :DownloadListener


    var isCanceled =false
    var isPaused = false
    private var lastProgress : Int = 0




    override fun doInBackground(vararg params: String): Int? {
        var iois: InputStream? =null
        var saveFile: RandomAccessFile? =null
        var file: File?= null
        try{
            var downloadedLength: Long = 0
            val downloadUrl: String = params[0]
            val fileName: String = downloadUrl.substring(downloadUrl.lastIndexOf("/"))
            val directory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).path
            file = File(directory+fileName)
            if(file.exists()){
                downloadedLength=file.length()
            }
            val contentLength = getContentLength(downloadUrl)
            if(contentLength == 0L){
                return TYPE_FAILED
            }else if (contentLength == downloadedLength){
                return TYPE_SUCCESS
            }
            val client = OkHttpClient()
            val request = Request.Builder()
                    .addHeader("RANGE", "bytes=$downloadedLength-")
                    .url(downloadUrl)
                    .build()
            val response = client.newCall(request).execute()
            if(response!=null){
                iois=response.body()!!.byteStream()
                saveFile = RandomAccessFile(file,"rw")
                saveFile.seek(downloadedLength)
                val b = ByteArray(1024)
                var total = 0
                var len: Int
                while(true){
                    len = iois.read(b)
                    if(len == -1){
                        break
                    }
                    if (isCanceled) {
                        return TYPE_CANCELED
                    } else if (isPaused) {
                        return TYPE_PAUSED
                    } else {
                        total += len
                        saveFile.write(b, 0, len)
                        val progress = ((total + downloadedLength) * 100 / contentLength).toInt()
                        publishProgress(progress)
                    }
                }
                response.body()!!.close()
                return TYPE_SUCCESS
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            try {
                if (iois != null) {
                    iois.close()
                }
                if (saveFile != null) {
                    saveFile.close()
                }
                if (isCanceled && file != null) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return TYPE_FAILED
    }


    override fun onProgressUpdate(vararg values: Int?) {
        val progress = values[0]
        val a = progress!!
        if(a > lastProgress){
            listener.onProgress(progress)
            lastProgress = a
        }
        //super.onProgressUpdate(*values)
    }


    override fun onPostExecute(integer: Int?) {
        when(integer){
            TYPE_SUCCESS->{ listener.onSuccess()}
            TYPE_FAILED->{ listener.onFailed()}
            TYPE_PAUSED->{ listener.onPaused()}
            TYPE_CANCELED->{ listener.onCanceled()}
            else -> {}
        }
       // super.onPreExecute()
    }



    fun pauseDownload(){
        isPaused=true
    }
    fun cancelDownload(){
        isCanceled=true
    }

    @Throws(IOException::class)
    private fun getContentLength(downloadUrl: String): Long{
        val client =OkHttpClient()
        val request = Request.Builder()
                .url(downloadUrl)
                .build()
        val response = client.newCall(request).execute()
        if(response != null && response.isSuccessful){
            val contentLength = response.body()!!.contentLength()
            response.close()
            return contentLength
        }
        return 0
    }
}