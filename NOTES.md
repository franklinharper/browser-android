# Ideas

## When redirected to a custom URL scheme then open that URL with the system

Steps to reproduce

1. Open https://www.speedtest.net/
2. Click on Try the App.

Expected: Play Store opens to the correct page
Actual: ERR_UNKNOWN_URL_SCHEME

## Run and test the app on iOS

## Add debug view that shows the WebView errors for the current page

This will help with fixing bugs.

## Add javascript console view

### Android implementation

See https://developer.android.com/develop/ui/views/layout/webapps/debugging#WebView

### iOS implementation

For iOS WKWebViews:
Use a tool like WebviewConsole, which provides a separate UI window to display the console logs
from the WebView.

Example:
WebviewConsole.shared.show()
WebviewConsole.shared.trackWebview(myWebView)

## bug: can't open link

https://t.me/ah2020_org_canal/138286

It fails with this error:

The web page at tg:resolve?domain=ah2020_org_canal&post=138286 could not be loaded because:
net::ERR_UNKNOWN_URL_SCHEME

## Show/Hide the TopBar based on the user's scrolling direction

For implementation ideas => See TODO in `App.kt`

# Implementation Notes

## Logging Implementations

###  kotlin-logging

It's Kotlin logging facade built on top of slf4j. Because of this it
requires adding more dependencies: slf4j-api and slf4j-android.
Kotlin multiplatform support was added afterwards.

For more info see

https://github.com/oshai/kotlin-logging/wiki/Multiplatform-support
https://github.com/oshai/kotlin-logging

### KmLogging

This library focuses on performance. It is KMP first and only.

It requires adding the following to each class that logs.

```
    companion object {
        val log = logging()
    }
```

Implementation was easy.

For more info see

https://github.com/LighthouseGames/KmLogging


## WebView
