package com.kotlinenjoyers.trackiteasy.ui.common

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import com.kotlinenjoyers.trackiteasy.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun Loading(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.wrapContentHeight(Alignment.CenterVertically),
        text = "${stringResource(id = R.string.loading)}...",
        style = MaterialTheme.typography.displayMedium,
    )
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.parcel_anim)
    var atEnd by remember { mutableStateOf(false) }
    val painterFirst = rememberAnimatedVectorPainter(animatedImageVector = image, atEnd = atEnd)
    val painterSecond = rememberAnimatedVectorPainter(animatedImageVector = image, atEnd = !atEnd)

    suspend fun runAnimation() {
        while (true) {
            atEnd = !atEnd
            delay(image.totalDuration.toLong() + 100)
        }
    }
    LaunchedEffect(image) {
        runAnimation()
    }
    Image(
        modifier = modifier,
        painter = if (atEnd) painterFirst else painterSecond,
        contentDescription = null,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
    )
}