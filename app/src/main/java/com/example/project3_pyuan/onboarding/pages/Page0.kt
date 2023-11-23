package com.example.project3_pyuan.onboarding.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project3_pyuan.R
import java.lang.NumberFormatException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun page0(heading: String, subtext: String) : Int {
    val context = LocalContext.current // strange that we have to put it up here
    var weight by remember {
        mutableStateOf("")
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = heading)
        Text(text = subtext)
        TextField(
            value = weight,
            label = { Text("Weight in lbs.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (it.contains(".") || it.contains("-") || it.contains(" ") ||
                    it.contains(",") || it.contains("\n")) {
                    // do nothing
                } else {
                    try {
                        it.toInt()
                        weight = it
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
