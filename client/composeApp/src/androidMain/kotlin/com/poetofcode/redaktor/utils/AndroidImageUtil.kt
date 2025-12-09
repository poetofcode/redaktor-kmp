package com.poetofcode.redaktor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage

internal class AndroidImageUtil {

    @Composable
    fun AsyncImage(url: String, modifier: Modifier) {
        AsyncImage(model = url, contentDescription = null, modifier = modifier)
    }
}