package assignment.sample.coding.di


import android.app.Application
import android.content.Context
import assignment.sample.coding.data.repository.FakeVehicleRepositoryImpl
import assignment.sample.coding.presentation.listvehicle.VehicleViewModel
import assignment.sample.coding.presentation.map.MapViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("fake_vehicle_view_model")
    fun provideFakeVehicleViewModel(
        @ApplicationContext context: Context
    ) = VehicleViewModel(context.applicationContext as Application, FakeVehicleRepositoryImpl())

    @Provides
    @Named("fake_map_view_model")
    fun provideFakeMapViewModel(
        @ApplicationContext context: Context
    ) = MapViewModel(context.applicationContext as Application, FakeVehicleRepositoryImpl())

}