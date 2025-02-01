package com.example.appointment.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Visa(
    @SerializedName("source_country") val sourceCountry: String?,
    @SerializedName("mission_country") val missionCountry: String?,
    @SerializedName("visa_type_id") val visaTypeId: Int?,
    @SerializedName("visa_category") val visaCategory: String?,
    @SerializedName("visa_subcategory") val visaSubcategory: String?,
    @SerializedName("people_looking") val peopleLooking: Int?,
    @SerializedName("center_name") val centerName: String?,
    @SerializedName("appointment_date") val appointmentDate: String?,
    @SerializedName("book_now_link") val bookNowLink: String?,
    @SerializedName("last_checked") val lastChecked: String?
) : Serializable


