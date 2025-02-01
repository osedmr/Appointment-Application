package com.example.appointment.data.repository

import com.example.appointment.data.datasource.VisaDataSource
import javax.inject.Inject

class VisaRepository @Inject constructor(var vds: VisaDataSource) {

    suspend fun getVisa(missionCountry:String) =vds.getVisa(missionCountry)

}