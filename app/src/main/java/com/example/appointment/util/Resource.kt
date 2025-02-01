package com.example.appointment.util

/**
 * Bu yapı tü m netword ve data işlemlerinizde uygulayabilirsiniz
 * Sealed class olması lazım. sadece belli sınıflar inherit etsin diye
 * Generic class <T> -> flexibility , reusable of data , different types of data
 * Success , Error, Loading
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data : T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}