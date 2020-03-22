package com.example.lightclientservices

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "Activity Start with external storage writable:" + Utils.isExternalStorageWritable() + "readable:" + Utils.isExternalStorageReadable())
        val downloadStateReceiver = DownloadStateReceiver()
        val statusIntentFilter = IntentFilter("lightClientResponse")
        statusIntentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        LocalBroadcastManager.getInstance(this).also {
            it.registerReceiver(downloadStateReceiver, statusIntentFilter)
        }

    }

    fun startService(view: View){
        Intent(this, LightClientServices::class.java).also { intent ->
            startService(intent)
        }
    }

    fun runService(view: View){
        Intent(this, LightClientServices::class.java).also {
            it.putExtra("runBinary", "that is the value")
            startService(it);
        }
    }

    fun downloadBinary(view: View) {
        Intent(this, DownLoadServices::class.java).also { intent ->
            startService(intent)
        }
    }
}

// Broadcast receiver for receiving status updates from the IntentService.
public class DownloadStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Listen to download success event.
        if(intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
            val q = DownloadManager.Query();
            val downloadedId: Long = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            Log.e(TAG, "download id is $downloadedId")
            q.setFilterById(downloadedId);
            val manager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val c: Cursor = manager.query(q)
            if (c.moveToFirst()) {
                val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) { // do any thing here
                     manager.getUriForDownloadedFile(downloadedId).path?.let {
                        val downloadedUri = it;
                        Log.e(TAG, "download success $downloadedUri")
                    }
                }
            }
            c.close();

            // Transfer it to internal storage.
            val savedPath = context.getExternalFilesDir(null).toString() + "/" + downloadSubPath
            if(savedPath !== "") {
                val inStream = FileInputStream(savedPath)
                val internalPath = context.filesDir.path + "/" + binarySubPath
                Log.e(TAG, "now transferring to internal  storage: $internalPath")
                val outStream = FileOutputStream(internalPath)
                val inChannel: FileChannel = inStream.channel
                val outChannel: FileChannel = outStream.channel
                inChannel.transferTo(0, inChannel.size(), outChannel)
                inStream.close()
                outStream.close()
            }
        }
        Log.e(TAG, "result: " + intent.getStringExtra("intent extra"))
    }

}
