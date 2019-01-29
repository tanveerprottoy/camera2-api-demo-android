package com.tanveershafeeprottoy.camera2apidemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityUtils.addFragmentOnActivity(
            supportFragmentManager,
            PreviewFragment.newInstance(), R.id.activityMainFrame
        )
    }
}
