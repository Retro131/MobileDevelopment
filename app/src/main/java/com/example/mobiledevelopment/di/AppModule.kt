package com.example.mobiledevelopment.di

import com.example.mobiledevelopment.data.GameDatabase
import com.example.mobiledevelopment.data.GameResultRepository
import com.example.mobiledevelopment.data.PlayerRepository
import com.example.mobiledevelopment.game.GameViewModel
import com.example.mobiledevelopment.gold.GoldQuoteStorage
import com.example.mobiledevelopment.gold.GoldRepository
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

    single {
        GoldQuoteStorage(androidContext())
    }

    single {
        GoldRepository(
            storage = get()
        )
    }

    viewModelOf(::GameViewModel)
    viewModelOf(::SessionViewModel)
}