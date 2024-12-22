package uk.co.oliverdelange.locationalarm.helper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> doWhen(boolFlow: Flow<Boolean>, exec: () -> Flow<T>) {
    boolFlow.distinctUntilChanged()
        .flatMapLatest { debug ->
            if (debug) {
                exec()
            } else emptyFlow()
        }.collect()
}