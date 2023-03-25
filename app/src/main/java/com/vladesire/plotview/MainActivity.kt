package com.vladesire.plotview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.restoreButton).setOnClickListener {
            findViewById<PlotView>(R.id.plotView).restore()
        }
    }
}