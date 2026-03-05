package de.bitcoindashboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.bitcoindashboard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge (Snapdragon 8 Punch-hole & curved displays)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.parseColor("#070a0f")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView

        setupEdgeToEdge()
        setupWebView()
        setupSwipeRefresh()
        setupBackNavigation()

        // Lade lokale HTML (Assets) – kein Server nötig
        webView.loadUrl("file:///android_asset/index.html")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            // JavaScript für Charts & API-Calls
            javaScriptEnabled = true

            // DOM Storage für State-Management
            domStorageEnabled = true

            // Cache-Strategie: nutze Cache, lade bei Bedarf nach
            cacheMode = WebSettings.LOAD_DEFAULT

            // Snapdragon 8 Adreno GPU: Hardware-Acceleration via WebGL
            setRenderPriority(WebSettings.RenderPriority.HIGH)

            // Zoom deaktivieren (Native-App-Feeling)
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false

            // Responsive Viewport
            useWideViewPort = true
            loadWithOverviewMode = true

            // Mixed Content (für CORS-Proxies)
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            // Encoding
            defaultTextEncodingName = "UTF-8"

            // Media
            mediaPlaybackRequiresUserGesture = false

            // User-Agent anpassen (für APIs)
            userAgentString = "BitcoinDashboard/1.0 (Android; Snapdragon8) ${userAgentString}"
        }

        // Hardware-Beschleunigung erzwingen (Adreno GPU)
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        // Hintergrundfarbe = App-Hintergrund (kein weißes Flackern beim Laden)
        webView.setBackgroundColor(Color.parseColor("#070a0f"))

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.swipeRefreshLayout.isRefreshing = false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                // Nur bei Haupt-Frame Fehler anzeigen
                if (request?.isForMainFrame == true) {
                    if (!isNetworkAvailable()) {
                        Toast.makeText(
                            this@MainActivity,
                            "⚠️ Keine Internetverbindung – Live-Daten nicht verfügbar",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            // Externe Links im Browser öffnen
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                return if (url.startsWith("file://")) {
                    false // Lokale Assets normal laden
                } else {
                    // Externe URLs (Blocktrainer News etc.) im Browser öffnen
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(url)
                    )
                    startActivity(intent)
                    true
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
                // Debug-Logs im Logcat sichtbar machen
                if (BuildConfig.DEBUG) {
                    android.util.Log.d(
                        "WebView",
                        "${message?.message()} -- Zeile ${message?.lineNumber()} von ${message?.sourceId()}"
                    )
                }
                return true
            }
        }

        // JavaScript Bridge: Native Android Funktionen aus dem WebView aufrufen
        webView.addJavascriptInterface(AndroidBridge(this), "Android")
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(Color.parseColor("#f7931a")) // Bitcoin Orange
            setProgressBackgroundColorSchemeColor(Color.parseColor("#0c1119"))
            setOnRefreshListener {
                webView.reload()
            }
        }
    }

    private fun setupEdgeToEdge() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        // Seite neu laden wenn App in den Vordergrund kommt (frische Kurse)
        webView.evaluateJavascript("if(typeof load === 'function') load(window._currentTf || '1');", null)
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.apply {
            loadUrl("about:blank")
            clearHistory()
            removeAllViews()
            destroy()
        }
    }
}

/**
 * JavaScript Bridge – ermöglicht Aufrufe von der WebApp in den nativen Android-Layer
 * Zum Beispiel: window.Android.showToast("Kurs kopiert!")
 */
class AndroidBridge(private val context: Context) {

    @JavascriptInterface
    fun showToast(message: String) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
            as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Bitcoin Preis", text)
        clipboard.setPrimaryClip(clip)
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, "📋 Kopiert: $text", Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    fun isAndroid(): Boolean = true

    @JavascriptInterface
    fun getDeviceInfo(): String = "Snapdragon 8 | Android ${android.os.Build.VERSION.RELEASE}"
}
