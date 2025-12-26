package com.chikegam.henwoldir.fergok.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.chikegam.henwoldir.ChickHenWorldActivity
import com.chikegam.henwoldir.fergok.presentation.app.ChickHenWorldApp
import com.chikegam.henwoldir.fergok.presentation.ui.load.ChickHenWorldLoadFragment
import org.koin.android.ext.android.inject

class ChickHenWorldV : Fragment(){

    private lateinit var chickHenWorldPhoto: Uri
    private var chickHenWorldFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val chickHenWorldTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        chickHenWorldFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        chickHenWorldFilePathFromChrome = null
    }

    private val chickHenWorldTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            chickHenWorldFilePathFromChrome?.onReceiveValue(arrayOf(chickHenWorldPhoto))
            chickHenWorldFilePathFromChrome = null
        } else {
            chickHenWorldFilePathFromChrome?.onReceiveValue(null)
            chickHenWorldFilePathFromChrome = null
        }
    }

    private val chickHenWorldDataStore by activityViewModels<ChickHenWorldDataStore>()


    private val chickHenWorldViFun by inject<ChickHenWorldViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (chickHenWorldDataStore.chickHenWorldView.canGoBack()) {
                        chickHenWorldDataStore.chickHenWorldView.goBack()
                        Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "WebView can go back")
                    } else if (chickHenWorldDataStore.chickHenWorldViList.size > 1) {
                        Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "WebView can`t go back")
                        chickHenWorldDataStore.chickHenWorldViList.removeAt(chickHenWorldDataStore.chickHenWorldViList.lastIndex)
                        Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "WebView list size ${chickHenWorldDataStore.chickHenWorldViList.size}")
                        chickHenWorldDataStore.chickHenWorldView.destroy()
                        val previousWebView = chickHenWorldDataStore.chickHenWorldViList.last()
                        chickHenWorldAttachWebViewToContainer(previousWebView)
                        chickHenWorldDataStore.chickHenWorldView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (chickHenWorldDataStore.chickHenWorldIsFirstCreate) {
            chickHenWorldDataStore.chickHenWorldIsFirstCreate = false
            chickHenWorldDataStore.chickHenWorldContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return chickHenWorldDataStore.chickHenWorldContainerView
        } else {
            return chickHenWorldDataStore.chickHenWorldContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "onViewCreated")
        if (chickHenWorldDataStore.chickHenWorldViList.isEmpty()) {
            chickHenWorldDataStore.chickHenWorldView = ChickHenWorldVi(requireContext(), object :
                ChickHenWorldCallBack {
                override fun chickHenWorldHandleCreateWebWindowRequest(chickHenWorldVi: ChickHenWorldVi) {
                    chickHenWorldDataStore.chickHenWorldViList.add(chickHenWorldVi)
                    Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "WebView list size = ${chickHenWorldDataStore.chickHenWorldViList.size}")
                    Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "CreateWebWindowRequest")
                    chickHenWorldDataStore.chickHenWorldView = chickHenWorldVi
                    chickHenWorldVi.chickHenWorldSetFileChooserHandler { callback ->
                        chickHenWorldHandleFileChooser(callback)
                    }
                    chickHenWorldAttachWebViewToContainer(chickHenWorldVi)
                }

            }, chickHenWorldWindow = requireActivity().window).apply {
                chickHenWorldSetFileChooserHandler { callback ->
                    chickHenWorldHandleFileChooser(callback)
                }
            }
            chickHenWorldDataStore.chickHenWorldView.chickHenWorldFLoad(arguments?.getString(
                ChickHenWorldLoadFragment.CHICK_HEN_WORLD_D) ?: "")
//            ejvview.fLoad("www.google.com")
            chickHenWorldDataStore.chickHenWorldViList.add(chickHenWorldDataStore.chickHenWorldView)
            chickHenWorldAttachWebViewToContainer(chickHenWorldDataStore.chickHenWorldView)
        } else {
            chickHenWorldDataStore.chickHenWorldViList.forEach { webView ->
                webView.chickHenWorldSetFileChooserHandler { callback ->
                    chickHenWorldHandleFileChooser(callback)
                }
            }
            chickHenWorldDataStore.chickHenWorldView = chickHenWorldDataStore.chickHenWorldViList.last()

            chickHenWorldAttachWebViewToContainer(chickHenWorldDataStore.chickHenWorldView)
        }
        Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "WebView list size = ${chickHenWorldDataStore.chickHenWorldViList.size}")
    }

    private fun chickHenWorldHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        chickHenWorldFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "Launching file picker")
                    chickHenWorldTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "Launching camera")
                    chickHenWorldPhoto = chickHenWorldViFun.chickHenWorldSavePhoto()
                    chickHenWorldTakePhoto.launch(chickHenWorldPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(ChickHenWorldApp.CHICK_HEN_WORLD_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                chickHenWorldFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun chickHenWorldAttachWebViewToContainer(w: ChickHenWorldVi) {
        chickHenWorldDataStore.chickHenWorldContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            chickHenWorldDataStore.chickHenWorldContainerView.removeAllViews()
            chickHenWorldDataStore.chickHenWorldContainerView.addView(w)
        }
    }



}