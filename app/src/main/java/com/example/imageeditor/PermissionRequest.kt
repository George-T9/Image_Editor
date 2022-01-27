package com.example.imageeditor

import android.Manifest
import android.content.Context
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

fun PermissionRequest(context: Context): Boolean {

    val permissionRequests = mutableListOf<String>()
    permissionRequests.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    permissionRequests.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    permissionRequests.add(Manifest.permission.CAMERA)
    var permissionGranted = false

    Dexter.withContext(context).withPermissions(permissionRequests)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                permissionGranted = false
            }

            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report != null) {
                    if (report.areAllPermissionsGranted()) {
                        permissionGranted = true
                    }
                }
            }
        }).check()
    return permissionGranted
}