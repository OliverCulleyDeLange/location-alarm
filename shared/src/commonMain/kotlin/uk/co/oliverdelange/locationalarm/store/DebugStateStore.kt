package uk.co.oliverdelange.locationalarm.store

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import uk.co.oliverdelange.locationalarm.model.domain.DebugState
import uk.co.oliverdelange.locationalarm.model.domain.Log
import uk.co.oliverdelange.locationalarm.model.domain.LogLevel

open class DebugStateStore {
    private val _state = MutableStateFlow(DebugState())

    @NativeCoroutinesState
    val state: StateFlow<DebugState> = _state.asStateFlow()

    fun onLog(date: Instant, level: LogLevel, message: String) {
        _state.update {
            it.copy(
                logs = it.logs.plus(Log(date, level, message))
            )
        }
    }
}
