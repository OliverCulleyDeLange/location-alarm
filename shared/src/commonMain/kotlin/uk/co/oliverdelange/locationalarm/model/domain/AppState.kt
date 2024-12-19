package uk.co.oliverdelange.locationalarm.model.domain

import uk.co.oliverdelange.locationalarm.model.ui.UiState
import uk.co.oliverdelange.locationalarm.model.ui.UiViewModel

/** Shared domain level app state
 * - Should contain domain objects like [Location], [PermissionState] and numbers like [Int]s
 * - Shouldn't contain many strings - the [UiViewModel] will map this state into [UiState], which is mostly strings and bools
 * */
interface AppState