package com.franklinharper.browser.android

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import com.google.android.material.bottomsheet.BottomSheetDialog


private const val LOG_TAG = "MainActivity"
private const val REQUEST_PERMISSION = 1
private const val FILE_CHOOSER_RESULT_CODE = 2

sealed class ShortcutButton(
    val siteName: String,
    val url: String,
    val id: Int,
) {
    data object PerplexityAi : ShortcutButton(
        id = R.id.PerplexityAiButton,
        siteName = "Perplexity AI",
        url = "https://www.perplexity.ai/",
    )

    data object ChatGptAi : ShortcutButton(
        id = R.id.ChatGptButton,
        siteName = "ChatGpt",
        url = "https://chat.openai.com/",
    )

    data object GoogleSearch : ShortcutButton(
        id = R.id.GoogleSearchButton,
        siteName = "Google",
        url = "https://www.google.com/",
    )

    data object Llama3Ai : ShortcutButton(
        id = R.id.Llama3Button,
        siteName = "Llama 3",
        url = "https://llama3.replicate.dev/",
    )

    data object LinkedIn : ShortcutButton(
        id = R.id.LinkedInButton,
        siteName = "LInkedIn Messaging",
        url = "https://www.linkedin.com/messaging",
    )
    data object ProductivitySubReddit : ShortcutButton(
        id = R.id.RedditButton,
        siteName = "Productivity SubReddit",
        url = "https://www.reddit.com/r/productivity/",
    )
}

