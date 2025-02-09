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
class WorldViewModel @Inject constructor(private val vrepo: VisaRepository) : ViewModel() {

    // World verilerini içeren LiveData (10'ar parça olacak)
    val response10World: MutableLiveData<Resource<List<Visa>>> = MutableLiveData()

    // Sayfalama değişkenleri
    var currentPage = 1
    private val pageSize = 10
    private var fullVisaList: List<Visa> = emptyList()
    private var filteredList: List<Visa> = emptyList()

    var hasFirstPostSeen: Boolean = false
    var isPagination: Boolean = false
        private set
    var isRefreshing: Boolean = false
        private set
    var isLastPage: Boolean = false

    private var currentDestination: String = "All"
    private var currentOrigin: String = "All"

    init {
        fetchAllData()
    }

    private fun fetchAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            response10World.postValue(Resource.Loading())
            try {
                val response = vrepo.getWorld()
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        fullVisaList = list
                        // İlk yüklemede tüm listeyi filtrele ve ilk sayfayı göster
                        filterAndShowFirstPage()
                    }
                } else {
                    response10World.postValue(Resource.Error("Veri alınamadı: ${response.code()}"))
                }
            } catch (e: Exception) {
                response10World.postValue(Resource.Error("Hata: ${e.message}"))
            }
        }
    }

    private fun filterAndShowFirstPage() {
        viewModelScope.launch(Dispatchers.Default) {
            // Filtreleme işlemi
            filteredList = fullVisaList.filter { visa ->
                (currentDestination == "All" || visa.missionCountry == currentDestination) &&
                (currentOrigin == "All" || visa.sourceCountry == currentOrigin)
            }

            // İlk sayfayı göster
            val initialPage = filteredList.take(pageSize)
            response10World.postValue(Resource.Success(initialPage))
            isLastPage = filteredList.size <= pageSize
        }
    }

    fun filterVisas(destinationCountry: String, originCountry: String) {
        viewModelScope.launch(Dispatchers.Default) {
            currentDestination = destinationCountry
            currentOrigin = originCountry
            currentPage = 1
            hasFirstPostSeen = false
            isPagination = false
            isLastPage = false

            // Filtreleme ve ilk sayfayı gösterme
            filterAndShowFirstPage()
        }
    }

    fun loadMoreWorld() {
        if (!isLastPage && !isPagination) {
            viewModelScope.launch(Dispatchers.Default) {
                isPagination = true
                currentPage++

                val startIndex = (currentPage - 1) * pageSize
                if (startIndex < filteredList.size) {
                    val nextPage = filteredList.drop(startIndex).take(pageSize)
                    response10World.postValue(Resource.Success(nextPage))
                    isLastPage = (startIndex + pageSize) >= filteredList.size
                } else {
                    isLastPage = true
                }
                isPagination = false
            }
        }
    }

    fun refreshWorld() {
        isRefreshing = true
        currentPage = 1
        hasFirstPostSeen = false
        isPagination = false
        isLastPage = false
        fetchAllData()
    }
}
