package uk.co.oliverdelange.locationalarm.model.ui

/** To allow passing a viewmodel into previews */
interface MapViewModelInterface {
    fun onEvent(uiEvent: UiEvents)
}


class EmptyMapViewModel : MapViewModelInterface {
    override fun onEvent(uiEvent: UiEvents) {}
}