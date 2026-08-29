package com.example.toggleloc

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var statusBar: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 0)
        }

        // ===== 顶部状态栏 =====
        statusBar = TextView(this).apply {
            text = "  定位状态：读取中..."
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            setPadding(40, 40, 40, 40)
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        root.addView(statusBar)

        // ===== 中间留白 + 圆形按钮 =====
        val spacer = TextView(this).apply {
            text = ""
            setPadding(0, 120, 0, 0)
        }
        root.addView(spacer)

        button = Button(this).apply {
            text = "切 换"
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            setOnClickListener {
                toggleAndRefresh()
            }
        }

        val btnParams = LinearLayout.LayoutParams(300, 300).apply {
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }
        button.layoutParams = btnParams

        button.background = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.OVAL
            setColor(Color.parseColor("#2196F3"))
        }

        root.addView(button)
        setContentView(root)

        refreshStatus()
    }

    private fun toggleAndRefresh() {
        try {
            val current = runSu("settings get secure location_mode").trim()
            val newMode = if (current == "3") "0" else "3"

            val process = Runtime.getRuntime().exec("su -c settings put secure location_mode $newMode")
            val exitCode = process.waitFor()

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

        refreshStatus()
    }

    private fun refreshStatus() {
        try {
            val mode = runSu("settings get secure location_mode").trim()
            if (mode == "3") {
                statusBar.text = "  ● 定位已开启"
                statusBar.setBackgroundColor(Color.parseColor("#4CAF50")) // 绿色
            } else {
                statusBar.text = "  ● 定位已关闭"
                statusBar.setBackgroundColor(Color.parseColor("#F44336")) // 红色
            }
        } catch (_: Exception) {
            statusBar.text = "  ● 定位：未知"
            statusBar.setBackgroundColor(Color.parseColor("#9E9E9E")) // 灰色
        }
    }

    private fun runSu(command: String): String {
        val process = Runtime.getRuntime().exec("su -c $command")
        val output = process.inputStream.bufferedReader().readText().trim()
        process.waitFor()
        return output
    }
}
