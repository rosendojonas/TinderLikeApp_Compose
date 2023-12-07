package com.jonasrosendo.tinder.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jonasrosendo.tinder.R
import com.jonasrosendo.tinder.navigation.RouteNavigation
import com.jonasrosendo.tinder.navigation.navigateTo

enum class BottomNavigationItem(val icon: Int, val route: RouteNavigation) {
    SWIPE(R.drawable.baseline_swipe, RouteNavigation.Swipe),
    CHAT_LIST(R.drawable.baseline_chat, RouteNavigation.ChatList),
    PROFILE(R.drawable.baseline_profile, RouteNavigation.Profile)
}

@Composable
fun BottomNavigationMenu(selectedItem: BottomNavigationItem, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)
    ) {

        for (item in BottomNavigationItem.values()) {
            Image(
                painter = painterResource(id = item.icon), contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .weight(1f)
                    .clickable {
                        when (item) {
                            BottomNavigationItem.SWIPE -> {}
                            BottomNavigationItem.CHAT_LIST -> {}
                            BottomNavigationItem.PROFILE -> {
                                navController.navigateTo(RouteNavigation.Profile.route)
                            }
                        }
                    },
                colorFilter = if (item == selectedItem) ColorFilter.tint(Color.Black) else ColorFilter.tint(
                    Color.Gray
                )
            )
        }
    }
}