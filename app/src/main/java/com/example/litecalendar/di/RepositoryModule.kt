package com.example.litecalendar.di

import com.example.litecalendar.data.repository.EventServiceImpl
import com.example.litecalendar.data.repository.HolidayServiceImpl
import com.example.litecalendar.domain.repository.EventService
import com.example.litecalendar.domain.repository.HolidayService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEventRepository(eventRepositoryImpl: EventServiceImpl) : EventService

    @Binds
    @Singleton
    abstract fun bindHolidayRepository(holidayRepositoryImpl : HolidayServiceImpl) : HolidayService
}