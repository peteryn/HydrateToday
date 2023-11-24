package com.example.project3_pyuan.onboarding.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality.Companion.Low
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.NumberFormatException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun page1() : Int {
    val context = LocalContext.current // strange that we have to put it up here
    var expanded by remember {
        mutableStateOf(false)
    }
    val exerciseLevels = arrayOf("Low (< 30 minutes)", "Average (60 minutes)", "High (> 90 minutes)")
    var selectedItem by remember {
        mutableStateOf("")
    }
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Please select your activity level",
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(bottom = 30.dp)
                .width(300.dp)
        )
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {expanded = !expanded}) {
            TextField(
                value = selectedItem,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                exerciseLevels.forEach { item ->
                    DropdownMenuItem(text = { Text(text = item) }, onClick = {
                        selectedItem = item
                        expanded = false
                    })
                }
            }
        }
    }
    if (selectedItem.isEmpty()) {
        return -1
    }

    return exerciseLevels.indexOf(selectedItem)
}