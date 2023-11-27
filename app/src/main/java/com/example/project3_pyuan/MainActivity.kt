package com.example.project3_pyuan

import android.health.connect.datatypes.units.Percentage
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        setContent {
            Project3pyuanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val store = UserStore(this)

                    val dayFlow = store.getDay
                    var dayValue: Int
                    val today = secondsToDay((System.currentTimeMillis()/1000).toInt())

                    val waterDrunkFlow = store.getTodayWater
                    var waterDrunkValue: Int

                    val onBoardingStatusFlow = store.getOnboardingStatus
                    val onboardingStatusValue: Boolean

                    val waterGoalFlow = store.getUserWaterGoal
                    var waterGoalValue: Int

                    val currentStreak = store.getCurrentStreak
                    var currentStreakValue: Int

                    runBlocking(Dispatchers.IO) {
                        // get all values
                        dayValue = dayFlow.first()
                        waterDrunkValue = waterDrunkFlow.first()
                        onboardingStatusValue = onBoardingStatusFlow.first()
                        waterGoalValue = waterGoalFlow.first()
                        currentStreakValue = currentStreak.first()

                        // if it is a new day
                        if (dayValue < today) {
                            store.saveDay(today)
                            // if the water goal was met
                            if (waterDrunkValue >= waterGoalValue) {
                                store.saveStreak(currentStreakValue + 1)
                            } else {
                                store.saveStreak(0)
                            }
                            // reset the water for the new day
                            store.setTodayWater(0)
                        }
                   }
                    if (onboardingStatusValue) {
                        Layout(waterGoalValue, waterDrunkValue, currentStreakValue, store)
                    } else {
                        OnboardingScreen()
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout(waterGoal: Int, waterDrunkPassed: Int, currentStreakValue: Int, store: UserStore) {
    Project3pyuanTheme {
        // set the state with the values passed in
        var waterDrunk by remember { mutableStateOf(waterDrunkPassed) }
        var percentageToGoal by remember { mutableStateOf(min(1.0F, waterDrunk.toFloat()/waterGoal)) }

        // make sure state is synchronized
        waterDrunk = (store.getTodayWater.collectAsState(initial = 0)).value
        percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)

        Column() {
            Column(modifier = Modifier.weight(1f)) {
                // Code is from
                // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#LinearProgressIndicator(kotlin.Float,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap)
                // Used to animate water progress
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
            }
            Row(modifier = Modifier.padding(5.dp)) {
                Column(modifier = Modifier.weight(1f)) {
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
                Column(modifier = Modifier.weight(1f)) {
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
                Column(modifier = Modifier.weight(1f)) {
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
//                    Button(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                            .padding(5.dp),
//                        shape = RoundedCornerShape(20),
//                        onClick = {
//                        CoroutineScope(Dispatchers.IO).launch {
//                            store.saveDay(0)
//                            store.saveOnboardingStatus(false)
//                            store.clearAll()
//                        }
//                    }) {
//                        Text(text = "Clear day")
//                    }

//                    var test by remember { mutableStateOf("") }
//                    hoist(name = test) {
//                        test = "spring"
//                        Log.w("INFO", test)
//                    }
                    DrinkButton(name = "Can of Soda", itemAmountOfWater = 12, waterDrunk = waterDrunk, store = store) {itemAmountOfWater ->
                        waterDrunk += itemAmountOfWater
                        percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
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

@Composable
fun hoist(name: String, update: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp),
        shape = RoundedCornerShape(20),
        onClick = {
            update()
        }
    ) {
        Text(text = name)
    }
}

fun secondsToDay(timestamp: Int): Int {
//    return (timestamp / (60 * 60 * 24)) // for when a day lasts for 24 hours
    return (timestamp / (60)) // for when a day lasts for 1 minute
}

@Composable
fun DrinkButton(name: String, itemAmountOfWater: Int, waterDrunk: Int, store: UserStore, updateWater: (Int) -> Unit) {
//    Button(
//        onClick = {
//            CoroutineScope(Dispatchers.IO).launch {
//                store.setTodayWater(waterDrunk + currentWater)
//            }
//            updateWater()
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//    ) {
//        Text(text = name)
//    }
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp),
        shape = RoundedCornerShape(20),
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                store.setTodayWater(waterDrunk + itemAmountOfWater)
            }
            updateWater(itemAmountOfWater)
        }
    ) {
        Text(
            text = "$name\n$itemAmountOfWater oz",
            textAlign = TextAlign.Center
        )
    }
}