package com.whr.ktxpermission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.whr.ktxpermissions.KtxPermissions

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        KtxPermissions(this).toSetting(false).requestPermissions(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
                ),
            {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }, {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            })
    }
}
