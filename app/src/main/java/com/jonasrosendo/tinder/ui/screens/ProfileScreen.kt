package com.jonasrosendo.tinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jonasrosendo.tinder.TinderViewModel
import com.jonasrosendo.tinder.model.FirebaseUserData
import com.jonasrosendo.tinder.navigation.RouteNavigation
import com.jonasrosendo.tinder.navigation.navigateTo
import com.jonasrosendo.tinder.navigation.navigateToAndClearStack
import com.jonasrosendo.tinder.ui.BottomNavigationItem
import com.jonasrosendo.tinder.ui.BottomNavigationMenu
import com.jonasrosendo.tinder.utils.CommonDivider
import com.jonasrosendo.tinder.utils.CommonProgressSpinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.wait

enum class Gender {
    MALE, FEMALE, BOTH
}

@Composable
fun ProfileScreen(viewModel: TinderViewModel, navController: NavController) {

    var inProgress by remember {
        mutableStateOf(false)
    }

    CoroutineScope(Dispatchers.Main).launch {
        viewModel.loading.collect {
            inProgress = it
        }
    }

    val userData = remember {
        mutableStateOf<FirebaseUserData?>(null)
    }

    val deferred = CoroutineScope(Dispatchers.Main).async {
        viewModel.userData.collect {
            userData.value = it
        }
    }

    LaunchedEffect(key1 = Unit) {
        deferred.wait()
    }

    if (inProgress) {
        CommonProgressSpinner()
    } else {

        val name = rememberSaveable {
            mutableStateOf(userData.value?.name ?: "")
        }

        val username = rememberSaveable {
            mutableStateOf(userData.value?.username ?: "")
        }

        val bio = rememberSaveable {
            mutableStateOf(userData.value?.bio ?: "")
        }

        val gender = rememberSaveable {
            mutableStateOf(Gender.valueOf(userData.value?.gender?.uppercase() ?: "MALE"))
        }

        val genderPreference = rememberSaveable {
            mutableStateOf(
                Gender.valueOf(
                    userData.value?.genderPreference?.uppercase() ?: "FEMALE"
                )
            )
        }

        val scrollState = rememberScrollState()

        Column {
            ProfileContent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(8.dp),
                viewModel = viewModel,
                name = name.value,
                username = username.value,
                bio = bio.value,
                gender = gender.value,
                genderPreference = genderPreference.value,
                onNameChange = { name.value = it },
                onUsernameChange = { username.value = it },
                onBioChange = { bio.value = it },
                onGenderChange = { gender.value = it },
                onGenderPreferenceChange = { genderPreference.value = it },
                onSave = {
                    //call from view model
                },
                onBack = {
                    navController.navigateTo(RouteNavigation.Swipe.route)
                },
                onLogout = {
                    viewModel.onLogout()
                    navController.navigateToAndClearStack(RouteNavigation.Login.route)
                }
            )
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.PROFILE,
                navController = navController
            )
        }
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier,
    viewModel: TinderViewModel,
    name: String,
    username: String,
    bio: String,
    gender: Gender,
    genderPreference: Gender,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onGenderPreferenceChange: (Gender) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var imageUrl by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.userData.collect {
            imageUrl = it?.imageUrl
        }
    }

    Column(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", modifier = Modifier.clickable { onBack() })
            Text(text = "Save", modifier = Modifier.clickable { onSave() })
        }

        CommonDivider()

        ProfileImage()

        CommonDivider()

        ProfileTextBox(label = "Name", textFieldValue = name, onValueChange = onNameChange)
        ProfileTextBox(
            label = "Username",
            textFieldValue = username,
            onValueChange = onUsernameChange
        )
        ProfileTextBox(
            label = "Bio",
            textFieldValue = bio,
            onValueChange = onBioChange,
            modifier = Modifier
                .background(
                    color = Color.Transparent
                )
                .height(150.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "I am a:", modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == Gender.MALE,
                        onClick = {
                            onGenderChange(Gender.MALE)
                        }
                    )
                    Text(
                        text = "Man",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderChange(Gender.MALE) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == Gender.FEMALE,
                        onClick = {
                            onGenderChange(Gender.FEMALE)
                        }
                    )
                    Text(
                        text = "Woman",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderChange(Gender.FEMALE) }
                    )
                }
            }

        }

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "Looking for:", modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = genderPreference == Gender.MALE,
                        onClick = {
                            onGenderPreferenceChange(Gender.MALE)
                        }
                    )
                    Text(
                        text = "Men",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderPreferenceChange(Gender.MALE) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = genderPreference == Gender.FEMALE,
                        onClick = {
                            onGenderPreferenceChange(Gender.FEMALE)
                        }
                    )
                    Text(
                        text = "Women",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderPreferenceChange(Gender.FEMALE) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = genderPreference == Gender.BOTH,
                        onClick = {
                            onGenderPreferenceChange(Gender.BOTH)
                        }
                    )
                    Text(
                        text = "Both",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderPreferenceChange(Gender.BOTH) }
                    )
                }
            }
        }

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Logout", modifier = Modifier.clickable { onLogout() })
        }
    }
}

@Composable
fun ProfileTextBox(
    label: String,
    textFieldValue: String,
    onValueChange: (String) -> Unit,
    isSingleLine: Boolean = true,
    modifier: Modifier? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.width(100.dp))
        TextField(
            value = textFieldValue,
            onValueChange = onValueChange,
            modifier = modifier ?: Modifier.background(
                color = Color.Transparent
            ),
            colors = TextFieldDefaults.textFieldColors(textColor = Color.Black),
            singleLine = isSingleLine
        )
    }
}

@Composable
fun ProfileImage() {

}