package com.franklinharper.focusguard.android

import android.content.Intent
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
                val loadUrl = request.isForMainFrame && isHttp
                if (loadUrl) {
                    Log.d(LOG_TAG, "loadUrl: $uri")
                    return false
//                    view.loadUrl(uri.toString())
//                    return true
                } else {
                    Log.d(LOG_TAG, "Start Activity: $uri")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                    return true
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
