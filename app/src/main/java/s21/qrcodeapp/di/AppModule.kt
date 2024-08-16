package s21.qrcodeapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import s21.qrcodeapp.data.datasource.network.api.RocketChatApiService
import s21.qrcodeapp.data.datasource.network.api.RocketChatApiServiceImpl
import s21.qrcodeapp.data.datasource.preferences.PreferencesDataSource
import s21.qrcodeapp.data.repository.QRCodeRepositoryImpl
import s21.qrcodeapp.domain.repository.QRCodeRepository
import s21.qrcodeapp.domain.usecase.GetQRCodeUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesDataSource(@ApplicationContext context: Context): PreferencesDataSource {
        return PreferencesDataSource(context)
    }

    private const val WEB_SOCKET_URL = "wss://rocketchat-student.21-school.ru/websocket"

    @Provides
    @Singleton
    fun provideRocketChatApiService(): RocketChatApiService {
        return RocketChatApiServiceImpl(webSocketUri = WEB_SOCKET_URL)
    }

    @Provides
    @Singleton
    fun provideQRCodeRepository(
        preferencesDataSource: PreferencesDataSource,
        apiService: RocketChatApiService
    ): QRCodeRepository {
        return QRCodeRepositoryImpl(preferencesDataSource, apiService)
    }

    @Provides
    @Singleton
    fun provideGetQRCodeUseCase(repository: QRCodeRepository): GetQRCodeUseCase {
        return GetQRCodeUseCase(repository)
    }
}