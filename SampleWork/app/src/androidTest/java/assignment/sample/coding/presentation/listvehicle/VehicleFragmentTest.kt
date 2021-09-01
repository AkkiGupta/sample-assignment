package assignment.sample.coding.presentation.listvehicle

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import assignment.sample.coding.R
import assignment.sample.coding.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class VehicleFragmentTest {

    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Click on a child view with specified id."

        override fun perform(uiController: UiController, view: View) =
            click().perform(uiController, view.findViewById<View>(viewId))
    }

    @Test
    fun listVehiclesAndClick_navigateToMapFragment() {
        val vehicleRV = RecyclerViewIdlingRes("idlingRecyclerView")
        IdlingRegistry.getInstance().register(vehicleRV)

        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<VehicleFragment> {
            Navigation.setViewNavController(requireView(), navController)
            registerOnCallBackIdl(vehicleRV)
        }

        onIdle()
        IdlingRegistry.getInstance().unregister(vehicleRV)

        onView(withId(R.id.rvVehicles))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickOnViewChild(R.id.item_rv_vehicles_child)
                )
            )
    }

    @Test
    fun clickMapNavOption_navigateToMapFragment() {

        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<VehicleFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.bottomNavigationView))
            .perform(
                NavigationViewActions.navigateTo(R.id.mapFragment)
            )

        verify(navController).navigate(
            VehicleFragmentDirections.actionListTaxiFragmentToMapFragment()
        )
    }
}