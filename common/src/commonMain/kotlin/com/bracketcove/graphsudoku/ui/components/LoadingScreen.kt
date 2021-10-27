package com.bracketcove.graphsudoku.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.ui.STR_LOADING
import com.bracketcove.graphsudoku.ui.STR_LOGO_DESCRIPTION
import com.bracketcove.graphsudoku.ui.lightGrey
import com.bracketcove.graphsudoku.ui.mainTitle

@Composable
fun LoadingScreen() {
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxHeight(.8f)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                //TODO find out how to show ic_launcher in shared project
                imageVector = Icons.Default.Done,
                modifier = Modifier.size(128.dp),
                contentDescription = STR_LOGO_DESCRIPTION
            )

            LinearProgressIndicator(
                color = lightGrey,
                modifier = Modifier
                    .width(128.dp)
                    .padding(16.dp)
            )

            Text(
                text = STR_LOADING,
                style = mainTitle.copy(color = MaterialTheme.colors.secondary),
                modifier = Modifier.wrapContentSize(),
            )
        }
    }
}