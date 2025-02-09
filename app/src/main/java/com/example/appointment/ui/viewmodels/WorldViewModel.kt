package com.example.appointment.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appointment.data.model.Visa
import com.example.appointment.data.repository.VisaRepository
import com.example.appointment.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class WorldViewModel @Inject constructor(private val vrepo: VisaRepository) : ViewModel() {

    private val _response10World = MutableLiveData<Resource<List<Visa>>>()
    val response10World: LiveData<Resource<List<Visa>>> = _response10World

    private val _countries = MutableLiveData<Resource<Set<String>>>()
    val countries: LiveData<Resource<Set<String>>> = _countries

    private var fullVisaList: List<Visa> = emptyList()
    
    private var filterJob: Job? = null
    
    var isRefreshing: Boolean = false
        private set

    private var currentDestination: String = "All"
    private var currentOrigin: String = "All"

    init {
        fetchAllData()
    }

    override fun onCleared() {
        super.onCleared()
        filterJob?.cancel()
    }

    private fun fetchAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            _response10World.postValue(Resource.Loading())
            try {
                val response = vrepo.getWorld()
                withContext(Dispatchers.Default) {
                    if (response.isSuccessful) {
                        response.body()?.let { list ->
                            fullVisaList = list.toList()
                            
                            // Ülke listelerini oluştur
                            val sourceCountries = list.mapNotNull { it.sourceCountry }.toSet()
                            val missionCountries = list.mapNotNull { it.missionCountry }.toSet()
                            
                            // Tüm ülkeleri birleştir ve sırala
                            val allCountries = (sourceCountries + missionCountries).toSortedSet()
                            
                            // "All" seçeneğini en başa ekle
                            val countriesWithAll = setOf("All") + allCountries
                            
                            _countries.postValue(Resource.Success(countriesWithAll))
                            filterVisaList()
                        }
                    } else {
                        _response10World.postValue(Resource.Error("Veri alınamadı: ${response.code()}"))
                    }
                }
            } catch (e: Exception) {
                _response10World.postValue(Resource.Error("Hata: ${e.message}"))
            }
        }
    }

    private fun filterVisaList() {
        // Önceki filtreleme işlemini iptal et
        filterJob?.cancel()
        
        filterJob = viewModelScope.launch(Dispatchers.Default) {
            try {
                val filteredList = fullVisaList.filter { visa ->
                    (currentDestination == "All" || visa.missionCountry == currentDestination) &&
                    (currentOrigin == "All" || visa.sourceCountry == currentOrigin)
                }
                _response10World.postValue(Resource.Success(filteredList))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _response10World.postValue(Resource.Error("Filtreleme hatası: ${e.message}"))
            }
        }
    }

    fun filterVisas(destinationCountry: String, originCountry: String) {
        if (currentDestination == destinationCountry && currentOrigin == originCountry) {
            return // Aynı filtre parametreleri varsa işlem yapma
        }
        currentDestination = destinationCountry
        currentOrigin = originCountry
        filterVisaList()
    }

    fun refreshWorld() {
        if (isRefreshing) return
        isRefreshing = true
        fetchAllData()
        isRefreshing = false
    }
}
