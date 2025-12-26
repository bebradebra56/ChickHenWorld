package com.chikegam.henwoldir

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.chikegam.henwoldir.fergok.ChickHenWorldGlobalLayoutUtil
import com.chikegam.henwoldir.fergok.chickHenWorldSetupSystemBars
import com.chikegam.henwoldir.fergok.presentation.app.ChickHenWorldApp
import com.chikegam.henwoldir.fergok.presentation.pushhandler.ChickHenWorldPushHandler
import org.koin.android.ext.android.inject

class ChickHenWorldActivity : AppCompatActivity() {
    private val chickHenWorldPushHandler by inject<ChickHenWorldPushHandler>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chickHenWorldSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_chick_hen_world)
        val chickHenWorldRootView = findViewById<View>(android.R.id.content)
        ChickHenWorldGlobalLayoutUtil().chickHenWorldAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(chickHenWorldRootView) { chickHenWorldView, chickHenWorldInsets ->
            val chickHenWorldSystemBars = chickHenWorldInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val chickHenWorldDisplayCutout = chickHenWorldInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val chickHenWorldIme = chickHenWorldInsets.getInsets(WindowInsetsCompat.Type.ime())


            val chickHenWorldTopPadding = maxOf(chickHenWorldSystemBars.top, chickHenWorldDisplayCutout.top)
            val chickHenWorldLeftPadding = maxOf(chickHenWorldSystemBars.left, chickHenWorldDisplayCutout.left)
            val chickHenWorldRightPadding = maxOf(chickHenWorldSystemBars.right, chickHenWorldDisplayCutout.right)
            window.setSoftInputMode(ChickHenWorldApp.chickHenWorldInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "ADJUST PUN")
                val chickHenWorldBottomInset = maxOf(chickHenWorldSystemBars.bottom, chickHenWorldDisplayCutout.bottom)

                chickHenWorldView.setPadding(chickHenWorldLeftPadding, chickHenWorldTopPadding, chickHenWorldRightPadding, 0)

                chickHenWorldView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = chickHenWorldBottomInset
                }
            } else {
                Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "ADJUST RESIZE")

                val chickHenWorldBottomInset = maxOf(chickHenWorldSystemBars.bottom, chickHenWorldDisplayCutout.bottom, chickHenWorldIme.bottom)

                chickHenWorldView.setPadding(chickHenWorldLeftPadding, chickHenWorldTopPadding, chickHenWorldRightPadding, 0)

                chickHenWorldView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = chickHenWorldBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "Activity onCreate()")
        chickHenWorldPushHandler.chickHenWorldHandlePush(intent.extras)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            chickHenWorldSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        chickHenWorldSetupSystemBars()
    }
}