package com.chikegam.henwoldir.fergok.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.chikegam.henwoldir.fergok.presentation.di.chickHenWorldModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface ChickHenWorldAppsFlyerState {
    data object ChickHenWorldDefault : ChickHenWorldAppsFlyerState
    data class ChickHenWorldSuccess(val chickHenWorldData: MutableMap<String, Any>?) :
        ChickHenWorldAppsFlyerState
    data object ChickHenWorldError : ChickHenWorldAppsFlyerState
}
interface ChickHenWorldAppsApi {
    @Headers("Content-Type: application/json")
    @GET(CHICK_HEN_WORLD_LIN)
    fun chickHenWorldGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val CHICK_HEN_WORLD_APP_DEV = "mJXzVWA7GHVJ3gszkgUuWC"
private const val CHICK_HEN_WORLD_LIN = "com.chikegam.henwoldir"
class ChickHenWorldApp : Application() {
    private var chickHenWorldIsResumed = false
    private var chickHenWorldConversionTimeoutJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        chickHenWorldSetDebufLogger(appsflyer)
        chickHenWorldMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when(p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        Log.d(CHICK_HEN_WORLD_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }
                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(CHICK_HEN_WORLD_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }
                    DeepLinkResult.Status.ERROR -> {
                        Log.d(CHICK_HEN_WORLD_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })

        appsflyer.init(
            CHICK_HEN_WORLD_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    chickHenWorldConversionTimeoutJob?.cancel()
                    Log.d(CHICK_HEN_WORLD_MAIN_TAG, "onConversionDataSuccess: $p0")
                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = chickHenWorldGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.chickHenWorldGetClient(
                                    devkey = CHICK_HEN_WORLD_APP_DEV,
                                    deviceId = chickHenWorldGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(CHICK_HEN_WORLD_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    chickHenWorldSafeResume(ChickHenWorldAppsFlyerState.ChickHenWorldSuccess(p0))
                                } else {
                                    chickHenWorldSafeResume(ChickHenWorldAppsFlyerState.ChickHenWorldSuccess(resp))
                                }
                            } catch (d: Exception) {
                                Log.d(CHICK_HEN_WORLD_MAIN_TAG, "Error: ${d.message}")
                                chickHenWorldSafeResume(ChickHenWorldAppsFlyerState.ChickHenWorldError)
                            }
                        }
                    } else {
                        chickHenWorldSafeResume(ChickHenWorldAppsFlyerState.ChickHenWorldSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    chickHenWorldConversionTimeoutJob?.cancel()
                    Log.d(CHICK_HEN_WORLD_MAIN_TAG, "onConversionDataFail: $p0")

                    chickHenWorldSafeResume(ChickHenWorldAppsFlyerState.ChickHenWorldError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(CHICK_HEN_WORLD_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(CHICK_HEN_WORLD_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )
        appsflyer.start(this, CHICK_HEN_WORLD_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(CHICK_HEN_WORLD_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(CHICK_HEN_WORLD_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
//                todoSafeResume(ChickHenWorldAppsFlyerState.ChickHenWorldError)
            }
        })
        chickHenWorldStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ChickHenWorldApp)
            modules(
                listOf(
                    chickHenWorldModule
                )
            )
        }
    }
    private fun chickHenWorldStartConversionTimeout() {
        chickHenWorldConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!chickHenWorldIsResumed) {
                Log.d(CHICK_HEN_WORLD_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                chickHenWorldSafeResume(ChickHenWorldAppsFlyerState.ChickHenWorldError)
            }
        }
    }

    private fun chickHenWorldSafeResume(state: ChickHenWorldAppsFlyerState) {
        chickHenWorldConversionTimeoutJob?.cancel()
        if (!chickHenWorldIsResumed) {
            chickHenWorldIsResumed = true
            chickHenWorldConversionFlow.value = state
        }
    }

    private fun chickHenWorldGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(CHICK_HEN_WORLD_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun chickHenWorldSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun chickHenWorldMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun chickHenWorldGetApi(url: String, client: OkHttpClient?) : ChickHenWorldAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var chickHenWorldInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val chickHenWorldConversionFlow: MutableStateFlow<ChickHenWorldAppsFlyerState> = MutableStateFlow(
            ChickHenWorldAppsFlyerState.ChickHenWorldDefault
        )
        var CHICK_HEN_WORLD_FB_LI: String? = null
        const val CHICK_HEN_WORLD_MAIN_TAG = "ChickHenWorldMainTag"
    }
}