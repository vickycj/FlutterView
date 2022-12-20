package com.vickycodes.flutterview

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.vickycodes.flutterview.ui.theme.FlutterViewTheme
import io.flutter.FlutterInjector
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor

class MainActivity : ComponentActivity() {

    private var flutterView: FlutterView? = null
    private var flutterEngine : FlutterEngine? = null

    private var flutterViewEngine: FlutterViewEngine? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getArgsFromIntent(intent)
        flutterEngine = FlutterEngine(applicationContext, args)
        flutterEngine?.dartExecutor?.executeDartEntrypoint(
            DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "main"))


        flutterView = FlutterView(this)
        flutterView?.attachToFlutterEngine(flutterEngine!!)
        setContent {
            FlutterViewTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    flutterView?.let { FlutterViewComposable(it) }
                }
            }
        }
    }

    private fun getArgsFromIntent(intent: Intent): Array<String>? {
        // Before adding more entries to this list, consider that arbitrary
        // Android applications can generate intents with extra data and that
        // there are many security-sensitive args in the binary.
        val args = ArrayList<String>()
        if (intent.getBooleanExtra("trace-startup", false)) {
            args.add("--trace-startup")
        }
        if (intent.getBooleanExtra("start-paused", false)) {
            args.add("--start-paused")
        }
        if (intent.getBooleanExtra("enable-dart-profiling", false)) {
            args.add("--enable-dart-profiling")
        }
        if (!args.isEmpty()) {
            return args.toTypedArray()
        }
        return null
    }



    /// hire will be tested if the channel lifecycle is resumed
    override fun onResume() {
        super.onResume()
        flutterEngine!!.lifecycleChannel.appIsResumed()
    }

    // hire will be tested if the channel lifecycle is paused
    override fun onPause() {
        super.onPause()
        flutterEngine!!.lifecycleChannel.appIsInactive()
    }

    // hire will be tested if the channel lifecycle is stoped
    override fun onStop() {
        super.onStop()
        flutterEngine!!.lifecycleChannel.appIsPaused()
    }

    // hire will be tested if the channel lifecycle is destroied
    override fun onDestroy() {
        flutterView!!.detachFromFlutterEngine()
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String) {
    val context = LocalContext.current
    Button(onClick = {
        context.startActivity(
            FlutterActivity.createDefaultIntent(context)
        )
    }) {

    }
    Text(text = "Hello $name!")
}


@Composable
fun FlutterViewComposable(flutterView: FlutterView) {
    AndroidView(factory = {
        flutterView
    })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlutterViewTheme {
        Greeting("Android")
    }
}

