package com.example.project3_pyuan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project3_pyuan.onboarding.pages.page0
import com.example.project3_pyuan.onboarding.pages.page1
import com.example.project3_pyuan.onboarding.pages.page3
import com.example.project3_pyuan.ui.theme.Project3pyuanTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Float.min

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = UserStore(this)
        val day = store.getDay
        var dayValue = 0
        val waterDrunk = store.getTodayWater
        var waterDrunkValue = 0
        runBlocking {
            dayValue = day.first()
            waterDrunkValue = waterDrunk.first()
        }


        setContent {
            Project3pyuanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val onBoardingStatusFlow = store.getOnboardingStatus
                    val onboardingStatus: Boolean
                    val today = secondsToDay((System.currentTimeMillis()/1000).toInt())
                    val waterGoal = store.getUserWaterGoal
                    var waterGoalValue = 0
                    val currentStreak = store.getCurrentStreak
                    var currentStreakValue = 0
                    Log.w("INFO", dayValue.toString())
                    Log.w("INFO", today.toString())
                    runBlocking(Dispatchers.IO) {
                        onboardingStatus = onBoardingStatusFlow.first()
                        waterGoalValue = waterGoal.first()
                        currentStreakValue = currentStreak.first()
                        if (dayValue < today) {
                            store.saveDay(today)
                            if (waterDrunkValue >= waterGoalValue) {
                                store.saveStreak(currentStreakValue + 1)
                            } else {
                                store.saveStreak(0)
                            }
                            store.setTodayWater(0)
                        }
                   }
                    if (onboardingStatus) {
                        Layout(waterGoalValue, waterDrunkValue, currentStreakValue, store)
                    } else {
                        OnboardingScreen()
                    }
                }
            }
        }

        val dbHelper: DatabaseOpenHelper = DatabaseOpenHelper(this)
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

//@Composable
//fun LayoutPreview() {
//    Project3pyuanTheme {
//        Layout()
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout(waterGoal: Int, waterDrunkPassed: Int, currentStreakValue: Int, store: UserStore) {
    Project3pyuanTheme {
        val context = LocalContext.current
        var waterDrunk by remember{ mutableStateOf(waterDrunkPassed) }
//        val store = UserStore(context)
//        val waterGoalState = store.getUserWaterGoal.collectAsState(initial = 1)
//        val waterGoal = waterGoalState.value
//
//        var waterDrunkState by remember { mutableStateOf(0)}
//
//        val day = store.getDay.collectAsState(initial = 0)
////        var day by remember { mutableStateOf(getDay(System.currentTimeMillis() / 1000)) }
//        val currentWater = store.getTodayWater.collectAsState(initial = 0)
//        val waterDrunk = currentWater.value
//        val currentStreak = store.getCurrentStreak.collectAsState(initial = 0)
//        val currentStreakValue = currentStreak.value

//        waterDrunkState = waterDrunk

        val percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)

        // Get local density from composable
        val localDensity = LocalDensity.current

        // Create element height in pixel state
        var columnHeightPx by remember {
            mutableStateOf(0f)
        }

        // Create element height in dp state
        var columnHeightDp by remember {
            mutableStateOf(0.dp)
        }


        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Goal: $waterGoal oz",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        // Set column height using the LayoutCoordinates
                        columnHeightPx = coordinates.size.height.toFloat()
                        columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                    }
            ) {
//                Text(text = "Second row")
//                Text(text = "Water drunk so far: $waterDrunk")
//                Text(text = "Streak: $currentStreakValue")
                Button(modifier = Modifier.rotate(180f), onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        store.saveDay(0)
                        store.saveOnboardingStatus(false)
                        store.clearAll()
                    }
                }) {
                    Text(text = "Clear day")
                }

                val height = columnHeightDp.value * percentageToGoal
                val oh = columnHeightDp.value - height
