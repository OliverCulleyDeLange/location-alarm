package di

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import uk.co.oliverdelange.locationalarm.di.sharedModule
import uk.co.oliverdelange.locationalarm.logging.SLog

class KoinProvider {
    companion object {
        lateinit var koin: Koin
        fun initKoin() {
            val instance = startKoin {
                modules(sharedModule + iosModule)
            }

            koin = instance.koin
            SLog.i("Koin Initialised")
        }
    }
}

@OptIn(BetaInteropApi::class)
fun Koin.get(objCClass: ObjCClass): Any {
    val kClazz = getOriginalKotlinClass(objCClass)!!
    return get(kClazz, null, null)
}

@OptIn(BetaInteropApi::class)
fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?, parameter: Any): Any {
    val kClazz = getOriginalKotlinClass(objCClass)!!
    return get(kClazz, qualifier) { parametersOf(parameter) }
}
