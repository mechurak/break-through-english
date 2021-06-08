package com.shimnssso.headonenglish.ui.select

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.room.DatabaseGlobal
import com.shimnssso.headonenglish.room.DatabaseSubject
import com.shimnssso.headonenglish.ui.MainActivity
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import com.shimnssso.headonenglish.ui.components.LoadingAwareBox
import com.shimnssso.headonenglish.ui.daylist.HomeViewModel
import com.shimnssso.headonenglish.utils.supportWideScreen

@ExperimentalAnimationApi
@Composable
fun SelectScreen(
    navigateToDayList: () -> Unit
) {
    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity).get(HomeViewModel::class.java)

    val isLogIn by viewModel.isLogIn.observeAsState(false)
    val globalData by viewModel.global.observeAsState(DatabaseGlobal(0))
    val subjects by viewModel.subjects.observeAsState(listOf())

    val showDialog by viewModel.showDialog.observeAsState(false)
    val sheetFiles by viewModel.sheetFiles.observeAsState(listOf())

    val isLoading by viewModel.isLoading.observeAsState(false)


    if (showDialog) {
        ImportConfirmDialog(
            files = sheetFiles,
            onConfirm = { name, sheetId -> viewModel.importSubject(name, sheetId) },
            onDismiss = { viewModel.dismissSheetFetchDialog() }
        )
    }

    var showRemoveConfirm by remember { mutableStateOf(false) }
    var removingSubject: DatabaseSubject? by remember { mutableStateOf(null) }
    if (showRemoveConfirm) {
        RemoveConfirmDialog(
            subject = removingSubject!!,
            onConfirm = {
                viewModel.removeSubject(removingSubject!!.subjectId)
                removingSubject = null
                showRemoveConfirm = false
            },
            onDismiss = {
                removingSubject = null
                showRemoveConfirm = false
            })
    }


    Scaffold(
        topBar = {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = LocalContentColor.current
                    )
                },
            )
        },
    ) { innerPadding ->
        if (isLogIn) {
            LoadingAwareBox(isLoading = isLoading) {
                Column(
                    modifier = Modifier
                        // innerPadding takes into account the top and bottom bar
                        .padding(innerPadding)
                        // offset content in landscape mode to account for the navigation bar
                        .navigationBarsPadding(bottom = false)
                        // center content in landscape mode
                        .supportWideScreen()
                        .verticalScroll(rememberScrollState()),
                ) {
                    subjects.forEach {
                        SubjectCard(
                            subject = it, globalData.subjectId == it.subjectId,
                            onClick = { subjectId ->
                                viewModel.changeSubject(subjectId)
                                navigateToDayList()
                            },
                            onLongClick = { targetSubject ->
                                removingSubject = targetSubject
                                showRemoveConfirm = true
                            },
                            onLinkClick = { link ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                activity.startActivity(intent)
                            }
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(16.dp)
                    ) {
                        Button(onClick = { activity.openFilePicker() }) {
                            Text("Add")
                        }

                        Button(onClick = { activity.requestSignOut() }) {
                            Text("Log out")
                        }
                    }
                }

            }
        } else {
            val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.walking_broccoli) }
            val animationState = rememberLottieAnimationState(autoPlay = true, repeatCount = Integer.MAX_VALUE)

            val activity = LocalContext.current as MainActivity
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                LottieAnimation(
                    spec = animationSpec,
                    modifier = Modifier
                        .size(250.dp),
                    animationState = animationState,
                )

                Text("Google sign-in is required to access your google sheets.", textAlign = TextAlign.Center)
                Button(
                    onClick = { activity.requestSignIn() },
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("Sign in")
                }
            }
        }
    }
}