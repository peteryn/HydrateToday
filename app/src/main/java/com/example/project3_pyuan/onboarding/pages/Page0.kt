package com.example.project3_pyuan.onboarding.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ask user for their weight
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun page0(currentWeight: Int): Int {
    var weight by remember {
        mutableStateOf(if (currentWeight == -1) {""} else {currentWeight.toString()})
    }
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Please enter your weight",
            fontSize = 30.sp,
            modifier = Modifier
                .padding(bottom = 30.dp)
        )
        TextField(
            value = weight,
            label = {Text("Weight in lbs.")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                // make sure user can't type illegal values or values longer than 3 digits
                if (it.contains(".") || it.contains("-") || it.contains(" ") || it.contains(",") || it.contains("\n") || it.length > 3) {
                    // do nothing
                } else {
                    weight = it // set the weight
                }
            }
        )
    }

    // return correct value
    if (weight.isEmpty() || weight.toInt() == 0) {
        return -1
    }
    return weight.toInt()
}
