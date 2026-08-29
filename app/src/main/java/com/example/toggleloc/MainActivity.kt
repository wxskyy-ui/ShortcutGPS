package com.example.toggleloc

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        button = Button(this).apply {
            text = "定位：读取中..."
            textSize = 28f
            setOnClickListener {
                toggleAndRefresh()
            }
        }

        setContentView(button)
        refreshButtonText()
    }

    private fun toggleAndRefresh() {
        try {
            val current = runSu("settings get secure location_mode").trim()
            val newMode = if (current == "3") "0" else "3"
            val code = runSu("settings put secure location_mode $newMode").let { 0 }

            // 用 exit code 判断
            val putProcess = Runtime.getRuntime().exec("su -c settings put secure location_mode $newMode")
            val exitCode = putProcess.waitFor()

            if (exitCode == 0) {
                Toast.makeText(
                    this,
                    if (newMode == "3") "定位已开启" else "定位已关闭",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "切换失败，请确认已授予 Root 权限", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "错误: ${e.message}", Toast.LENGTH_LONG).show()
        }

        refreshButtonText()
    }

    private fun refreshButtonText() {
        try {
            val mode = runSu("settings get secure location_mode").trim()
            button.text = if (mode == "3") "定位：开\n点击关闭" else "定位：关\n点击开启"
        } catch (_: Exception) {
            button.text = "定位：未知\n点击切换"
        }
    }

    private fun runSu(command: String): String {
        val process = Runtime.getRuntime().exec("su -c $command")
        val output = process.inputStream.bufferedReader().readText().trim()
        process.waitFor()
        return output
    }
}
