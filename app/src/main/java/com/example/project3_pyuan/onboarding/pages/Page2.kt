package com.example.project3_pyuan.onboarding.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.Math.round
import kotlin.math.roundToInt

@Composable
fun page2(weight: Int, activityLevel: Int): Int {
    val context = LocalContext.current // strange that we have to put it up here
    val waterGoal = (weight * 0.5).roundToInt() + ((activityLevel + 1) * 12)
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Your goal is to drink $waterGoal oz a day",
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(bottom = 30.dp)
                .width(300.dp)
        )
    }
    return waterGoal
}