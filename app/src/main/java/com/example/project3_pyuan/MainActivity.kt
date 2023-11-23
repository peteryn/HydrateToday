package com.example.project3_pyuan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project3_pyuan.ui.theme.Project3pyuanTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.NumberFormatException

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = UserStore(this)

        setContent {
            Project3pyuanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("Android")
                    val myFlow = store.getAccessToken
                    val flowValue: String
                    runBlocking(Dispatchers.IO) {
                        flowValue = myFlow.first()
                    }
                    if (flowValue.isEmpty()) {
                        PagerAnimateToItem()
                    } else {
                        Layout()
                    }
                }
            }
        }

        val dbHelper: DatabaseOpenHelper = DatabaseOpenHelper(this)
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

//        val flowValue: String
//        runBlocking(Dispatchers.IO) {
//            flowValue = store.getAccessToken.first()
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
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerAnimateToItem() {
    val context = LocalContext.current
    val store = UserStore(context)
    var weight by remember {
        mutableStateOf(0)
    }
    Box (
        modifier = Modifier
            .fillMaxWidth()
    ){
        // [START android_compose_layouts_pager_scroll_animate]
        var currentPage by remember {
            mutableStateOf(0)
        };
        val pagerState = rememberPagerState(0)
        val myContext = LocalContext.current

        HorizontalPager(state = pagerState, pageCount = 3, userScrollEnabled = false) { page ->
            // Our page content
            when (page) {
                0 -> {
                    weight = page("Please enter your weight")
                    Log.w("Weight", weight.toString())
                }
                1 -> page("stuff")
                2 -> page("zebra")
            }
//            Text(
//                text = "Page: $page",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//            )
        }

        // scroll to page
        val coroutineScope = rememberCoroutineScope()
        var s by remember {
            mutableStateOf("Next")
        }
        Button(onClick = {
            if (s == "Finish") {
//                CoroutineScope(Dispatchers.IO).launch {
//                    store.saveToken("Done")
//                }
                // there's a chance that a coroutine does not update the datastore before the UI
                // thread checks it, so it is possible that the onboarding is run twice
                // use blocking instead
                runBlocking(Dispatchers.IO) {
                    store.saveToken("Done")
                }
                val intent = Intent(myContext, MainActivity::class.java)
                myContext.startActivity(intent)
            }
            else {
                coroutineScope.launch {
                    // Call scroll to on pagerState
                    Log.w("INFO", currentPage.toString())
                    if (currentPage < 2) {
                        currentPage++
                        pagerState.animateScrollToPage(currentPage)
                    }
                }
            }
        }, modifier = Modifier.align(Alignment.BottomEnd)) {
            if (currentPage == 2) {
                s = "Finish"
            }
            else {
                s = "Next"
            }
            Text(s);
        }
        if (currentPage != 0) {
            Button(onClick = {
                coroutineScope.launch {
                    Log.w("INFO", currentPage.toString())
                    // Call scroll to on pagerState
                    if (currentPage > 0) {
                        currentPage--;
                        pagerState.animateScrollToPage(currentPage)
                    }
                }
            }, modifier = Modifier.align(Alignment.BottomStart)) {
                Text("Back")
            }
        }
        // [END android_compose_layouts_pager_scroll_animate]
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun page(heading: String) : Int {
    val context = LocalContext.current // strange that we have to put it up here
    var weight by remember {
        mutableStateOf("")
    }
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = heading,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(bottom = 30.dp)
        )
        TextField(
            value = weight,
            label = {Text("Weight in lbs.")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (it.contains(".") || it.contains("-") || it.contains(" ") || it.contains(",") || it.contains("\n")) {
                    // do nothing
                } else {
                    try {
                        Log.w("INFO", "In try")
                        it.toInt()
                        Log.w("INFO", "Past first check")
                        weight = it
                        Log.w("INFO", "Weight should be updated")
                    } catch (e: NumberFormatException) {
                        // toast displaying stuff
                        weight = ""
                        if (it.isNotEmpty()) {
                            Toast.makeText(context, "Please enter a smaller weight", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        )
    }
    if (weight.toString().isEmpty()) {
        return 0
    }

    return weight.toInt()
}