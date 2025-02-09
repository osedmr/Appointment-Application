package com.example.appointment.data.datasource

import com.example.appointment.retrofit.VisaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VisaDataSource @Inject constructor(var vdao: VisaDao)  {

    suspend fun getVisa(missionCountry:String) = withContext(Dispatchers.IO){
        vdao.visa(missionCountry)
    }

    suspend fun getWorld() = withContext(Dispatchers.IO){
        vdao.world()
    }


}