package com.bracketcove.graphsudoku.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.R

@Composable
actual fun AppIcon() {
    Image(
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = "App Icon",
        modifier = Modifier.size(128.dp)
    )
}