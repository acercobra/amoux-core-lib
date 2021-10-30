package utils


fun Int.toSizeString(): String {
    val aKB = 1024.0
    if (this < aKB) {
        return "$this bytes"
    }

    val aMB = aKB * 1024.0
    if (this < aMB) {
        return "${this.div(aKB)} KB"
    }

    val aGB = aMB * 1024.0
    if (this < aGB) {
        return "${this.div(aMB)} MB"
    }

    return "${this.div(aGB)} GB"
}