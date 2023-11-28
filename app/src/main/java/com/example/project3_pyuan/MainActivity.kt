package com.example.project3_pyuan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
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
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.lang.Float.min
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val debug = false
        setContent {
            Project3pyuanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val intent = intent
                    val v = intent.getIntExtra("complete", -1)
                    val store = UserStore(this)

                    val dayFlow = store.getDay
                    var dayValue: Int
                    val today = secondsToDay((System.currentTimeMillis()/1000).toInt(), debug)

                    val waterDrunkFlow = store.getTodayWater
                    var waterDrunkValue: Int

                    val onBoardingStatusFlow = store.getOnboardingStatus
                    val onboardingStatusValue: Boolean

                    val waterGoalFlow = store.getUserWaterGoal
                    var waterGoalValue: Int

                    val currentStreak = store.getCurrentStreak
                    var currentStreakValue: Int
                    var updateStreak = true
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
//                                store.saveStreak(currentStreakValue + 1)
                            } else {
                                store.saveStreak(0)
                            }
                            // reset the water for the new day
                            store.setTodayWater(0)
                        } else {
                            if (onboardingStatusValue && v == -1) {
                                updateStreak = false
                            }
                        }
                   }
                    if (onboardingStatusValue) {
                        Layout(waterGoalValue, waterDrunkValue, currentStreakValue, store, debug, updateStreak)
                    } else {
                        OnboardingScreen()
                    }
                }
            }
        }
    }
}



@Composable
fun Layout(waterGoal: Int, waterDrunkPassed: Int, currentStreak: Int, store: UserStore, debug: Boolean, updateStreak: Boolean) {
    Project3pyuanTheme {
        // set the state with the values passed in
        var waterDrunk by remember { mutableStateOf(waterDrunkPassed) }
        var percentageToGoal by remember { mutableStateOf(min(1.0F, waterDrunk.toFloat()/waterGoal)) }
        var currentStreakState by remember { mutableStateOf(currentStreak) }
        var updateStreakValue by remember { mutableStateOf(updateStreak) }
        var showConfetti by remember { mutableStateOf(false) }

        // make sure state is synchronized
        waterDrunk = store.getTodayWater.collectAsState(initial = 0).value
        percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)
        currentStreakState = store.getCurrentStreak.collectAsState(initial = 0).value

        // confetti code from https://github.com/DanielMartinus/Konfetti
        val state = listOf(Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        ))
        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = state,
            )
        }
        Column {
            Column(modifier = Modifier.weight(1f)) {
                // Code is from
                // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#LinearProgressIndicator(kotlin.Float,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap)
                // Used to animate water progress
                val animatedProgress by animateFloatAsState(
                    targetValue = percentageToGoal,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    if (debug) {
                        Button(
                            modifier = Modifier.align(Alignment.CenterStart),
                            onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                store.saveDay(-1)
                                store.saveOnboardingStatus(false)
                                store.clearAll()
                            }
                        }) {
                            Text(text = "Reset App")
                        }
                    }
                    Box (
                        modifier = Modifier
                            .padding(5.dp)
                            .size(30.dp)
                            .align(Alignment.TopEnd)
                            // info about circle borders from here
                            // https://stackoverflow.com/questions/66014834/how-to-draw-a-circular-image-in-android-jetpack-compose
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clip(CircleShape)
                    ) {
                        Text (
                            text = "$currentStreakState",
                            fontSize = 15.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
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

            Row(modifier = Modifier.padding(4.dp)) {
                val updateWater: (Int) -> Unit = { itemAmountOfWater ->
                    waterDrunk += itemAmountOfWater
                    percentageToGoal = min(1.0F, waterDrunk.toFloat() / waterGoal)
                    if (waterDrunk >= waterGoal && updateStreakValue) {
                        currentStreakState++
                        CoroutineScope(Dispatchers.IO).launch {
                            store.saveStreak(currentStreakState)
                        }
                        updateStreakValue = false
                        showConfetti = true
                    }
                }
                // left column
                Column(modifier = Modifier.weight(1f)) {
                    DrinkButton(
                        name = "Cup of Water",
                        itemAmountOfWater = 8,
                        waterDrunk = waterDrunk,
                        store = store,
                        updateWater = updateWater
                    )
                    DrinkButton(
                        name = "Bottle of Water",
                        itemAmountOfWater = 17,
                        waterDrunk = waterDrunk,
                        store = store,
                        updateWater = updateWater
                    )
                }
                // middle column
                Column(modifier = Modifier.weight(1f)) {
                    DrinkButton(
                        name = "Cup of Coffee",
                        itemAmountOfWater = 7,
                        waterDrunk = waterDrunk,
                        store = store,
                        updateWater = updateWater
                    )
                    DrinkButton(
                        name = "Average Meal",
                        itemAmountOfWater = 15,
                        waterDrunk = waterDrunk,
                        store = store,
                        updateWater = updateWater
                    )
                }
                // right column
                Column(modifier = Modifier.weight(1f)) {
                    DrinkButton(
                        name = "Can of Soda",
                        itemAmountOfWater = 11,
                        waterDrunk = waterDrunk,
                        store = store,
                        updateWater = updateWater
                    )
                    DrinkButton(
                        name = "Slice of Watermelon",
                        itemAmountOfWater = 6,
                        waterDrunk = waterDrunk,
                        store = store,
                        updateWater = updateWater
                    )
                }
            }
        }
    }
}

fun secondsToDay(timestamp: Int, debug: Boolean): Int {
    return if (debug) {
        (timestamp / (60)) // for when a day lasts for 1 minute
    } else {
        (timestamp / (60 * 60 * 24)) // for when a day lasts for 24 hours
    }
}

@Composable
fun DrinkButton(name: String, itemAmountOfWater: Int, waterDrunk: Int, store: UserStore, updateWater: (Int) -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(20),
        onClick = {
            // update the datastore
            CoroutineScope(Dispatchers.IO).launch {
                store.setTodayWater(waterDrunk + itemAmountOfWater)
            }
            // update the UI state
            updateWater(itemAmountOfWater)
        }
    ) {
        Text(
            text = "$name\n$itemAmountOfWater oz",
            textAlign = TextAlign.Center
        )
    }
}