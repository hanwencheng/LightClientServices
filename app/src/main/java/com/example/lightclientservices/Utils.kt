package com.example.lightclientservices

import android.os.Environment
import android.util.Log
import java.io.File

const val TAG = "MainActivity";

const val binarySubPath = "substrate"
const val dataSubPath = "substrateData"
const val downloadSubPath = "binary/substrate"

class Utils {
    companion object {
        fun isExternalStorageWritable(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        fun isExternalStorageReadable(): Boolean {
            return Environment.getExternalStorageState() in
                    setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
        }

        //Deprecated
        fun getDownloadProcess(path: String): Process {
            val file = File(path)
            val setReadableSuccess = file.setReadable(true);
            val setExecutableSuccess = file.setExecutable(true)
            Log.i(TAG, "file executable " + file.canExecute() + "success : $setExecutableSuccess and readable $setReadableSuccess")
            Log.i(TAG, "service start with defined path: $path")
            val process = Runtime.getRuntime().let {
                it.exec("chomod 777 $path")
                it.exec("ls $path")
            }
            return process
        }

        fun setPermissions(path: String) {
            val file = File(path);
            val setReadableSuccess = file.setReadable(true);
            val setWritableSuccess = file.setWritable(true);
            val setExecutableSuccess = file.setExecutable(true)
            Log.i(TAG, "file $path permission: executable $setExecutableSuccess, writable : $setWritableSuccess, readable: $setReadableSuccess")
        }
    }
}