package com.jonasrosendo.tinder.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
fun AuthenticationButton(
    label: String,
    forceFocus: Boolean = true,
    onClick: () -> Unit
) {

    val focus = LocalFocusManager.current

    Button(
        onClick = {
            focus.clearFocus(force = forceFocus)
            onClick()
        },
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(text = label)
    }
}