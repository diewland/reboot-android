package com.diewland.rebootandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_reboot).setOnClickListener {
            // android 7.1 --- worked
            Runtime.getRuntime().exec(arrayOf("reboot"))
        }

        findViewById<Button>(R.id.btn_reboot_su).setOnClickListener {
            // android 4.4.2 --- worked
            // android 7.1 --- worked
            SuUtil.exec("reboot")
        }
    }
}