class MainActivity : ComponentActivity() {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private lateinit var webView: WebView
    private lateinit var customViewContainer: FrameLayout

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.wv)
        customViewContainer = findViewById(R.id.customViewContainer)
        customViewContainer.visibility = View.GONE

        val gestureListener = object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d(LOG_TAG, " onDoubleTap motionEvent: $e")
                showBottomSheet()
                return true
            }
        }
        gestureDetector = GestureDetector(this, gestureListener)
        webView.setOnTouchListener { view, event ->
//            Log.d(LOG_TAG, "webView OnTouchListener")
            if (gestureDetector.onTouchEvent(event)) {
                false
            } else {
                super.onTouchEvent(event)
            }
        }
        initWebView()

        onBackPressedDispatcher.addCallback(owner = this) {
            Log.d(LOG_TAG, "onBackPressed")
            if (webView.canGoBack()) {
                Log.d(LOG_TAG, "webView.GoBack()")
                webView.goBack()
            } else {
                Log.d(LOG_TAG, "finish Activity")
                finish()
            }
        }
        if (
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(LOG_TAG, "Request permissions")
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_PERMISSION,
            )
        } else {
            Log.d(LOG_TAG, "Permissions already granted")
        }

        val url = intent?.dataString
        Log.d(LOG_TAG, "onCreate url: $url")
        if (url == null) {
            showBottomSheet()
        } else {
            webView.loadUrl(url)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        Log.d(LOG_TAG, "onActivityResult requestCode:$requestCode, resultCode:$resultCode, data:$data")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == filePathCallback) return
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (result != null) {
                filePathCallback?.onReceiveValue(arrayOf(result))
            } else {
                filePathCallback?.onReceiveValue(null)
            }
            filePathCallback = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        // This is for runtime permission on Marshmallow and above; It is not directly related to
        // the WebView PermissionRequest API.
        if (requestCode == REQUEST_PERMISSION) {
            if (permissions.size != 1 || grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.e(
                    LOG_TAG,
                    "Permission not granted."
                )
            } else {
                Log.d(
                    LOG_TAG,
                    "Permission GRANTED!"
                )
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        val url = intent?.dataString ?: SEARCH_URL
//        Log.d(LOG_TAG, "onNewIntent url: $url")
//        webView.loadUrl(url)
//    }

    private fun initWebView() {
        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            safeBrowsingEnabled = false
            mediaPlaybackRequiresUserGesture = false
            setSupportZoom(true)
            allowFileAccess = true
            allowContentAccess = true
            loadWithOverviewMode = false
            setGeolocationEnabled(true)
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.d(
                    LOG_TAG,
                    "onConsoleMessage: ${consoleMessage?.messageLevel()} ${consoleMessage?.message()}"
                )
                return true
            }


            var customViewCallback: WebChromeClient.CustomViewCallback? = null

            // onShowCustomView should be called when the webpage requests full screen mode.
            // https://developer.android.com/reference/android/webkit/WebChromeClient#onShowCustomView(android.view.View,%20android.webkit.WebChromeClient.CustomViewCallback)
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                // TODO test and debug this code
                Log.d(LOG_TAG, "onShowCustomView view: $view, callback: $callback")
                webView.visibility = View.GONE
                customViewContainer.addView(view)
                customViewCallback = callback
                customViewContainer.visibility = View.GONE
            }

            override fun onHideCustomView() {
                Log.d(LOG_TAG, "onHideCustomView")
                webView.visibility = View.VISIBLE
                customViewContainer.visibility = View.GONE
                customViewCallback?.onCustomViewHidden()
            }

            // The WebView tell the app to show a file chooser.
            // onShowFileChooser is called to handle HTML forms with 'file' input type,
            // in response to the user pressing a "Select File" button.
            //
            // onShowFileChooser handles file selection prompts initiated by
            // HTML <input type="file"> elements within a WebView.
            // Implementing this provides users the ability to upload files to websites.
            // See https://developer.android.com/reference/android/webkit/WebChromeClient#onShowFileChooser(android.webkit.WebView,%20android.webkit.ValueCallback%3Candroid.net.Uri[]%3E,%20android.webkit.WebChromeClient.FileChooserParams)
            override fun onShowFileChooser(
                webView: WebView?,
                callback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams,
            ): Boolean {
                Log.d(LOG_TAG, "onShowFileChooser")
                val fileIntent = fileChooserParams.createIntent()
                try {
                    // Start activity to select file
                    startActivityForResult(fileIntent, FILE_CHOOSER_RESULT_CODE)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        /* context = */ this@MainActivity,
                        /* text = */ "Cannot open file chooser",
                        /* duration = */ Toast.LENGTH_LONG
                    ).show()
                    // Cancel the request
                    callback.onReceiveValue(null)
                    return true
                }

                // Save the callback for later retrieval
                filePathCallback = callback
                // Returns true if filePathCallback will be invoked, or false to use default handling.
                return true
            }

            // To cancel the request, call filePathCallback.onReceiveValue(null) and return true.
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?,
            ) {
                Log.d(LOG_TAG, "onGeolocationPermissionsShowPrompt")
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                Log.d(LOG_TAG, "onPermissionRequest: $request")
                runOnUiThread(Runnable { // Check the permission request.
                    request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
//                    for (permission in request.resources) {
//                        if (PermissionRequest.RESOURCE_AUDIO_CAPTURE == permission) {
//                            request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
//                            return@Runnable
//                        }
//                    }
//                    request.deny()
                })
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest,
            ): Boolean {
                val uri = request.url

                Log.d(LOG_TAG, "shouldOverrideUrlLoading")
                Log.d(LOG_TAG, "======== view ================")
                Log.d(LOG_TAG, "view.url: ${view.url}")
                Log.d(LOG_TAG, "view.originalUrl: ${view.originalUrl}")
                Log.d(LOG_TAG, "======== request ================")
                Log.d(LOG_TAG, "request.url: ${request.url}")
                Log.d(LOG_TAG, "request.isRedirect: ${request.isRedirect}")
                Log.d(LOG_TAG, "request.isForMainFrame: ${request.isForMainFrame}")
                Log.d(LOG_TAG, "request.requestHeaders: ${request.requestHeaders}")
                Log.d(LOG_TAG, "======== Uri ================")
                Log.d(LOG_TAG, "uri.scheme: ${uri.scheme}")
                Log.d(LOG_TAG, "uri.host: ${uri.host}")
                Log.d(LOG_TAG, "uri.path: ${uri.path}")
                Log.d(LOG_TAG, "uri.query: ${uri.query}")

                val isHttp = uri.scheme == "http"
                        || uri.scheme == "https"
                Log.d(LOG_TAG, "isHttp: $isHttp")
//                val loadUrl = request.isForMainFrame && isHttp
                return when {
                    isHttp -> {
                        Log.d(LOG_TAG, "loadUrl: $uri")
                        false
                    }

                    else -> {
                        val intent = Intent.parseUri(
                            /* uri = */ request.url.toString(),
                            /* flags = */ Intent.URI_INTENT_SCHEME
                        )
                        Log.d(LOG_TAG, "starting Intent: $intent")
                        try {
                            startActivity(intent);
                            true
                        } catch (activityNotFoundException: ActivityNotFoundException) {
                            Log.d(LOG_TAG, "Start Play Store")
                            val appPackage = intent.`package`
                            Log.d(LOG_TAG, "id: $appPackage")
                            val storeIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(
                                    "https://play.google.com/store/apps/details?id=$appPackage"
                                )
                                setPackage("com.android.vending")
                            }
                            startActivity(storeIntent)
                            // The Play Store opens.
                            // But seeing the Play Store **web** page in "Recents" feels weird.
                            // Solution => go back to the previous web page when launching the Play Store.
//                            onBackPressedDispatcher.onBackPressed()
                            true
                        }
                    }
                }
            }
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet)
        configureButton(dialog, ShortcutButton.PerplexityAi) { shortcutButton ->
            webView.loadUrl(shortcutButton.url)
        }
        configureButton(dialog, ShortcutButton.ChatGptAi) { shortcutButton ->
            webView.loadUrl(shortcutButton.url)
        }
        configureButton(dialog, ShortcutButton.GoogleSearch) { shortcutButton ->
            webView.loadUrl(shortcutButton.url)
        }
        configureButton(dialog, ShortcutButton.Llama3Ai) { shortcutButton ->
            webView.loadUrl(shortcutButton.url)
        }
        configureButton(dialog, ShortcutButton.LinkedIn) { shortcutButton ->
            webView.loadUrl(shortcutButton.url)
        }
        configureButton(dialog, ShortcutButton.ProductivitySubReddit) { shortcutButton ->
            webView.loadUrl(shortcutButton.url)
        }
        dialog.findViewById<Button>(R.id.shareButton)!!.apply {
            setOnClickListener {
                val title = getString(R.string.shareTitle, webView.title)
                val shareIntent: Intent =
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, title)
                        putExtra(Intent.EXTRA_TEXT, webView.url)
                    }
                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        null
                    )
                )
                dialog.dismiss()
            }
        }
        dialog.findViewById<Button>(R.id.exitButton)!!.apply {
            setOnClickListener {
                dialog.dismiss()
                finish()
            }
        }
        dialog.show()
    }

    private fun configureButton(
        dialog: BottomSheetDialog,
        button: ShortcutButton,
        onClick: (ShortcutButton) -> Unit,
    ) {
        dialog.findViewById<Button>(button.id)!!.apply {
            text = button.siteName
            setOnClickListener {
                onClick(button)
                dialog.dismiss()
            }
        }
    }
}
