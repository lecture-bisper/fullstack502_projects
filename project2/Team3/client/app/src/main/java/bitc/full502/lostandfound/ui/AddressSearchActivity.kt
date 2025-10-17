package bitc.full502.lostandfound.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bitc.full502.lostandfound.databinding.ActivityAddressSearchBinding
import bitc.full502.lostandfound.util.Constants

class AddressSearchActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddressSearchBinding.inflate(layoutInflater) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.addJavascriptInterface(WebAppInterface(this), "Android")
        binding.webView.loadUrl(Constants.POST_CODE_URL)
    }

    class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun receiveAddress(zonecode: String, road: String, jibun: String) {
            val intent = Intent().apply {
                putExtra("zonecode", zonecode)
                putExtra("roadAddress", road)
                putExtra("jibunAddress", jibun)
            }
            if (context is Activity) {
                context.setResult(RESULT_OK, intent)
                context.finish()
            }
        }
    }
}