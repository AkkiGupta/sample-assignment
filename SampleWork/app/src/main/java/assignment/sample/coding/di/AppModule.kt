package assignment.sample.coding.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import assignment.sample.coding.data.repository.VehicleRepositoryImpl
import assignment.sample.coding.data.source.remote.VehicleApi
import assignment.sample.coding.data.util.Constants.BASE_URL
import assignment.sample.coding.domain.repository.VehicleRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * AppModule provides all the dependencies using Hilt-Dependency Injection
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides Vehicle Repository
     * @param vehicleApi Retrofit API interface of [VehicleApi]
     *
     * Return [VehicleRepositoryImpl]
     */
    @Singleton
    @Provides
    fun provideVehicleRepositoryImpl(
        vehicleApi: VehicleApi
    ) = VehicleRepositoryImpl(vehicleApi) as VehicleRepository

    /**
     * Provides Vehicle API
     * @param retrofit Retrofit Instance
     *
     * Return [VehicleApi]
     */
    @Singleton
    @Provides
    fun provideVehicleApi(
        retrofit: Retrofit
    ): VehicleApi {
        return retrofit.create(VehicleApi::class.java)
    }

    /**
     * Provides Retrofit Instance
     * @param gsonConverterFactory [GsonConverterFactory] to handle response converters
     * @param okHttpClient [OkHttpClient] to provide custom HTTP Client to intercept or
     * logging purpose
     *
     * Return [Retrofit] instance
     */
    @Singleton
    @Provides
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
//            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }

    /**
     * Provides OkHttp Instance
     * @param context [Context] Application context to build HTTP Client
     *
     * Return [OkHttpClient] Http Client
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val cache = Cache(context.cacheDir, cacheSize)
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .cache(cache) // make your app offline-friendly without a database!
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(loggingInterceptor)
        return okHttpClient.build()
    }

    /**
     * Provide GSON instance
     *
     * return [Gson]
     */
    @Provides
    @Singleton
    fun providesGson(): Gson {
        return Gson()
    }

    /**
     * Provide GSONConverter Factory instance
     *
     * return [GsonConverterFactory]
     */
    @Provides
    @Singleton
    fun providesGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    /**
     * Check if network is available or not
     *
     * @return [Boolean] true if available, false otherwise
     */
    @Provides
    @Singleton
    fun provideIsNetworkAvailable(@ApplicationContext context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}