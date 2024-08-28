package com.shahbaz.workmanager.di

import com.google.firebase.storage.FirebaseStorage
import com.shahbaz.workmanager.repo.UploadImageRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideImageUploaderRepo(firebaseStorage: FirebaseStorage): UploadImageRepo {
        return UploadImageRepo(firebaseStorage)
    }
}