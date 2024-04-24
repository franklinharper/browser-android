package com.franklinharper.browser.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.addCallback


private const val LOG_TAG = "MainActivity"
private const val SEARCH_URL = "https://www.google.com/"
private const val aiUrl = "https://llama3.replicate.dev/"

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LOG_TAG, "onCreate")
        setContentView(R.layout.activity_main)
        webView = findViewById<WebView>(R.id.wv)
        initWebView()
        val url = intent?.dataString ?: SEARCH_URL
        Log.d(LOG_TAG, "onCreate url: $url")

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
        webView.loadUrl(url)
    }

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
                Log.d(LOG_TAG, "shouldOverrideUrlLoading")
                Log.d(LOG_TAG, "======== view ================")
                Log.d(LOG_TAG, "view.url: ${view.url}")
                Log.d(LOG_TAG, "view.originalUrl: ${view.originalUrl}")
                Log.d(LOG_TAG, "======== request ================")
                Log.d(LOG_TAG, "request.url: ${request.url}")
                Log.d(LOG_TAG, "request.isRedirect: ${request.isRedirect}")
                Log.d(LOG_TAG, "request.isForMainFrame: ${request.isForMainFrame}")
                Log.d(LOG_TAG, "request.requestHeaders: ${request.requestHeaders}")
                val uri = request.url
                val isHttp = uri.scheme == "http"
                        || uri.scheme == "https"
                Log.d(LOG_TAG, "isHttp: $isHttp")
//                val loadUrl = request.isForMainFrame && isHttp
                return when {
                    isHttp -> {
                        Log.d(LOG_TAG, "loadUrl: $uri")
                        false
                    }

                    uri.host == "play.google.com" -> {
                        Log.d(LOG_TAG, "Start Play Store: $uri")
                        val id = uri.getQueryParameter("id")
                        Log.d(LOG_TAG, "id: $id")
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(
                                "https://play.google.com/store/apps/details?id=$id"
                            )
                            setPackage("com.android.vending")
                        }
                        startActivity(intent)
                        // The Play Store opens.
                        // But seeing the Play Store **web** page in "Recents" feels weird.
                        // Solution => go back to the previous web page.
                        onBackPressedDispatcher.onBackPressed()
                        return true
                    }

                    else -> {
                        Log.d(LOG_TAG, "unknown uri: $uri")
                        false
                    }
                }
            }
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        val url = intent?.dataString ?: SEARCH_URL
//        Log.d(LOG_TAG, "onNewIntent url: $url")
//        webView.loadUrl(url)
//    }

}
