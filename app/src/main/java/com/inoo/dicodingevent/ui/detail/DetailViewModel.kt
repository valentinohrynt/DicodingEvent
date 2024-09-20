package com.inoo.dicodingevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inoo.dicodingevent.data.response.ListEventsItem
import com.inoo.dicodingevent.data.response.SpecificEventResponse
import com.inoo.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _eventDetail = MutableLiveData<ListEventsItem>()
    val eventDetail: LiveData<ListEventsItem> get() = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchEventDetail(id: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getEventDetail(id)
        client.enqueue(object: Callback<SpecificEventResponse> {
            override fun onResponse(
                call: Call<SpecificEventResponse>,
                response: Response<SpecificEventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("DetailViewModel", "Response Body: $body")
                    _eventDetail.value = body?.event
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            }
            override fun onFailure(call: Call<SpecificEventResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error: ${t.message}"
            }
        })
    }
}

