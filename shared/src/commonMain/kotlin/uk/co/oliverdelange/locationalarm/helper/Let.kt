package uk.co.oliverdelange.locationalarm.helper

fun <A, B, R> ifNotNull(a: A?, b: B?, action: (A, B) -> R): R? {
    return if (a != null && b != null) {
        action(a, b)
    } else null
}