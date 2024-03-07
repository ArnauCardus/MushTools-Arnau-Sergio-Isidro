package com.example.mushtools.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tiempo()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Tiempo() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL(
                    null,
                    """<div id="TT6903ca5"></div>
                       <script type="text/javascript" src="https://www.tutiempo.net/s-widget/full/6903/ca5/100"></script>""",
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }
    )
}
