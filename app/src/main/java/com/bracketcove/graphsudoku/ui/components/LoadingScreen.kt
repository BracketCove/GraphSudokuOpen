package com.bracketcove.graphsudoku.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.R


@Composable
fun LoadingScreen() {
    Surface(
        color = if (MaterialTheme.colors.isLight) primaryGreen else primaryCharcoal,
        modifier = Modifier
            .fillMaxHeight(.8f)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LogoSlideIn()

            LinearProgressIndicator(
                color = lightGrey,
                modifier = Modifier
                    .width(128.dp)
                    .padding(16.dp)
            )

            TextAlphaIn(text = stringResource(id = R.string.loading))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LogoSlideIn() {
    AnimatedVisibility(
        visible = true,
        initiallyVisible = false,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 200
            )
        )
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            modifier = Modifier.size(128.dp),
            contentDescription = stringResource(R.string.logo_description)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TextAlphaIn(text: String) {
    AnimatedVisibility(
        visible = true,
        initiallyVisible = false,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 200
            )
        )
    ) {
        Text(
            text = text,
            style = mainTitle.copy(color = MaterialTheme.colors.secondary),
            modifier = Modifier.wrapContentSize()
        )
    }
}