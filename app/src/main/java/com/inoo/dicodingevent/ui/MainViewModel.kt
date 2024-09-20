package com.inoo.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inoo.dicodingevent.data.response.EventResponse
import com.inoo.dicodingevent.data.response.ListEventsItem
import com.inoo.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _activeEvents = MutableLiveData<List<ListEventsItem>>()
    val activeEvents: LiveData<List<ListEventsItem>> get() = _activeEvents

    private val _inactiveEvents = MutableLiveData<List<ListEventsItem>>()
    val inactiveEvents: LiveData<List<ListEventsItem>> get() = _inactiveEvents

    private val _error = MutableLiveData<Error?>()
    val error: MutableLiveData<Error?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> get() = _searchResults

    var isSearching = false

    fun fetchActiveEvents() {
        isSearching = false
        _isLoading.value = true
        viewModelScope.launch {
            ApiConfig.getApiService().getActiveEvent().enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _activeEvents.value = response.body()?.listEvents ?: emptyList()
                    } else {
                        _error.value = Error("Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = Error("Failure: ${t.message}")
                }
            })
        }
    }

    fun fetchInactiveEvents() {
        isSearching = false
        _isLoading.value = true
        viewModelScope.launch {
            ApiConfig.getApiService().getInactiveEvent().enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _inactiveEvents.value = response.body()?.listEvents ?: emptyList()
                    } else {
                        _error.value = Error("Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = Error("Failure: ${t.message}")
                }
            })
        }
    }

    fun searchActiveEvents(query: String) {
        isSearching = true
        _isLoading.value = true
        viewModelScope.launch {
            ApiConfig.getApiService().searchEvent(1, query).enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val events = response.body()?.listEvents ?: emptyList()
                        _searchResults.value = events
                    } else {
                        _error.value = Error("Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = Error("Failure: ${t.message}")
                }
            })
        }
    }

    fun searchInactiveEvents(query: String) {
        isSearching = true
        _isLoading.value = true
        viewModelScope.launch {
            ApiConfig.getApiService().searchEvent(0, query).enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    _isLoading.value = false
                    val events = response.body()?.listEvents ?: emptyList()
                    _searchResults.value = events
                    if (events.isEmpty()) {
                        _error.value = Error("Data tidak ditemukan")
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = Error("Failure: ${t.message}")
                }
            })
        }
    }

    fun clearSearchResults() {
        isSearching = false
        _searchResults.value = emptyList()
    }
}
