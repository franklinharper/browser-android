package com.franklinharper.focusguard.android

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView

private const val LOG_TAG = "MainActivity"
private const val SEARCH_URL = "https://www.google.com/"
private const val aiUrl = "https://llama3.replicate.dev/"

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LOG_TAG, "onCreate url: ${intent.dataString}")
        val url = intent.dataString ?: SEARCH_URL
        setContentView(R.layout.activity_main)
        val webView = findViewById<WebView>(R.id.wv)
        webView.loadUrl(url)
    }
}
