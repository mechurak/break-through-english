package com.shimnssso.headonenglish.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.room.DatabaseGlobal
import com.shimnssso.headonenglish.ui.MainActivity

@ExperimentalAnimationApi
@Composable
fun HomeBottomBar(
    showBackdrop: Boolean,
    setShowBackdrop: (Boolean) -> Unit
) {
    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity).get(HomeViewModel::class.java)

    val globalData by viewModel.global.observeAsState(DatabaseGlobal(0))
    val subjects by viewModel.subjects.observeAsState(listOf())
    val isLogIn by viewModel.isLogIn.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)

    var showSubjectList by remember { mutableStateOf(showBackdrop) }

    LaunchedEffect(showBackdrop) {
        showSubjectList = showBackdrop
    }

    Surface(
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
        ) {
            AnimatedVisibility(
                showSubjectList,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    subjects.forEach { subject ->
                        DrawerButton(
                            icon = Icons.Filled.Favorite,
                            label = subject.title,
                            isSelected = subject.subjectId == globalData.subjectId,
                            action = {
                                viewModel.changeSubject(subject.subjectId)
                                showSubjectList = false
                                setShowBackdrop(false)
                            }
                        )
                    }
                    Divider(
                        color = MaterialTheme.colors.onSurface.copy(alpha = .2f),
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (isLogIn) {
                        Button(
                            onClick = {
                                activity.openFilePicker()
                                showSubjectList = false
                                setShowBackdrop(false)
                            },
                            modifier = Modifier
                                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                                .fillMaxWidth()
                        )
                        {
                            Text("Add an item from google sheet")
                        }
                        Button(
                            onClick = {
                                activity.requestSignOut()
                            },
                            modifier = Modifier
                                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                                .fillMaxWidth()
                        )
                        {
                            Text("Sign out")
                        }
                        Divider(
                            color = MaterialTheme.colors.onSurface.copy(alpha = .2f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = {
                    if (!isLoading) {
                        showSubjectList = !showSubjectList
                        setShowBackdrop(showSubjectList)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.MenuOpen,
                        contentDescription = "temp settings"
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colors
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }
    val textIconColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null, // decorative
                    colorFilter = ColorFilter.tint(textIconColor),
                    alpha = imageAlpha
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor
                )
            }
        }
    }
}