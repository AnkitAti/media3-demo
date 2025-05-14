package com.ankit.media3.sample.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ankit.media3.sample.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val controller = viewModel.controllerFlow.collectAsState()
    val isPlaying by remember(controller.value?.isPlaying) { derivedStateOf { controller.value?.isPlaying == true } }
    MaterialTheme {
       Scaffold(
           modifier = modifier,
           topBar = { TopAppBar(title = { Text("Sample media application") }) },
           bottomBar = { BottomAppBar { Text("Copyright Google") } }
       ) { contentPadding ->
           var text by remember { mutableStateOf("Controller empty") }
           Box(modifier = Modifier.padding(contentPadding)) {
               Surface(modifier = Modifier.fillMaxSize()) {
                   Column(
                       modifier = Modifier.fillMaxSize(),
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.SpaceEvenly
                   ) {
                       Button(
                           onClick = { viewModel.fetchController() }
                       ) {
                           Text("Fetch controller")
                       }

                       ElevatedButton(
                           onClick = { viewModel.togglePlayPause() },
                           modifier = Modifier.size(128.dp).padding(ButtonDefaults.ContentPadding),
                           enabled = controller.value != null,
                           colors = ButtonDefaults.filledTonalButtonColors()
                       ) {
                           if (isPlaying) {
                               Icon(
                                   imageVector = Icons.Default.Close,
                                   contentDescription = "pause",
                                   modifier = Modifier.fillMaxSize())
                           } else {
                               Icon(
                                   imageVector = Icons.Default.PlayArrow,
                                   contentDescription = "play",
                                   modifier = Modifier.fillMaxSize()
                               )
                           }
                       }
                   }
               }
           }
           LaunchedEffect(controller.value) {
               if (controller.value != null) {
                   text = "Controller created"
               }
           }
       }
    }
}