//                LinearProgressIndicator()

                var progress by remember { mutableStateOf(0.1f) }
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                )

                Row(modifier = Modifier.rotate(270f)) {
                    LinearProgressIndicator(progress = animatedProgress)
                    Spacer(Modifier.requiredHeight(30.dp))
                    OutlinedButton(
                        onClick = {
                            if (progress < 1f) progress += 0.1f
                        }
                    ) {
                        Text("Increase")
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(0.dp, oh.dp)
                        .rotate(0f)
                        .background(Color.Red)
                        .animateContentSize()
                        .height((-1 * height).dp)
                        .width(200.dp)
//                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                        }
                ) {
                }
//                Canvas(modifier = Modifier.fillMaxSize()) {
//                    val canvasWidth = size.width
//                    val canvasHeight = size.height
//                    Log.w("Water Drunk", waterDrunk.toString())
//                    Log.w("Water Goal", waterGoal.toString())
//                    val percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
//                    Log.w("percentage", percentageToGoal.toString())
//                    val height = percentageToGoal * canvasHeight
//                    val drinked = Size(canvasWidth / 5F, height)
////                    val drinked = Size(canvasWidth / 5F, canvasHeight)
//                    drawRect(
//                        color = Color.Red,
//                        size = drinked,
////                        topLeft = Offset(x = (canvasWidth/2) - (drinked.width / 2), y = 0.toFloat())
//                        topLeft = Offset(x = (canvasWidth/2) - (drinked.width / 2), y = size.height - height)
//                    )
//                }
            }
            Row() {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
//                    waterDrunk = DrinkButton(name = "Cup of water", 8, waterDrunk, store)
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                store.setTodayWater(8 + waterDrunk)
                            }
                            waterDrunk += 8
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(text = "Cup of water\n8 oz")
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
//                    DrinkButton(name = "Coffee")
//                    DrinkButton(name = "Jello")
                }
            }
        }
    }
}

fun secondsToDay(timestamp: Int): Int {
//    return (timestamp / (60 * 60 * 24))
    return (timestamp / (60 * 5))
}

@Composable
fun DrinkButton(name: String, waterDrunk: Int, currentWater: Int, store: UserStore) {
    Button(
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                store.setTodayWater(waterDrunk + currentWater)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = name)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun OnboardingScreen() {
    val context = LocalContext.current
    val store = UserStore(context)
    var weight by remember { mutableStateOf(-1) }
    var activityLevel by remember { mutableStateOf(-1) }
    var waterGoal by remember { mutableStateOf(-1) }


    // code to animate from https://stackoverflow.com/questions/73466994/how-to-make-button-background-color-change-animatedly-when-enabled-changes
    var isButtonEnabled by remember { mutableStateOf(false) }
    val animatedButtonColor = animateColorAsState(
        targetValue = if (isButtonEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
        animationSpec = tween(500, 0, LinearEasing), label = ""
    )


    Box (
        modifier = Modifier
            .fillMaxWidth()
    ){
        var currentPage by remember {
            mutableStateOf(0)
        };
        val pagerState = rememberPagerState(0)
        val myContext = LocalContext.current

        HorizontalPager(state = pagerState, pageCount = 3, userScrollEnabled = false) { page ->
            // Our page content
            when (page) {
                0 -> {
                    weight = page0(weight)
                    isButtonEnabled = weight != -1
                }
                1 -> {
                    activityLevel = page1()
                    isButtonEnabled = activityLevel != -1
                }
                2 -> {
                    waterGoal = page3(weight, activityLevel)
                }
            }
        }

        // scroll to page
        val coroutineScope = rememberCoroutineScope()
        var s by remember {
            mutableStateOf("Next")
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = animatedButtonColor.value,
                disabledContainerColor = animatedButtonColor.value,
            ),
            enabled = isButtonEnabled,
            onClick = {
            if (s == "Finish") {
                // there's a chance that a coroutine does not update the datastore before the UI
                // thread checks it, so it is possible that the onboarding is run twice
                // use blocking instead
                runBlocking(Dispatchers.IO) {
                    store.saveOnboardingStatus(true)
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

                CoroutineScope(Dispatchers.IO).launch {
                    store.saveWeight(weight)
                    store.saveActivityLevel(activityLevel)
                    store.saveUserWaterGoal(waterGoal)
                }
            }
        }, modifier = Modifier
            .align(Alignment.BottomEnd)) {
            if (currentPage == 2) {
                s = "Finish"
            }
            else {
                s = "Next"
            }
            Text(s)
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
    }
}