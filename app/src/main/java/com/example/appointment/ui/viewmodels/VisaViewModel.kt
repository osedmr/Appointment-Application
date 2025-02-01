package com.example.appointment.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appointment.data.model.Visa
import com.example.appointment.data.repository.VisaRepository
import com.example.appointment.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class VisaViewModel @Inject constructor(var vrepo: VisaRepository) : ViewModel() {

    val visaList : MutableLiveData<Resource<List<Visa>>> = MutableLiveData()

    var isRefreshing = false
        set(value) {
            field = value
        }

    fun visaRequest(missionCountry:String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (!isRefreshing) {
                visaList.postValue(Resource.Loading())
            }
            val response = vrepo.getVisa(missionCountry)
            visaList.postValue(handleListResponse(response))
            isRefreshing = false
        }
    }

    private fun handleListResponse(response: Response<List<Visa>>) : Resource<List<Visa>> {
        if (response.isSuccessful){
            response.body()?.let {myResponse ->
                return Resource.Success(myResponse)
            }
        }
        return Resource.Error("Hata Oluştu ${response.errorBody()} - ${response.code()}")

    }
}