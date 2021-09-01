package assignment.sample.coding.presentation.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import assignment.sample.coding.MainCoroutineRule
import assignment.sample.coding.data.repository.FakeVehicleRepositoryImpl
import assignment.sample.coding.data.util.Response
import assignment.sample.coding.getOrAwaitValue
import assignment.sample.coding.launchFragmentInHiltContainer
import assignment.sample.coding.presentation.listvehicle.VehicleFragment
import assignment.sample.coding.util.Constants
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class MapViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get: Rule
    var instanTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    @Named("fake_map_view_model")
    lateinit var fakeMapViewModel: MapViewModel

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun listVehiclesWithInValidNELat_returnsError() {
        fakeMapViewModel.listVehiclesInGivenBound(
            91.0, Constants.HAMBURG_NE_LON, Constants.HAMBURG_SW_LAT,
            Constants.HAMBURG_SW_LON,
        )

        val response = fakeMapViewModel.observeVehicleListChange().getOrAwaitValue()

        Truth.assertThat(
            response.getContentIfNotAlreadyHandled()?.message
        ).isEqualTo(
            Constants.INVALID_LATITUDE
        )
    }

    @Test
    fun listVehiclesWithInValidNELon_returnsError() {
        fakeMapViewModel.listVehiclesInGivenBound(
            Constants.HAMBURG_NE_LAT, 181.0, Constants.HAMBURG_SW_LAT,
            Constants.HAMBURG_SW_LON,
        )

        val response = fakeMapViewModel.observeVehicleListChange().getOrAwaitValue()

        Truth.assertThat(
            response.getContentIfNotAlreadyHandled()?.message
        ).isEqualTo(
            Constants.INVALID_LONGITUDE
        )
    }

    @Test
    fun listVehiclesWithInValidSWLat_returnsError() {
        fakeMapViewModel.listVehiclesInGivenBound(
            Constants.HAMBURG_NE_LAT, Constants.HAMBURG_NE_LON, -91.0,
            Constants.HAMBURG_SW_LON,
        )

        val response = fakeMapViewModel.observeVehicleListChange().getOrAwaitValue()

        Truth.assertThat(
            response.getContentIfNotAlreadyHandled()?.message
        ).isEqualTo(
            Constants.INVALID_LATITUDE
        )
    }

    @Test
    fun listVehiclesWithInValidSWLon_returnsError() {
        fakeMapViewModel.listVehiclesInGivenBound(
            Constants.HAMBURG_NE_LAT, Constants.HAMBURG_NE_LON, Constants.HAMBURG_SW_LAT,
            -181.0,
        )

        val response = fakeMapViewModel.observeVehicleListChange().getOrAwaitValue()

        Truth.assertThat(
            response.getContentIfNotAlreadyHandled()?.message
        ).isEqualTo(
            Constants.INVALID_LONGITUDE
        )
    }

    @Test
    fun listVehiclesWithValidLatLon_returnsLoadingFirst() {
        FakeVehicleRepositoryImpl.setShouldReturnNetworkError(false)
        fakeMapViewModel.listVehiclesInGivenBound(
            Constants.HAMBURG_NE_LAT, Constants.HAMBURG_NE_LON, Constants.HAMBURG_SW_LAT,
            Constants.HAMBURG_SW_LON,
        )

        var loading = false
        when (fakeMapViewModel.observeVehicleListChange().getOrAwaitValue()
            .getContentIfNotAlreadyHandled()) {

            is Response.Loading -> {
                loading = true
            }
        }

        Truth.assertThat(loading).isTrue()
    }

    @Test
    fun listVehiclesWithValidLatLon_returnsNetworkError() {
        FakeVehicleRepositoryImpl.setShouldReturnNetworkError(true)
        fakeMapViewModel.listVehiclesInGivenBound(
            Constants.HAMBURG_NE_LAT, Constants.HAMBURG_NE_LON,
            Constants.HAMBURG_SW_LAT, Constants.HAMBURG_SW_LON,
        )

        var message: String? = null

        when (val response =
            fakeMapViewModel.observeVehicleListChange().getOrAwaitValue(responseExpected = 2)
                .getContentIfNotAlreadyHandled()) {
            is Response.Error -> {
                message = response.message.toString()
            }
        }

        Truth.assertThat(message).isEqualTo(Constants.NETWORK_ERROR)
    }

    @Test
    fun listVehiclesWithValidLatLon_returnsList() {
        FakeVehicleRepositoryImpl.setShouldReturnNetworkError(false)
        fakeMapViewModel.listVehiclesInGivenBound(
            Constants.HAMBURG_NE_LAT, Constants.HAMBURG_NE_LON, Constants.HAMBURG_SW_LAT,
            Constants.HAMBURG_SW_LON,
        )

        var dataAvailable = false
        when (val response =
            fakeMapViewModel.observeVehicleListChange().getOrAwaitValue(responseExpected = 2)
                .getContentIfNotAlreadyHandled()) {
            is Response.Success -> {
                dataAvailable = (response.data != null)
            }
        }

        Truth.assertThat(dataAvailable).isTrue()
    }

    @Test
    fun testLaunchFragment() {
        launchFragmentInHiltContainer<VehicleFragment> {  }
    }

}