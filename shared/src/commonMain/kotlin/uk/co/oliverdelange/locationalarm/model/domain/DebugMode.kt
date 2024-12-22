package uk.co.oliverdelange.locationalarm.model.domain

import uk.co.oliverdelange.locationalarm.model.domain.DebugMode.VolumeButton.Down
import uk.co.oliverdelange.locationalarm.model.domain.DebugMode.VolumeButton.Up

class DebugMode(val appStateStore: AppStateStore) {
    var keyPresses: MutableList<VolumeButton> = mutableListOf()
    val pattern = listOf(Up, Down, Up, Down)

    enum class VolumeButton {
        Up, Down
    }

    fun onVolumeButton(button: VolumeButton) {
        keyPresses.add(button)
        if (keyPresses.takeLast(4) == pattern) {
            appStateStore.setDebug(!appStateStore.state.value.debug)
        }
    }
}