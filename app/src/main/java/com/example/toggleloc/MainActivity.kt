package com.example.toggleloc

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var tv: TextView
    private lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.tvStatus)
        btn = findViewById(R.id.btnToggle)

        refresh()

        btn.setOnClickListener {
            toggle()
            refresh()
        }
    }

    private fun toggle() {
        // 读当前值并翻转：3(高精度) <-> 0(关)
        val cmd = "m=$(settings get secure location_mode); " +
                "if [ \"$m\" = \"3\" ]; then settings put secure location_mode 0; " +
                "else settings put secure location_mode 3; fi"
        val code = runAsRoot(cmd)
        if (code != 0) {
            Toast.makeText(this, "执行失败，请确认已授予 Root 权限", Toast.LENGTH_LONG).show()
        }
    }

    private fun refresh() {
        val mode = runAsRootRead("settings get secure location_mode").trim()
        tv.text = when (mode) {
            "3" -> "当前：高精度定位（已开）"
            "0" -> "当前：定位已关闭"
            else -> "当前：未知（$mode）"
        }
    }

    private fun runAsRootRead(cmd: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
            val out = BufferedReader(InputStreamReader(process.inputStream)).readText()
            process.waitFor()
            out
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun runAsRoot(cmd: String): Int {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}
