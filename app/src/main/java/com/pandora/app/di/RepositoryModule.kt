package com.pandora.app.di

import com.pandora.app.data.repository.ItemRepository
import com.pandora.app.data.repository.ItemRepositoryImpl
import com.pandora.app.data.repository.OrganizationRepository
import com.pandora.app.data.repository.OrganizationRepositoryImpl
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
    abstract fun bindItemRepository(
        impl: ItemRepositoryImpl
    ): ItemRepository

    @Binds
    @Singleton
    abstract fun bindOrganizationRepository(
        impl: OrganizationRepositoryImpl
    ): OrganizationRepository
}
