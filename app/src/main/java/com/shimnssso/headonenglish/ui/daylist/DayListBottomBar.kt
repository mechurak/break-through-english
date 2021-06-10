package com.shimnssso.headonenglish.ui.daylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.R
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
            Image(
                painter = painterResource(R.drawable.ic_home),
                contentDescription = "home icon",
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            if (!isLoading) {
                                onBack()
                            }
                        })
                    .padding(8.dp)
            )

            Image(
                painter = painterResource(R.drawable.ic_refresh),
                contentDescription = "refresh icon",
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            if (!isLoading) {
                                onRefresh()
                            }
                        })
                    .padding(8.dp)
            )
        }
    }
}