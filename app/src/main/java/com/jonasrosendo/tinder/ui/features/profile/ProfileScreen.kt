package com.jonasrosendo.tinder.ui.features.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jonasrosendo.tinder.R
import com.jonasrosendo.tinder.navigation.RouteNavigation
import com.jonasrosendo.tinder.navigation.navigateTo
import com.jonasrosendo.tinder.navigation.navigateToAndClearStack
import com.jonasrosendo.tinder.shared_ui.ShowToast
import com.jonasrosendo.tinder.ui.BottomNavigationItem
import com.jonasrosendo.tinder.ui.BottomNavigationMenu
import com.jonasrosendo.tinder.ui.features.profile.ProfileViewState.DataRetrieved
import com.jonasrosendo.tinder.utils.CommonDivider
import com.jonasrosendo.tinder.utils.CommonImage
import com.jonasrosendo.tinder.utils.CommonProgressSpinner

enum class Gender {
    MALE, FEMALE, BOTH
}

@Composable
fun ProfileScreen(navController: NavController) {

    val viewModel = hiltViewModel<ProfileViewModel>()

    val userData = viewModel.userData.collectAsState()

    val nameState = rememberSaveable {
        mutableStateOf("")
    }

    val usernameState = rememberSaveable {
        mutableStateOf("")
    }

    val bioState = rememberSaveable {
        mutableStateOf("")
    }

    val genderState = rememberSaveable {
        mutableStateOf(Gender.MALE)
    }

    val genderPreferenceState = rememberSaveable {
        mutableStateOf(Gender.FEMALE)
    }

    val scrollState = rememberScrollState()

    when (viewModel.viewState.collectAsState(initial = ProfileViewState.Default).value) {
        ProfileViewState.Default -> viewModel.applyAction(ProfileViewAction.GetUserData)
        is DataRetrieved -> {
            nameState.value = userData.value?.name ?: ""
            usernameState.value = userData.value?.username ?: ""
            bioState.value = userData.value?.bio ?: ""
            genderState.value =
                Gender.valueOf(
                    if (userData.value == null) stringResource(R.string.male)
                    else userData.value!!.gender.toString().uppercase()
                )
            genderPreferenceState.value =
                Gender.valueOf(
                    if (userData.value == null) stringResource(R.string.female)
                    else userData.value!!.genderPreference.toString().uppercase()
                )

            Column {
                ProfileContent(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(8.dp),
                    viewModel = viewModel,
                    name = nameState,
                    username = usernameState,
                    bio = bioState,
                    gender = genderState,
                    genderPreference = genderPreferenceState,
                    onNameChange = { nameState.value = it },
                    onUsernameChange = { usernameState.value = it },
                    onBioChange = { bioState.value = it },
                    onGenderChange = { genderState.value = it },
                    onGenderPreferenceChange = { genderPreferenceState.value = it },
                    onSave = {
                        //call from view model
                        viewModel.save(
                            name = nameState.value,
                            username = usernameState.value,
                            bio = bioState.value,
                            gender = genderState.value,
                            genderPreference = genderPreferenceState.value
                        )
                    },
                    onBack = {
                        navController.navigateTo(RouteNavigation.Swipe.route)
                    }
                ) {
                    viewModel.logout()
                    navController.navigateToAndClearStack(RouteNavigation.Login.route)
                }
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.PROFILE,
                    navController = navController
                )
            }
        }

        ProfileViewState.Loading -> CommonProgressSpinner()
    }

    when (viewModel.viewEvents.collectAsState(initial = null).value) {
        ProfileViewEvent.Saving -> ShowToast(message = stringResource(R.string.saving_profile_message))
        ProfileViewEvent.Saved -> ShowToast(message = stringResource(R.string.profile_saved_message))
        null -> {}
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier,
    viewModel: ProfileViewModel,
    name: MutableState<String>,
    username: MutableState<String>,
    bio: MutableState<String>,
    gender: MutableState<Gender>,
    genderPreference: MutableState<Gender>,
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
            Text(text = stringResource(R.string.back), modifier = Modifier.clickable { onBack() })
            Text(text = stringResource(R.string.save), modifier = Modifier.clickable { onSave() })
        }

        CommonDivider()

        ProfileImage(imageUri = imageUrl, viewModel)

        CommonDivider()

        ProfileTextBox(
            label = stringResource(R.string.name),
            textFieldValue = name,
            onValueChange = onNameChange
        )
        ProfileTextBox(
            label = stringResource(R.string.username),
            textFieldValue = username,
            onValueChange = onUsernameChange
        )
        ProfileTextBox(
            label = stringResource(R.string.bio),
            textFieldValue = bio,
            onValueChange = onBioChange,
            modifier = Modifier.height(150.dp),
            isSingleLine = false
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = stringResource(R.string.i_am_a), modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender.value == Gender.MALE,
                        onClick = {
                            onGenderChange(Gender.MALE)
                        }
                    )
                    Text(
                        text = stringResource(R.string.man),
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderChange(Gender.MALE) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender.value == Gender.FEMALE,
                        onClick = {
                            onGenderChange(Gender.FEMALE)
                        }
                    )
                    Text(
                        text = stringResource(R.string.woman),
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
                text = stringResource(R.string.looking_for), modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = genderPreference.value == Gender.MALE,
                        onClick = {
                            onGenderPreferenceChange(Gender.MALE)
                        }
                    )
                    Text(
                        text = stringResource(R.string.men),
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderPreferenceChange(Gender.MALE) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = genderPreference.value == Gender.FEMALE,
                        onClick = {
                            onGenderPreferenceChange(Gender.FEMALE)
                        }
                    )
                    Text(
                        text = stringResource(R.string.women),
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderPreferenceChange(Gender.FEMALE) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {

                    RadioButton(
                        selected = genderPreference.value == Gender.BOTH,
                        onClick = {
                            onGenderPreferenceChange(Gender.BOTH)
                        }
                    )

                    Text(
                        text = stringResource(R.string.both),
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
            Text(
                text = stringResource(R.string.logout),
                modifier = Modifier.clickable { onLogout() })
        }
    }
}

@Composable
fun ProfileTextBox(
    modifier: Modifier = Modifier,
    label: String,
    textFieldValue: MutableState<String>,
    onValueChange: (String) -> Unit,
    isSingleLine: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.width(100.dp))
        TextField(
            value = textFieldValue.value,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                backgroundColor = Color.Transparent
            ),
            singleLine = isSingleLine
        )
    }
}

@Composable
fun ProfileImage(
    imageUri: String?,
    viewModel: ProfileViewModel
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        uri?.let {
            viewModel.uploadProfileImage(uri)
        }
    }
    Box(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Min),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    // open image gallery
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUri)
            }

            Text(text = stringResource(R.string.change_profile_picture))
        }

        val isLoading = false
        if (isLoading) CommonProgressSpinner()
    }
}