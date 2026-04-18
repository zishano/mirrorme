package com.mirrorme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mirrorme.ui.navigation.MirrorMeNavGraph
import com.mirrorme.ui.theme.MirrorMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MirrorMeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MirrorMeNavGraph()
                }
            }
        }
    }
}
