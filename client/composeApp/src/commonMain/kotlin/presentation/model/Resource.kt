package presentation.model

sealed interface Resource<out T>

data object IdleResource : Resource<Nothing>

data class CompleteResource<T>(val result: T) : Resource<T>

data class ExceptionResource(val exception: Throwable) : Resource<Nothing>

data object LoadingResource : Resource<Nothing>
