package com.shimnssso.headonenglish.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.shimnssso.headonenglish.R

@ExperimentalAnimationApi
@Composable
fun LoadingAwareBox(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        AnimatedVisibility(
            isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {

                    }
                    .background(Color.Black.copy(0.7f))
            )
        }
        AnimatedVisibility(
            isLoading,
            enter = slideInHorizontally(
                // Enters by sliding up from offset -fullHeight to 0.
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
            ) + fadeIn(),
            exit = slideOutHorizontally(
                // Exits by sliding right from offset 0 to fullHeight.
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
            ) + fadeOut()
        ) {
            val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.walking_broccoli) }
            val animationState = rememberLottieAnimationState(autoPlay = true, repeatCount = Integer.MAX_VALUE)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                LottieAnimation(
                    spec = animationSpec,
                    modifier = Modifier
                        .size(250.dp),
                    animationState = animationState,
                )
            }
        }
    }
}