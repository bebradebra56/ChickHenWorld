package com.chikegam.henwoldir.fergok.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.chikegam.henwoldir.fergok.presentation.app.ChickHenWorldApp

class ChickHenWorldVi(
    private val chickHenWorldContext: Context,
    private val chickHenWorldCallback: ChickHenWorldCallBack,
    private val chickHenWorldWindow: Window
) : WebView(chickHenWorldContext) {
    private var chickHenWorldFileChooserHandler: ((ValueCallback<Array<Uri>>?) -> Unit)? = null
    fun chickHenWorldSetFileChooserHandler(handler: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.chickHenWorldFileChooserHandler = handler
    }
    init {
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(chickHenWorldContext).replace("; wv)", "").replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        isNestedScrollingEnabled = true



        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val link = request?.url?.toString() ?: ""

                return if (request?.isRedirect == true) {
                    view?.loadUrl(request?.url.toString())
                    true
                }
                else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        chickHenWorldContext.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(chickHenWorldContext, "This application not found", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                if (url?.contains("ninecasino") == true) {
                    ChickHenWorldApp.chickHenWorldInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "onPageFinished : ${ChickHenWorldApp.chickHenWorldInputMode}")
                    chickHenWorldWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    ChickHenWorldApp.chickHenWorldInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "onPageFinished : ${ChickHenWorldApp.chickHenWorldInputMode}")
                    chickHenWorldWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?,
            ): Boolean {
                chickHenWorldFileChooserHandler?.invoke(filePathCallback)
                return true
            }
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                chickHenWorldHandleCreateWebWindowRequest(resultMsg)
                return true
            }
        })
    }


    fun chickHenWorldFLoad(link: String) {
        super.loadUrl(link)
    }

    private fun chickHenWorldHandleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            val windowWebView = ChickHenWorldVi(chickHenWorldContext, chickHenWorldCallback, chickHenWorldWindow)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            chickHenWorldCallback.chickHenWorldHandleCreateWebWindowRequest(windowWebView)
        }
    }

}