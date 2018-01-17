package com.example.downloadsimple.Kotlin

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.downloadsimple.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by 张垚杰 on 2018/1/17.
 */
class HomeActivitykt :AppCompatActivity() ,View.OnClickListener{
    private var downloadBinder: DownloadServicekt.DownloadBinder? = null

    private val connection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadBinder=service as DownloadServicekt.DownloadBinder
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start_download.setOnClickListener(this@HomeActivitykt)
        pause_download.setOnClickListener (this@HomeActivitykt)
        cancel_download.setOnClickListener(this@HomeActivitykt)

        val intent = Intent(this@HomeActivitykt,DownloadServicekt::class.java)
        startService(intent)
        bindService(intent,connection, Context.BIND_AUTO_CREATE)


    }
    override fun onClick(v: View) {
        if(downloadBinder == null){
            return
        }
        when(v.id){
            R.id.start_download -> {
                val url = "https://raw.githubusercontent.com/guolindev/eclipse/" + "master/eclipse-inst-win64.exe"
                downloadBinder!!.startDownload(url)}
            R.id.pause_download  -> downloadBinder!!.pauseDownload()
            R.id.cancel_download -> downloadBinder!!.cancelDownload()
            else ->{}
        }
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

}