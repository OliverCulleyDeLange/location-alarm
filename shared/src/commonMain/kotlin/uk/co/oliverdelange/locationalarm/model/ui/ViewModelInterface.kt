package uk.co.oliverdelange.locationalarm.model.ui

/** To allow passing a viewmodel into previews */
interface ViewModelInterface {
    fun onEvent(uiEvent: UiEvents)
}


class EmptyViewModel : ViewModelInterface {
    override fun onEvent(uiEvent: UiEvents) {}
}