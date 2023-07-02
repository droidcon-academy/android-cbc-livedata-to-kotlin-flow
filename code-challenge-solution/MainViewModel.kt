// The question was to convert the `loading` `LiveData` to a `StateFlow` in the `MainViewModel`.

private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
val loading = _loading.asStateFlow()
