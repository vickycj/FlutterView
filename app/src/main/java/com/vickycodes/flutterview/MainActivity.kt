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
import androidx.core.content.ContextCompat.startActivity
import io.flutter.embedding.android.FlutterActivity;
import com.vickycodes.flutterview.ui.theme.FlutterViewTheme
import io.flutter.FlutterInjector
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor

class MainActivity : ComponentActivity() {

    private var flutterView: FlutterView? = null

    private var flutterViewEngine: FlutterViewEngine? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val engine = FlutterEngine(applicationContext)
        engine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "main"))

        flutterViewEngine = FlutterViewEngine(engine)
        // The activity and FlutterView have different lifecycles.
        // Attach the activity right away but only start rendering when the
        // view is also scrolled into the screen.
        flutterViewEngine?.attachToActivity(this)

        flutterView = FlutterView(this)
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

    override fun onDestroy() {
        super.onDestroy()
        flutterViewEngine?.detachActivity()
    }

    // These below aren't used here in this demo but would be needed for Flutter plugins that may
    // consume these events.

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        flutterViewEngine?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        flutterViewEngine?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onUserLeaveHint() {
        flutterViewEngine?.onUserLeaveHint()
        super.onUserLeaveHint()
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

