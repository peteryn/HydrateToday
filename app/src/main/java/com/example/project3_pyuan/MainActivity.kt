package com.example.project3_pyuan

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.platform.LocalContext
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
        val debug = true
        // setContent is where the 'jetpack compose' code starts
        setContent {
            Project3pyuanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // setting up variables
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
                    runBlocking(Dispatchers.IO) {
                        // get all values
                        dayValue = dayFlow.first()
                        waterDrunkValue = waterDrunkFlow.first()
                        onboardingStatusValue = onBoardingStatusFlow.first()
                        waterGoalValue = waterGoalFlow.first()

                        // if it is a new day
                        if (dayValue < today) {
                            store.saveDay(today)
                            // reset the water for the new day
                            store.setTodayWater(0)
                        }
                   }
                    if (onboardingStatusValue) {
                        Layout(waterGoalValue, waterDrunkValue, store, debug)
                    } else {
                        OnboardingScreen()
                    }
                }
            }
        }
    }
}

// home screen user sees after onboarding screen
@Composable
fun Layout(waterGoal: Int, waterDrunkPassed: Int, store: UserStore, debug: Boolean) {
    Project3pyuanTheme {
        // set the state with the values passed in
        var waterDrunk by remember { mutableStateOf(waterDrunkPassed) }
        var percentageToGoal by remember { mutableStateOf(min(1.0F, waterDrunk.toFloat()/waterGoal)) }
        var showConfetti by remember { mutableStateOf(false) }
        val myContext = LocalContext.current

        // make sure state is synchronized
        waterDrunk = store.getTodayWater.collectAsState(initial = 0).value
        percentageToGoal = min(1.0F, waterDrunk.toFloat()/waterGoal)

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
                    // show reset button if debug is enabled
                    if (debug) {
                        Button(
                            modifier = Modifier.align(Alignment.CenterStart),
                            onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                store.saveDay(-1)
                                store.saveOnboardingStatus(false)
                                store.clearAll()
                            }
                            val intent = Intent(myContext, MainActivity::class.java)
                            myContext.startActivity(intent)
                        }) {
                            Text(text = "Reset App")
                        }
                    }
                    // water goal
                    Text(
                        text = "$waterGoal oz",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                    // display how much water has been drunk as a bar
                    LinearProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .rotate(270f)
                            .scale(2F)
                    )
                    // display how much water drunk as a number
                    Text(
                        text = "$waterDrunk oz",
                        fontSize = 40.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                }
            }

            Row(modifier = Modifier.padding(4.dp)) {
                // define callback function that is called from within a button
                val updateWater: (Int) -> Unit = { itemAmountOfWater ->
                    waterDrunk += itemAmountOfWater
                    percentageToGoal = min(1.0F, waterDrunk.toFloat() / waterGoal)
                    if (waterDrunk >= waterGoal) {
                        showConfetti = true
                    }
                }
                // left column
                Column(modifier = Modifier.weight(1f)) {
                    // button to add to water drunken
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

// convert unix time epoch to day number
fun secondsToDay(timestamp: Int, debug: Boolean): Int {
    return if (debug) {
        (timestamp / (60)) // for when a day lasts for 1 minute
    } else {
        (timestamp / (60 * 60 * 24)) // for when a day lasts for 24 hours
    }
}

// the button that a user presses after finishing a cup of water or x item of water
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