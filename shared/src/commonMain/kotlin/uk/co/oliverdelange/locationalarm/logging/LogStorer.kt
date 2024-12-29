package uk.co.oliverdelange.locationalarm.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import uk.co.oliverdelange.locationalarm.provider.TimeProvider
import uk.co.oliverdelange.locationalarm.store.DebugStateStore

/** When debug flag is enabled, we store all logs in state to allow viewing in the debug screen */
class LogStorer(
    private val debug: Flow<Boolean>,
    private val debugStateStore: DebugStateStore,
    private val timeProvider: TimeProvider,
) {
    init {
        storeLogsWhenDebug()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun storeLogsWhenDebug() {
        GlobalScope.launch {
            debug.distinctUntilChanged().collect {
                if (it) {
                    SLog.w("StoringLogger added")
                    Logger.setLogWriters(platformLogWriter(), StoringLogger(debugStateStore, timeProvider))
                } else {
                    SLog.w("StoringLogger removed")
                    Logger.setLogWriters(platformLogWriter())
                }
            }
        }
    }
}