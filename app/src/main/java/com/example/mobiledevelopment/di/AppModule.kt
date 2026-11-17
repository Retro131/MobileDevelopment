package com.example.mobiledevelopment.di

import com.example.mobiledevelopment.data.GameDatabase
import com.example.mobiledevelopment.data.GameResultRepository
import com.example.mobiledevelopment.data.PlayerRepository
import com.example.mobiledevelopment.session.SessionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single {
        GameDatabase.getInstance(androidContext())
    }

    single {
        get<GameDatabase>().playerDao()
    }

    single {
        get<GameDatabase>().gameResultDao()
    }

    single {
        PlayerRepository(get())
    }

    single {
        GameResultRepository(get())
    }

    viewModelOf(::SessionViewModel)
}