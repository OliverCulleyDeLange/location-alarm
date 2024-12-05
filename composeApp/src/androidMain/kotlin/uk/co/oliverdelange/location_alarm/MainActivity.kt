package uk.co.oliverdelange.location_alarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.viewmodel.ext.android.viewModel
import uk.co.oliverdelange.location_alarm.screens.App
import uk.co.oliverdelange.location_alarm.screens.MapUiViewModel

class MainActivity : ComponentActivity() {

    private val appViewModel: MapUiViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        setContent {
            App(appViewModel)
        }
        super.onCreate(savedInstanceState)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}