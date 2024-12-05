package model.ui

import kotlinx.coroutines.flow.Flow
import model.domain.AppState

/** Defines an app side UI view model which provides UiState based on AppState */
interface UiViewModel<A : AppState, U : UiState> {
    val uiState: Flow<U>

    /** A function that maps [AppState] (domain) into [UiState] (ui) */
    fun mapUiState(state: A): U
}