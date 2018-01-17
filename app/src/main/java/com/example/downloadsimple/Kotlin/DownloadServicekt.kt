package com.example.downloadsimple.Kotlin

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import com.example.downloadsimple.Java.HomeActivity
import com.example.downloadsimple.R
import java.io.File

class DownloadServicekt : Service() {

    private var downloadTask: DownloadTask? = null
    private var downloadUrl: String? = null

    private val listener = object : DownloadListener {
        override fun onProgress(progress: Int?) {
            getNotificationManager().notify(1,getNotification("Downloading..(kotlin)",progress!!))
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSuccess() {
            downloadTask = null
            stopForeground(true)
            getNotificationManager().notify(1,getNotification("Download success",-1))
            Toast.makeText(this@DownloadServicekt, "Download success(kotlin)", Toast.LENGTH_SHORT).show()
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onFailed() {
            downloadTask = null
            stopForeground(true)
            getNotificationManager().notify(1,getNotification("Download failed",-1))
            Toast.makeText(this@DownloadServicekt, "Download failed(kotlin)", Toast.LENGTH_SHORT).show()
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPaused() {
            downloadTask = null
            stopForeground(true)
            Toast.makeText(this@DownloadServicekt, "Download Paused(kotlin)", Toast.LENGTH_SHORT).show()
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onCanceled() {
            downloadTask = null
            stopForeground(true)
            Toast.makeText(this@DownloadServicekt, "Download Canceled(kotlin)", Toast.LENGTH_SHORT).show()
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private val binder = DownloadBinder()


    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        return binder
        //throw UnsupportedOperationException("Not yet implemented")
    }

    inner class DownloadBinder : Binder(){
        fun startDownload(url: String){
            if(downloadTask == null){
                downloadUrl = url
                downloadTask = DownloadTask(listener)
                downloadTask!!.execute(downloadUrl)
                startForeground(1,getNotification("Downloading...",0))
                Toast.makeText(this@DownloadServicekt, "Downloading...(kotlin)", Toast.LENGTH_SHORT).show()
            }
        }

        fun pauseDownload(){
            if(downloadTask!=null){
                downloadTask!!.pauseDownload()
            }
        }

        fun cancelDownload(){
            if(downloadTask!=null){
                downloadTask!!.cancelDownload()
            }else{
                if(downloadUrl!=null){
                    val fileName = downloadUrl!!.substring(downloadUrl!!.lastIndexOf("/"))
                    val directory = Environment.getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS).path
                    val file = File(directory+fileName)
                    if(file.exists()){
                        file.delete()
                    }
                    getNotificationManager().cancel(1)
                    stopForeground(true)
                    Toast.makeText(this@DownloadServicekt,"CancelDownload..(kotlin)",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getNotificationManager(): NotificationManager{
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    private fun getNotification(title: String,progress: Int): Notification{
        val intent = Intent(this@DownloadServicekt,HomeActivity::class.java)
        val pi = PendingIntent.getActivity(this@DownloadServicekt,0,intent,0)
        val builder = NotificationCompat.Builder(this@DownloadServicekt)
        builder.setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
        if(progress>0){
            val str = progress.toString()
            builder.setContentText(str +"%")
                    .setProgress(100,progress,false)
        }
        return builder.build()
    }
}
