package com.yusufjon.unitconverter.app

import android.app.Application

class UnitConverterApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(this)
    }
}
