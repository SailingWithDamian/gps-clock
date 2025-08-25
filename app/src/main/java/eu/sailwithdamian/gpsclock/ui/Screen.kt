package eu.sailwithdamian.gpsclock.ui

import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.OnUnhandledKeyEventListenerCompat

@Composable
fun ClockScreen(
    correctedTimeText: MutableState<String>,
    infoText: MutableState<String>,
) {
    val recordedValues = remember { mutableStateListOf<String>() }
    var showDialog = remember { mutableStateOf<Boolean>(false) }

    var view = LocalView.current
    DisposableEffect(view) {
        val listener = OnUnhandledKeyEventListenerCompat { _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.action == KeyEvent.ACTION_DOWN) {
                recordedValues.add(correctedTimeText.value)
                true
            } else {
                false
            }
        }

        ViewCompat.addOnUnhandledKeyEventListener(view, listener)

        onDispose {
            ViewCompat.removeOnUnhandledKeyEventListener(view, listener)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = correctedTimeText.value,
                fontSize = 78.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = infoText.value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { recordedValues.add(correctedTimeText.value) },
                modifier = Modifier.size(width = 1000.dp, height = 100.dp)
            ) {
                Text("Record")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Button(onClick = { recordedValues.add("") }) {
                    Text("Split")
                }
                Spacer(Modifier.width(50.dp))
                Button(onClick = {
                    if (recordedValues.size > 0) {
                        showDialog.value = true
                    }
                }) {
                    Text("Clear")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(recordedValues) { _, value ->
                    Text(value, fontSize = 16.sp)
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    recordedValues.clear()
                    showDialog.value = false
                }) {
                    Text("Clear Records")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Keep Records")
                }
            },
            title = { Text("Confirm") },
        )
    }
}
