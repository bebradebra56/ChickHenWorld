package com.chikegam.henwoldir.fergok.data.utils

import android.util.Log
import com.chikegam.henwoldir.fergok.presentation.app.ChickHenWorldApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChickHenWorldPushToken {

    suspend fun chickHenWorldGetToken(
        chickHenWorldMaxAttempts: Int = 3,
        chickHenWorldDelayMs: Long = 1500
    ): String {

        repeat(chickHenWorldMaxAttempts - 1) {
            try {
                val chickHenWorldToken = FirebaseMessaging.getInstance().token.await()
                return chickHenWorldToken
            } catch (e: Exception) {
                Log.e(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(chickHenWorldDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}