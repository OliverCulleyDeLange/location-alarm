package uk.co.oliverdelange.locationalarm.model.ui.debug

enum class LogColor {
    Red, Orange, Green, Blue, Grey
}

data class LogEntryUiModel(
    val date: String,
    val message: String,
    val color: LogColor
)
