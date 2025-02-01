package com.example.appointment.retrofit

import com.example.appointment.data.model.Visa
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VisaDao {


    @GET("/getappointmentbycountry/{missionCountry}")
    suspend fun visa(
        @Path("missionCountry") missionCountry :String
    ) : Response<List<Visa>>


}