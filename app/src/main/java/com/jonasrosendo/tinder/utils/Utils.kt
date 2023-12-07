package com.jonasrosendo.tinder.utils

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.jonasrosendo.tinder.TinderViewModel
import com.jonasrosendo.tinder.data.Event

@Composable
fun CommonProgressSpinner() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) {}
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun NotificationMessage(viewModel: TinderViewModel) {
    val notificationState = remember { mutableStateOf<Event<String>?>(null) }

    LaunchedEffect(key1 = Unit) {
        viewModel.popupNotification.collect { event ->
            notificationState.value = event
        }
    }
    val notificationMessage = notificationState.value?.getContentOrNull()

    if (notificationMessage?.isNotEmpty() == true) {
        Toast.makeText(LocalContext.current, notificationMessage, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun CommonDivider() {
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CommonImage(
    modifier: Modifier = Modifier,
    data: String?,
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberImagePainter(data = data)

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.wrapContentSize(),
        contentScale = contentScale
    )

    if (painter.state is ImagePainter.State.Loading) {
        CommonProgressSpinner()
    }
}