package com.whr.ktxpermissions

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import java.lang.Exception
import androidx.annotation.RequiresApi
import android.app.Activity


/**
 * 自定义fragment用于内部处理请求权限的返回
 */
class KtxPermissionFragment : Fragment() {
    private val REQUEST_CODE_PERMISSIONS = 0x01001

    var unPermissions = ArrayList<String>()
    /**
     * 所有權限請求都被允許了
     */
    private lateinit var allGrantBlock: (Boolean) -> Unit
    /**
     * 未被允許的權限
     */
    private lateinit var unGrantArrBlock: (MutableList<String>) -> Unit

    var isToSetting: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    /**
     * Android6.0版本请求权限
     */

    @TargetApi(Build.VERSION_CODES.M)
    internal fun requestPermissions(
        @NonNull permissions: Array<String>,
        isToSetting: Boolean,
        allGrantBlock: (isAllow: Boolean) -> Unit,
        unGrantArrBlock: (permissions: MutableList<String>) -> Unit
    ) {
        this.allGrantBlock = allGrantBlock
        this.unGrantArrBlock = unGrantArrBlock
        this.isToSetting = isToSetting
        requestPermissions(permissions, REQUEST_CODE_PERMISSIONS)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_PERMISSIONS) return
        //用于装载当前权限是否被禁止的数组
        var permissionArr = ArrayList<Permission>()
        for (i in permissions.indices) {
            var permission = Permission()
            permission.name = permissions[i]
            permission.granted = grantResults[i] == PackageManager.PERMISSION_GRANTED
            permission.shouldShowRequestPermission =
                shouldShowRequestPermissionRationale(permissions[i])
            permissionArr.add(permission)
        }
        fragRequestPermissionsResult(permissionArr)
    }

    fun fragRequestPermissionsResult(
        permissions: ArrayList<Permission>
    ) {
        permissions.forEach {
            when {
                !it.granted -> {
                    it.name?.let { it1 -> unPermissions.add(it1) }
                }
            }
        }
        //表示當前權限被有被禁止
        if (unPermissions.size > 0) {
            allGrantBlock(false)
            unGrantArrBlock(unPermissions)
            //是否跳转到设置中设置
            if (isToSetting) {
                startPermissionSetting(context!!)
            }
        } else {
            allGrantBlock(true)
        }
    }


    /**
     * 跳转到权限设置界面
     */
    fun startPermissionSetting(context: Context) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(Uri.fromParts("package", context.getPackageName(), null))
        try {
            context.startActivity(intent)
        } catch (exception: Exception) {
            throw exception
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun isHasInstallPermissionWithO(context: Context?): Boolean {
        return context?.packageManager?.canRequestPackageInstalls() ?: false
    }

    /**
     * 开启设置安装未知来源应用权限界面
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity(context: Context?) {
        if (context == null) {
            return
        }
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.setData(Uri.fromParts("package", context.getPackageName(), null))
        (context as Activity).startActivity(intent)
    }
}