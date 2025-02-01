package com.example.appointment.di

import com.example.appointment.data.datasource.VisaDataSource
import com.example.appointment.data.repository.VisaRepository
import com.example.appointment.retrofit.ApiUtils
import com.example.appointment.retrofit.VisaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideVisaDao(): VisaDao {
        return ApiUtils.getVisa()
    }
    @Provides
    @Singleton
    fun provideVisaDataSource(vdao: VisaDao): VisaDataSource {
        return VisaDataSource(vdao)
    }
    @Provides
    @Singleton
    fun provideVisaRepository(vds: VisaDataSource): VisaRepository {
        return VisaRepository(vds)
    }

}