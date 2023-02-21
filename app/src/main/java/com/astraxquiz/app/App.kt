package com.astraxquiz.app

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class App : Application()  {
    private val YANDEX_API_KEY = "c9117362-3d21-490a-aa5a-2cc1a145c8f6"

    companion object{
        private lateinit var myApp: App
    }

    override fun onCreate() {
        super.onCreate()
        myApp = this
        val config = YandexMetricaConfig.newConfigBuilder(YANDEX_API_KEY).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }

}