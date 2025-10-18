package io.github.mamedovilkin.weather.ui.screen.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.ui.theme.background
import io.github.mamedovilkin.weather.ui.theme.primary
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Composable
fun ErrorScreen(
    e: Exception,
    onRetry: () -> Unit = {},
) {
    val message = when (e.cause) {
        is SocketTimeoutException -> stringResource(R.string.server_timeout)
        is UnknownHostException -> stringResource(R.string.no_internet_connection)
        else -> stringResource(R.string.cannot_connect_to_server)
    }

    LaunchedEffect(Unit) {
        e.printStackTrace()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = stringResource(R.string.an_error_occurred),
            tint = primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.something_went_wrong),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
            )
        ) {
            Text(
                text = stringResource(R.string.retry),
                color = background
            )
        }
    }
}