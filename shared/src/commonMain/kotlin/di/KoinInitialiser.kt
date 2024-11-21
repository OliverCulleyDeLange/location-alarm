package di

import model.ui.AppViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { AppViewModel() }
}

fun appModule(): List<Module> = sharedModule + platformModule

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}