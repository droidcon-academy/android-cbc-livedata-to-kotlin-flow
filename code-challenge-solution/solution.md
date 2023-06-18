# Solution for Coding challenge

The question was to convert the `loading` `LiveData` to a `StateFlow` in the `MainViewModel`.

In the file `MainViewModel.kt` file

replace the `LiveData` with StateFLow and `MutableLiveData` with `MutableStateFlow`

```kotlin
private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
val loading = _loading.asStateFlow()
```

The test `fetchPassword should set loading and then clear loading` should still pass after the change.

