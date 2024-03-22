package com.security.passwordmanager.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.Time
import com.security.passwordmanager.util.animate
import com.security.passwordmanager.view.theme.PasswordManagerTheme


@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.toTime(): Time =
    Time(hours = hour, minutes = minute)


@Composable
fun RowScope.Time(
    time: Time,
    title: String,
    modifier: Modifier = Modifier,
    onClick: (time: Time, title: String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .align(Alignment.CenterVertically)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(8.dp))

        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable { onClick(time, title) }
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primaryContainer
                        ).map { it.animate() }
                    ),
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            val timeString = time.toString()

            repeat(timeString.length) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .border(
                            width = 0.2.dp,
                            color = MaterialTheme.colorScheme.onBackground
                                .copy(alpha = 0.3f)
                                .animate()
                        )
                        .padding(7.dp)
                ) {
                    Text(
                        text = timeString[index].toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.animate()
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun TimesPreview() {
    PasswordManagerTheme(isDarkTheme = false, dynamicColor = false) {
        val sunriseTime by remember { mutableStateOf(Time(14, 29)) }
        val sunsetTime by remember { mutableStateOf(Time(23, 1)) }

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
        ) {
            Time(
                time = sunriseTime,
                title = stringResource(R.string.sunrise_time),
                modifier = Modifier.weight(1f),
                onClick = { _, _ ->  }
            )
            Time(
                time = sunsetTime,
                title = stringResource(R.string.sunset_time),
                modifier = Modifier.weight(1f),
                onClick = { _, _ -> }
            )
        }
    }
}
