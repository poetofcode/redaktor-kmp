package data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

fun <T> Flow<T>.flowOnDefaultContext() : CommonFlow<T> {
    val context = Dispatchers.Default
    return this.flowOn(context)
}