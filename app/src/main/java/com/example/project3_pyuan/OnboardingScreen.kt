package com.example.project3_pyuan

import android.content.Intent
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.project3_pyuan.onboarding.pages.page0
import com.example.project3_pyuan.onboarding.pages.page1
import com.example.project3_pyuan.onboarding.pages.page2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


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
        var currentPage by remember { mutableStateOf(0) }
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
                    waterGoal = page2(weight, activityLevel)
                }
            }
        }

        // scroll to page
        val coroutineScope = rememberCoroutineScope()
        var s by remember {
            mutableStateOf("Next")
        }

        // next button
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = animatedButtonColor.value,
                disabledContainerColor = animatedButtonColor.value,
            ),
            enabled = isButtonEnabled,
            onClick = {
                // setup finish button
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
                // if not finish button, then move to next page
                else {
                    coroutineScope.launch {
                        // Call scroll to on pagerState
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
            s = if (currentPage == 2) {
                "Finish"
            } else {
                "Next"
            }
            Text(s)
        }

        // if we are any page besides the first, include a back button
        if (currentPage != 0) {
            Button(onClick = {
                coroutineScope.launch {
                    Log.w("INFO", currentPage.toString())
                    // Call scroll to on pagerState
                    if (currentPage > 0) {
                        currentPage--
                        pagerState.animateScrollToPage(currentPage)
                    }
                }
            }, modifier = Modifier.align(Alignment.BottomStart)) {
                Text("Back")
            }
        }
    }
}
