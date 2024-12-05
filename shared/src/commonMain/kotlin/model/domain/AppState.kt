package model.domain

import model.ui.UiState
import model.ui.UiViewModel

/** Shared domain level app state
 * - Should contain domain objects like [Location], [PermissionState] and numbers like [Int]s
 * - Shouldn't contain many strings - the [UiViewModel] will map this state into [UiState], which is mostly strings and bools
 * */
interface AppState