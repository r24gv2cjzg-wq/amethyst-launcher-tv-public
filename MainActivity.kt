package com.amethyst.launcher

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(12, 14, 23)
    private val panel = Color.rgb(27, 31, 47)
    private val accent = Color.rgb(155, 119, 255)
    private lateinit var root: LinearLayout
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        buildUi()
    }

    private fun buildUi() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(42, 28, 42, 24)
            setBackgroundColor(bg)
            isFocusableInTouchMode = true
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(label("AMETHYST", 25, accent, true), LinearLayout.LayoutParams(0, -2, 1f))
        status = label("", 15, Color.LTGRAY, false)
        header.addView(status)
        root.addView(header)

        root.addView(label("YOUR ENTERTAINMENT HUB", 12, Color.GRAY, true).apply {
            setPadding(0, 8, 0, 20)
        })

        root.addView(label("Applications", 21, Color.WHITE, true))
        val scroller = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            setPadding(0, 14, 0, 22)
        }
        val appRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        loadApps().forEach { app ->
            val tile = TextView(this).apply {
                text = app.label
                textSize = 16f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setPadding(18, 18, 18, 18)
                minWidth = 150
                minHeight = 104
                isFocusable = true
                isFocusableInTouchMode = true
                background = rounded(panel, 18)
                setOnClickListener { app.launchIntent?.let { startActivity(it) } }
                setOnFocusChangeListener { v, focused ->
                    v.background = rounded(if (focused) accent else panel, 18)
                    if (focused) (v as TextView).setTextColor(Color.BLACK)
                    else (v as TextView).setTextColor(Color.WHITE)
                }
            }
            val lp = LinearLayout.LayoutParams(-2, -2)
            lp.marginEnd = 14
            appRow.addView(tile, lp)
        }
        scroller.addView(appRow)
        root.addView(scroller)

        root.addView(label("Quick access", 21, Color.WHITE, true))
        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actions.addView(actionTile("Settings") { openSettings() })
        actions.addView(actionTile("Wi-Fi") { openWifiSettings() })
        actions.addView(actionTile("Refresh") { buildUi() })
        root.addView(actions)

        root.addView(label("AMETHYST LAUNCHER  •  Lightweight edition", 11, Color.GRAY, false).apply {
            setPadding(0, 24, 0, 0)
        })
        setContentView(root)
        updateStatus()
        root.requestFocus()
    }

    private data class AppEntry(val label: String, val launchIntent: Intent?)

    @Suppress("DEPRECATION")
    private fun loadApps(): List<AppEntry> {
        val pm = packageManager
        val intents = listOf(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER),
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        )
        val found = linkedMapOf<String, AppEntry>()
        intents.forEach { intent ->
            val list = if (android.os.Build.VERSION.SDK_INT >= 33)
                pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
            else pm.queryIntentActivities(intent, 0)
            list.forEach { info ->
                val pkg = info.activityInfo.packageName
                if (pkg != packageName && !found.containsKey(pkg)) {
                    val launch = pm.getLaunchIntentForPackage(pkg)
                    found[pkg] = AppEntry(info.loadLabel(pm).toString(), launch)
                }
            }
        }
        return found.values.sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    private fun actionTile(text: String, action: () -> Unit): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(20, 18, 20, 18)
            isFocusable = true
            background = rounded(panel, 16)
            val lp = LinearLayout.LayoutParams(0, 72, 1f)
            lp.marginEnd = 12
            layoutParams = lp
            setOnClickListener { action() }
            setOnFocusChangeListener { v, focused ->
                v.background = rounded(if (focused) accent else panel, 16)
                setTextColor(if (focused) Color.BLACK else Color.WHITE)
            }
        }
    }

    private fun label(text: String, size: Int, color: Int, bold: Boolean): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = radius.toFloat()
    }

    private fun openSettings() {
        try { startActivity(Intent(Settings.ACTION_SETTINGS)) }
        catch (_: Exception) { startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS)) }
    }

    private fun openWifiSettings() {
        try { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
        catch (_: Exception) { openSettings() }
    }

    private fun updateStatus() {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = cm.getNetworkCapabilities(network)
        val connected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val date = SimpleDateFormat("EEE, d MMM  •  HH:mm", Locale.getDefault()).format(Date())
        status.text = "$date    •    " + if (connected) "NETWORK ONLINE" else "OFFLINE"
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) updateStatus()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) { openSettings(); return true }
        return super.onKeyDown(keyCode, event)
    }
}
