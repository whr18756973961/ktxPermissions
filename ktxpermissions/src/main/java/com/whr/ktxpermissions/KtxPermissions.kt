package com.whr.ktxpermissions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.lang.NullPointerException

class KtxPermissions private constructor() {
    internal val TAG = "KtxPermissions"
    private var mActivity: FragmentActivity? = null
    /**
     * 当权限弹窗被禁止时
     */
    private var isToSetting = false
    /**
     *自定义一个ktxPermissionFragment是为了监听权限请求的返回值
     */
    private var ktxPermissionFragment: KtxPermissionFragment? = null

    constructor(activity: FragmentActivity) : this() {
        this.mActivity = activity
        ktxPermissionFragment = getKtxPermissionFragment(activity)
    }

    constructor(fragment: Fragment) : this() {
        mActivity = fragment.activity
    }

    fun toSetting(isToSetting: Boolean): KtxPermissions {
        this.isToSetting = isToSetting
        return this
    }

    /**
     *调用ktxFragment中方法回去请求权限
     */
    fun requestPermissions(
        permissions: Array<String>,
        allGrantBlock: (isAllow: Boolean) -> Unit,
        unGrantArrBlock: (permissions: MutableList<String>) -> Unit
    ) {
        if (ktxPermissionFragment == null) {
            throw NullPointerException("ktxPermissionFragment is null") as Throwable
        } else
            ktxPermissionFragment!!.requestPermissions(
                permissions,
                isToSetting,
                allGrantBlock,
                unGrantArrBlock
            )
    }

    private fun getKtxPermissionFragment(activity: FragmentActivity): KtxPermissionFragment {
        var fragment =
            activity.supportFragmentManager.findFragmentByTag(TAG)
        var ktxFragment: KtxPermissionFragment
        if (fragment == null) {
            ktxFragment = KtxPermissionFragment()
            val fragmentManager = activity.supportFragmentManager
            fragmentManager
                .beginTransaction()
                .add(ktxFragment, TAG)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        } else {
            ktxFragment = fragment as KtxPermissionFragment
        }
        return ktxFragment
    }

}