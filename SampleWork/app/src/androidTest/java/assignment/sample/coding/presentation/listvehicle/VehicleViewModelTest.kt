package assignment.sample.coding.presentation.listvehicle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import assignment.sample.coding.MainCoroutineRule
import assignment.sample.coding.data.repository.FakeVehicleRepositoryImpl
import assignment.sample.coding.data.util.Response
import assignment.sample.coding.getOrAwaitValue
import assignment.sample.coding.util.Constants
import assignment.sample.coding.util.Constants.HAMBURG_NE_LAT
import assignment.sample.coding.util.Constants.HAMBURG_NE_LON
import assignment.sample.coding.util.Constants.HAMBURG_SW_LAT
import assignment.sample.coding.util.Constants.HAMBURG_SW_LON
import assignment.sample.coding.util.Constants.INVALID_LATITUDE
import assignment.sample.coding.util.Constants.INVALID_LONGITUDE
import assignment.sample.coding.util.Constants.NETWORK_ERROR
import com.google.common.truth.Truth.assertThat
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
class VehicleViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get: Rule
    var instanTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    @Named("fake_vehicle_view_model")
    lateinit var fakeVehicleViewModel: VehicleViewModel

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun listVehiclesWithInValidNELat_returnsError() {
        fakeVehicleViewModel.listVehiclesInGivenBound(
            91.0, HAMBURG_NE_LON, Constants.HAMBURG_SW_LAT,
            HAMBURG_SW_LON,
        )

        val response = fakeVehicleViewModel.observeVehicleListChange().getOrAwaitValue()

        assertThat(
            response.getContentIfNotAlreadyHandled()?.message
        ).isEqualTo(
            INVALID_LATITUDE
        )
    }

    @Test
    fun listVehiclesWithInValidNELon_returnsError() {
        fakeVehicleViewModel.listVehiclesInGivenBound(
            HAMBURG_NE_LAT, 181.0, HAMBURG_SW_LAT,
            HAMBURG_SW_LON,
        )

        val response = fakeVehicleViewModel.observeVehicleListChange().getOrAwaitValue()

        assertThat(
            response.getContentIfNotAlreadyHandled()?.message
        ).isEqualTo(
            INVALID_LONGITUDE
        )
    }

    @Test
    fun listVehiclesWithInValidSWLat_returnsError() {
        fakeVehicleViewModel.listVehiclesInGivenBound(
            HAMBURG_NE_LAT, HAMBURG_NE_LON, -91.0,
            HAMBURG_SW_LON,
        )

        val response = fakeVehicleViewModel.observeVehicleListChange().getOrAwaitValue()

        assertThat(
            response.getContentIfNotAlreadyHandled()?.message
        ).isEqualTo(
            INVALID_LATITUDE
        )
    }

    @Test
    fun listVehiclesWithInValidSWLon_returnsError() {
        fakeVehicleViewModel.listVehiclesInGivenBound(
            HAMBURG_NE_LAT, HAMBURG_NE_LON, HAMBURG_SW_LAT,
            -181.0,
        )

        val response = fakeVehicleViewModel.observeVehicleListChange().getOrAwaitValue()

        assertThat(
            response.getContentIfNotAlreadyHandled()?.message
        ).isEqualTo(
            INVALID_LONGITUDE
        )
    }

    @Test
    fun listVehiclesWithValidLatLon_returnsLoadingFirst() {
        FakeVehicleRepositoryImpl.setShouldReturnNetworkError(false)
        fakeVehicleViewModel.listVehiclesInGivenBound(
            HAMBURG_NE_LAT, HAMBURG_NE_LON, HAMBURG_SW_LAT,
            HAMBURG_SW_LON,
        )

        var loading: Boolean = false
        when (val response = fakeVehicleViewModel.observeVehicleListChange().getOrAwaitValue()
            .getContentIfNotAlreadyHandled()) {

            is Response.Loading -> {
                loading = true
            }
        }

        assertThat(loading).isTrue()
    }

    /**
     * For this test case to pass open [FakeVehicleRepositoryImpl] and set shouldReturnNetworkError
     * to true
     */
    @Test
    fun listVehiclesWithValidLatLon_returnsNetworkError() {
        FakeVehicleRepositoryImpl.setShouldReturnNetworkError(true)
        fakeVehicleViewModel.listVehiclesInGivenBound(
            HAMBURG_NE_LAT, HAMBURG_NE_LON,
            HAMBURG_SW_LAT, HAMBURG_SW_LON,
        )

        var message: String? = null

        when (val response =
            fakeVehicleViewModel.observeVehicleListChange().getOrAwaitValue(responseExpected = 2)
                .getContentIfNotAlreadyHandled()) {
            is Response.Error -> {
                message = response.message.toString()
            }
        }

        assertThat(message).isEqualTo(NETWORK_ERROR)
    }

    @Test
    fun listVehiclesWithValidLatLon_returnsList() {
        FakeVehicleRepositoryImpl.setShouldReturnNetworkError(false)
        fakeVehicleViewModel.listVehiclesInGivenBound(
            HAMBURG_NE_LAT, HAMBURG_NE_LON, Constants.HAMBURG_SW_LAT,
            HAMBURG_SW_LON,
        )

        var dataAvailable: Boolean = false
        when (val response =
            fakeVehicleViewModel.observeVehicleListChange().getOrAwaitValue(responseExpected = 2)
                .getContentIfNotAlreadyHandled()) {
            is Response.Success -> {
                dataAvailable = (response.data != null)
            }
        }

        assertThat(dataAvailable).isTrue()
    }
}