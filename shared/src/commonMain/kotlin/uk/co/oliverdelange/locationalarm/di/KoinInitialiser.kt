package uk.co.oliverdelange.locationalarm.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import uk.co.oliverdelange.locationalarm.model.domain.AppViewModel

val sharedModule = module {
    single { AppViewModel() }
}

fun appModule(): List<Module> = listOf(sharedModule)

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}