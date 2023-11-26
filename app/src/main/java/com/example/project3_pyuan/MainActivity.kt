package com.example.project3_pyuan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
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

data class Percentage(var percentage: Float = 0F)

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

@Composable
fun LayoutPreview() {
    Project3pyuanTheme {
        Layout(0, 0, 0, UserStore(LocalContext.current))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout(waterGoal: Int, waterDrunkPassed: Int, currentStreakValue: Int, store: UserStore) {
    Project3pyuanTheme {
        val context = LocalContext.current
        var waterDrunk by remember { mutableStateOf(waterDrunkPassed) }
        var percentageToGoal by remember { mutableStateOf(min(1.0F, waterDrunk.toFloat()/waterGoal)) }

        waterDrunk = (store.getTodayWater.collectAsState(initial = 0)).value
        percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)

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
//                .padding(4.dp)
        ) {
//            Button(onClick = {
//                CoroutineScope(Dispatchers.IO).launch {
//                    store.saveDay(0)
//                    store.saveOnboardingStatus(false)
//                    store.clearAll()
//                }
//            }) {
//                Text(text = "Clear day")
//            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
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
                val animatedProgress by animateFloatAsState(
                    targetValue = percentageToGoal,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
                )

                Box(modifier = Modifier.fillMaxSize()) {

                    Text(
                        text = "$waterGoal oz",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                    LinearProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .rotate(270f)
                            .scale(2F)
                    )
                    Text(
                        text = "$waterDrunk oz",
                        fontSize = 40.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                }

//                OutlinedButton(
//                    onClick = {
//                        if (progress < 1f) progress += 0.1f
//                    }
//                ) {
//                    Text("Increase")
//                }
            }
            Row(
                modifier = Modifier
                    .padding(5.dp)
            ) {
                var p by remember { mutableStateOf(Percentage(0F))}
                p.percentage = min(1.0F, waterDrunk.toFloat()/waterGoal)
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
//                    waterDrunk = DrinkButton(name = "Cup of water", 8, waterDrunk, store)
                    Button(
                        onClick = {
                            waterDrunk += 8
                            CoroutineScope(Dispatchers.IO).launch {
                                store.setTodayWater(waterDrunk)
                            }
                            percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
                        },
                        shape = RoundedCornerShape(20),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = "Cup of Water\n8 oz",
                            textAlign = TextAlign.Center
                        )
                    }
                    Button(
                        onClick = {
                            waterDrunk += 8
                            CoroutineScope(Dispatchers.IO).launch {
                                store.setTodayWater(waterDrunk)
                            }
                            percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
                        },
                        shape = RoundedCornerShape(20),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = "Cup of Water\n8 oz",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
//                    DrinkButton(name = "Coffee", 4, waterDrunk, store, p) {
//                        p = p.copy(percentage = p.percentage + 4)
//                    }
//                    DrinkButton(name = "Jello")
                    Button(
                        onClick = {
                            waterDrunk += 8
                            CoroutineScope(Dispatchers.IO).launch {
                                store.setTodayWater(waterDrunk)
                            }
                            percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
                        },
                        shape = RoundedCornerShape(20),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = "Cup of Water\n8 oz",
                            textAlign = TextAlign.Center
                        )
                    }
                    Button(
                        onClick = {
                            waterDrunk += 8
                            CoroutineScope(Dispatchers.IO).launch {
                                store.setTodayWater(waterDrunk)
                            }
                            percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
                        },
                        shape = RoundedCornerShape(20),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = "Cup of Water\n8 oz",
                            textAlign = TextAlign.Center
                        )
                    }

                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
//                    DrinkButton(name = "Coffee")
//                    DrinkButton(name = "Jello")
                    Button(
                        onClick = {
                            waterDrunk += 8
                            CoroutineScope(Dispatchers.IO).launch {
                                store.setTodayWater(waterDrunk)
                            }
                            percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
                        },
                        shape = RoundedCornerShape(20),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = "Cup of Water\n8 oz",
                            textAlign = TextAlign.Center
                        )
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(5.dp),
                        shape = RoundedCornerShape(20),
                        onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            store.saveDay(0)
                            store.saveOnboardingStatus(false)
                            store.clearAll()
                        }
                    }) {
                        Text(text = "Clear day")
                    }
//                    Button(
//                        onClick = {
//                            CoroutineScope(Dispatchers.IO).launch {
//                                store.setTodayWater(8 + waterDrunk)
//                            }
//                            waterDrunk += 8
//                            percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
//                        },
//                        shape = RoundedCornerShape(20),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                            .padding(5.dp)
//                    ) {
//                        Text(
//                            text = "Cup of Water\n8 oz",
//                            textAlign = TextAlign.Center
//                        )
//                    }

                }
            }
        }
    }
}

fun secondsToDay(timestamp: Int): Int {
//    return (timestamp / (60 * 60 * 24))
    return (timestamp / (60))
}

@Composable
fun DrinkButton(name: String, waterDrunk: Int, currentWater: Int, store: UserStore, percentage: Percentage, updateWater: () -> Unit) {
    Button(
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                store.setTodayWater(waterDrunk + currentWater)
            }
            updateWater()
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = name)
    }
}