package com.example.project3_pyuan

import android.app.AlertDialog
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project3_pyuan.ui.theme.Project3pyuanTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project3pyuanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("Android")
                    Layout()
                }
            }
        }

        val dbHelper: DatabaseOpenHelper = DatabaseOpenHelper(this)
        val store = UserStore(this)
        val myFlow = store.getAccessToken
        val flowValue: String
//        runBlocking(Dispatchers.IO) {
//            flowValue = myFlow.first()
//        }
//        if (flowValue.isEmpty()) {
//
//        }
   }

    fun getInfo() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("I am the title")
            .setPositiveButton("Positive") { dialog, which ->
                // Do something.
            }
            .setNegativeButton("Negative") { dialog, which ->
                // Do something else.
            }
            .setItems(arrayOf("Item One", "Item Two", "Item Three")) { dialog, which ->
                // Do something on item tapped.
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Project3pyuanTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun LayoutPreview() {
    Project3pyuanTheme {
        Layout()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout() {
    Project3pyuanTheme {
        val context = LocalContext.current
        val tokenValue = remember {
            mutableStateOf(TextFieldValue())
        }
        val store = UserStore(context)
        val tokenText = store.getAccessToken.collectAsState(initial = "")

        val flowValue: String
        runBlocking(Dispatchers.IO) {
            flowValue = store.getAccessToken.first()
        }

        val openAlertDialog = remember { mutableStateOf(flowValue.isEmpty()) }
//        when {
//            openAlertDialog.value -> {
//                AlertDialogExample(
//                    onDismissRequest = { openAlertDialog.value = true },
//                    onConfirmation = {
//                        openAlertDialog.value = false
//                        Log.w("INFO","Confirmation registered") // Add logic here to handle confirmation.
//                    },
//                    dialogTitle = "First Time Setup",
//                    dialogText = "This is an example of an alert dialog with buttons.",
//                )
//            }
//        }
//        if (tokenText.value.isEmpty()) {
//            AlertDialogExample(
//                onDismissRequest = { },
//                onConfirmation = { /*TODO*/ },
//                dialogTitle = "No data",
//                dialogText = "no data",
//            )
//        }

        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {

            Row() {
                Text(text = tokenText.value)
                TextField(
                    value = tokenValue.value,
                    onValueChange = { tokenValue.value = it },
                )
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            store.saveToken(tokenValue.value.text)
                        }
                    }
                ) {
                    Text("Update")
                }
            }
            Row(modifier = Modifier.weight(1f)) {
                Text(text = "Second row")
            }
            Row() {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    DrinkButton(name = "Water")
                    DrinkButton(name = "Food")
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    DrinkButton(name = "Coffee")
                    DrinkButton(name = "Jello")
                }
            }
        }
    }
}

@Composable
fun DrinkButton(name: String) {
    Button(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = name)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
//        dismissButton = {
//            TextButton(
//                onClick = {
//                    onDismissRequest()
//                }
//            ) {
//                Text("Dismiss")
//            }
//        }
    )
}