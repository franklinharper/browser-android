package com.franklinharper.browser.android

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import com.google.android.material.bottomsheet.BottomSheetDialog


private const val LOG_TAG = "MainActivity"

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
}

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")
        setContentView(R.layout.activity_main)

        val gestureListener = object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d(LOG_TAG, " onDoubleTap motionEvent: $e")
                showBottomSheet()
                return true
            }
        }

        gestureDetector = GestureDetector(this, gestureListener)

        webView = findViewById<WebView>(R.id.wv)
        initWebView()
        webView.setOnTouchListener { view, event ->
//            Log.d(LOG_TAG, "webView OnTouchListener")
            if (gestureDetector.onTouchEvent(event)) {
                false
            } else {
                super.onTouchEvent(event)
            }
        }

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
        val url = intent?.dataString
        Log.d(LOG_TAG, "onCreate url: $url")
        if (url == null) {
            showBottomSheet()
        } else {
            webView.loadUrl(url)
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
