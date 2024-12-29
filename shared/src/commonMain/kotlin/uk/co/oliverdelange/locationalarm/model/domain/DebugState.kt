package uk.co.oliverdelange.locationalarm.model.domain

data class DebugState(
    val logs: List<Log> = emptyList()
)