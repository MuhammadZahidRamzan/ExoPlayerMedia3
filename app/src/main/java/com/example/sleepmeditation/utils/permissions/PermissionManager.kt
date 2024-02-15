package com.example.sleepmeditation.utils.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.sleepmeditation.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionManager(
    private val fragment: FragmentActivity,
    private val permissionGranted: (Boolean) -> Unit
) {
    private val storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestMultiplePermissionsLauncher =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var isGranted = false
            for (permission in permissions) {
                isGranted = permission.value
            }
            permissionGranted.invoke(isGranted)
            if (!isGranted) {
                showRationalPermissionDialog()
            }
        }

    private val settingsResultLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkPermissions(storagePermissions)) {
                permissionGranted.invoke(true)
            } else {
                permissionGranted.invoke(false)
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    private val android11PlusSettingResultLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Environment.isExternalStorageManager()) {
                permissionGranted.invoke(true)
            } else {
                permissionGranted.invoke(false)
            }
        }

    fun checkStoragePermissionsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionGranted.invoke(Environment.isExternalStorageManager())
        } else {
            permissionGranted.invoke(checkPermissions(storagePermissions))
        }
    }

    fun requestReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                permissionGranted.invoke(true)
            } else {
                permissionGranted.invoke(false)
                showAndroid11PlusPermissionDialog()
            }
        } else {
            if (checkPermissions(storagePermissions)) {
                permissionGranted.invoke(true)
            } else {
                permissionGranted.invoke(false)
                requestMultiplePermissionsLauncher.launch(
                    storagePermissions
                )
            }
        }
//        if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    permissionGranted.invoke(true)
//                    return
//                }
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                showAndroid11PlusPermissionDialog()
//            } else {
//                showRationalPermissionDialog()
//            }
//            permissionGranted.invoke(false)
//        }
    }

    private fun showRationalPermissionDialog() {

        MaterialAlertDialogBuilder(fragment)
            .setTitle(fragment.getString(R.string.external_storage_permission))
            .setMessage(fragment.getString(R.string.external_storage_rationale_message))
            .setPositiveButton(fragment.getString(R.string.open_setting)) { dialog, _ ->
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", fragment.packageName, null)
                }
                dialog.dismiss()
                settingsResultLauncher.launch(intent)
            }
            .setNegativeButton(fragment.getString(R.string.close)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showAndroid11PlusPermissionDialog() {
        MaterialAlertDialogBuilder(fragment)
            .setTitle(fragment.getString(R.string.allow_access))
            .setMessage(fragment.getString(R.string.external_storage_rationale_message))
            .setPositiveButton(fragment.getString(R.string.open_setting)) { dialog, _ ->
                dialog.dismiss()
                requestManageFilesPermission()
            }
            .setNegativeButton(fragment.getString(R.string.not_now)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun requestManageFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent().apply {
                action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                data = Uri.fromParts("package", fragment.packageName, null)
            }
            android11PlusSettingResultLauncher.launch(intent)
        }

    }


    private fun checkPermissions(permissions: Array<String>): Boolean {
        var isGranted = false
        permissions.forEach {
            isGranted = ContextCompat.checkSelfPermission(
                fragment,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
        return isGranted
    }


}