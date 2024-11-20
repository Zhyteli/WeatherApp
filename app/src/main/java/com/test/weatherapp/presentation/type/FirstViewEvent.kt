package com.test.weatherapp.presentation.type

import com.google.android.gms.common.api.ResolvableApiException

sealed class FirstViewEvent {
    data class ShowEnableGpsDialog(val resolvableApiException: ResolvableApiException) :
        FirstViewEvent()
}

