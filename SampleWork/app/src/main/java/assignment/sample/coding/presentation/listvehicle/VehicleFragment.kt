package assignment.sample.coding.presentation.listvehicle

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import assignment.sample.coding.R
import assignment.sample.coding.data.util.Response
import assignment.sample.coding.presentation.listvehicle.adapters.VehicleAdapter
import assignment.sample.coding.util.Constants.EXTRAS_SELECTED_VEHICLE
import assignment.sample.coding.util.Constants.HAMBURG_NE_LAT
import assignment.sample.coding.util.Constants.HAMBURG_NE_LON
import assignment.sample.coding.util.Constants.HAMBURG_SW_LAT
import assignment.sample.coding.util.Constants.HAMBURG_SW_LON
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_vehicle.*
import timber.log.Timber


@AndroidEntryPoint
class VehicleFragment : Fragment(R.layout.fragment_vehicle) {

    private val viewModel: VehicleViewModel by viewModels()
    lateinit var vehicleAdapter: VehicleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        observeVehicleListInHamburgResponse()
        getVehicleListInHamburg()
    }

    private fun setupRecyclerView() {
        vehicleAdapter = VehicleAdapter()
        rvVehicles.apply {
            adapter = vehicleAdapter
            layoutManager = object : LinearLayoutManager(requireActivity()) {
                override fun onLayoutCompleted(state: RecyclerView.State?) {
                    super.onLayoutCompleted(state)
                    Log.e("Akash", "layoutcompoler1")
                    callbackIdlGlobal?.let {
                        Log.e("Akash", "layoutcompoler2")
                        if (vehicleAdapter.itemCount > 0) {
                            callbackIdlGlobal?.recyclerViewHaveData()
                            callbackIdlGlobal = null
                        }
                    }
                }
            }
        }
        setVehicleClickListener()
//        setRecycleViewOnLoadListener()
    }

    private fun setVehicleClickListener() {
        vehicleAdapter.setOnItemClickListener {
            Bundle().apply {
                putSerializable(EXTRAS_SELECTED_VEHICLE, it)
            }
            findNavController().navigate(
                VehicleFragmentDirections.actionListTaxiFragmentToMapFragment(it)
            )
        }
    }

    private fun setRecycleViewOnLoadListener() {
//        vehicleAdapter.setOnItemLoadListener {
            callbackIdlGlobal?.let {
                if (vehicleAdapter.itemCount > 0) {
                    callbackIdlGlobal?.recyclerViewHaveData()
                    callbackIdlGlobal = null
                }
            }
//        }
    }

    private fun getVehicleListInHamburg() {
        viewModel.listVehiclesInGivenBound(
            HAMBURG_NE_LAT,
            HAMBURG_NE_LON,
            HAMBURG_SW_LAT,
            HAMBURG_SW_LON,
        )
    }

    private fun observeVehicleListInHamburgResponse() {
        viewModel.observeVehicleListChange().observe(viewLifecycleOwner, Observer { response ->
            when (val responseCaptured = response.getContentIfNotAlreadyHandled()) {
                is Response.Success -> {
                    hideProgressBar()
                    responseCaptured.data?.let { vehicleResponse ->
                        Timber.d("Vehicle data loaded successfully ${vehicleResponse.poiList}")
                        vehicleAdapter.asyncDifference.submitList(vehicleResponse.poiList)
                    }
                }
                is Response.Error -> {
                    hideProgressBar()
                    responseCaptured.message?.let { message ->
                        Timber.e("An error occurred $message")
                        showError(message)
                    }
                }
                is Response.Loading -> {
                    Timber.d("Vehicle data is loading")
                    showProgressBar()
                }
            }
        })
    }

    private fun showError(message: String) {
        tvErrorMessage.visibility = View.VISIBLE
        tvErrorMessage.text = message
    }

    private fun showProgressBar() {
        tvErrorMessage.visibility = View.INVISIBLE
        pbLoadingState.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        pbLoadingState.visibility = View.INVISIBLE
    }

    private var callbackIdlGlobal: RecyclerViewHaveDataListener? = null

    @VisibleForTesting
    fun registerOnCallBackIdl(callbackIdl: RecyclerViewHaveDataListener) {
        callbackIdlGlobal = callbackIdl
        if (vehicleAdapter.itemCount > 0) {
            callbackIdlGlobal?.recyclerViewHaveData()
            callbackIdlGlobal = null
        }
    }


}

