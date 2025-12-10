package data.utils

fun <T> List<T>.swap(i: Int, j: Int) : List<T> {
    val mutableList = this.toMutableList()
    val t = mutableList[i]
    mutableList[i] = mutableList[j]
    mutableList[j] = t
    return mutableList
}