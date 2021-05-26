package com.shimnssso.headonenglish.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.room.DatabaseSubject

@Composable
fun AppDrawer(
    selectedId: Int,
    subjects: List<DatabaseSubject>,
    navigateToHome: () -> Unit,
    closeDrawer: () -> Unit,
    changeSubject: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Spacer(Modifier.height(24.dp))
        HeadOnEnglishLogo(
            Modifier
                .background(MaterialTheme.colors.primary)
                .padding(16.dp), closeDrawer
        )
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
        DrawerButton(
            icon = Icons.Filled.Home,
            label = "Home",
            isSelected = false,
            action = {
                navigateToHome()
                closeDrawer()
            }
        )
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))

        subjects.forEach { subject ->
            DrawerButton(
                icon = Icons.Filled.Favorite,
                label = subject.title,
                isSelected = subject.subjectId == selectedId,
                action = {
                    changeSubject(subject.subjectId)
                    closeDrawer()
                }
            )
        }

        val activity = LocalContext.current as MainActivity
        Button(
            onClick = { activity.openFilePicker() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open")
        }
    }
}

@Composable
private fun HeadOnEnglishLogo(modifier: Modifier = Modifier, closeDrawer: () -> Unit) {
    val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.walking_broccoli) }
    val animationState = rememberLottieAnimationState(autoPlay = true, repeatCount = Integer.MAX_VALUE)
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        LottieAnimation(
            spec = animationSpec,
            modifier = Modifier.size(200.dp),
            animationState = animationState,
        )
        IconButton(onClick = { closeDrawer() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "close drawer", tint = MaterialTheme.colors.onPrimary)
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