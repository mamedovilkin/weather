package io.github.mamedovilkin.weather.ui.widget.forecast

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class RefreshAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters

    ) {
        ForecastWidget().update(context, glanceId)
    }
}