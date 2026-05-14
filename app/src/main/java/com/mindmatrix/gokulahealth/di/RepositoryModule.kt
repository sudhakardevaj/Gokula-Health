package com.mindmatrix.gokulahealth.di

import com.mindmatrix.gokulahealth.data.repository.CattleRepositoryImpl
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import com.mindmatrix.gokulahealth.domain.repository.GenAIRepository
import com.mindmatrix.gokulahealth.domain.repository.GenAIRepositoryImpl
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
    abstract fun bindCattleRepository(
        cattleRepositoryImpl: CattleRepositoryImpl
    ): CattleRepository

    @Binds
    @Singleton
    abstract fun bindGenAIRepository(
        genAIRepositoryImpl: GenAIRepositoryImpl
    ): GenAIRepository
    // ✅ PRESERVED: No changes needed here - was already correct!
}