package com.example.lightclientservices

import android.app.DownloadManager
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log


private const val BINARY_LINK = "https://parity.s3.eu-central-1.amazonaws.com/substrate-2020-03-18"

class DownLoadServices: IntentService("DownloadServices") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            val uri: Uri = Uri.parse(BINARY_LINK) // Path where you want to download file.

            val request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI) // Tell on which network you want to download file.
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // This will show notification on top when downloading the file.
            request.setMimeType("application/octet-stream")
            request.setTitle("Downloading binary") // Title for notification.
            request.allowScanningByMediaScanner()
            // save into app-specific storage, which do not have executable permission.
            request.setDestinationInExternalFilesDir(
                this,
                null,
                downloadSubPath
            )
            val fileUri = (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).let{
                val id = it.enqueue(request)
                it.getUriForDownloadedFile(id);
            }
            Log.e(TAG, "start download with data uri:$fileUri")
        }catch  (e: InterruptedException) {
            // Restore interrupt status.
            Thread.currentThread().interrupt()
        }
    }
}