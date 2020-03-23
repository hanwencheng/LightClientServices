package com.example.lightclientservices

import android.app.IntentService
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class LightClientServices : IntentService("LightClientServices") {
    override fun onHandleIntent(intent: Intent?) {
        try {
            val externalDirPath = getExternalFilesDir(null).toString();
            Log.e(TAG, "app external path is$externalDirPath")
            val currentDir = this.filesDir.path
            Log.e(TAG, "current dir is $currentDir")
            Utils.setPermissions("$currentDir/$binarySubPath");
            val dir =  File("$externalDirPath/$dataSubPath");
            dir.mkdir()
            Utils.setPermissions("$externalDirPath/$dataSubPath")
            val pb =
                ProcessBuilder(".$currentDir/$binarySubPath", "--light", "-d", "$externalDirPath/$dataSubPath")
//            val pb = ProcessBuilder("ls", "-l", "$externalDirPath/$dataSubPath/chains/flamingfir6/db")
            val process = pb.start()

            // Read Ouput
            val stdInput = BufferedReader(InputStreamReader(process.inputStream))
            val stdError = BufferedReader(InputStreamReader(process.errorStream))
            var log: String? = null
            while (stdInput.readLine().also { log = it } != null) {
                Log.e(TAG, "info:$log")
            }
            while (stdError.readLine().also { log = it } != null) {
                Log.e(TAG, "error:$log")
            }
            val localIntent = Intent("lightClientResponse").apply {
                putExtra("extraKey", log.toString())
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "service started")

        return super.onStartCommand(intent, flags, startId)
    }
}
