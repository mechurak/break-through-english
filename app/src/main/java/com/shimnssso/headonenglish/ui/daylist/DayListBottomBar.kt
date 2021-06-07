package com.shimnssso.headonenglish.ui.daylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.ui.MainActivity

@ExperimentalAnimationApi
@Composable
fun DayListBottomBar(
    onRefresh: () -> Unit,
    onBack: () -> Unit,
) {
    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity).get(HomeViewModel::class.java)

    val isLoading by viewModel.isLoading.observeAsState(false)


    Surface(
        elevation = 8.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
        ) {
            IconButton(onClick = {
                if (!isLoading) {
                    onBack()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            }

            IconButton(onClick = {
                if (!isLoading) {
                    onRefresh()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
    }
}