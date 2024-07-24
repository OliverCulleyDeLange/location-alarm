package di

import EventHandler
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::EventHandler)
}

fun appModule() = listOf(sharedModule)

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}