package assignment.sample.coding.presentation.listvehicle.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import assignment.sample.coding.R
import assignment.sample.coding.domain.model.Poi
import assignment.sample.coding.util.fleetIconResource
import assignment.sample.coding.util.fleetString
import kotlinx.android.synthetic.main.item_vehicle.view.*

/**
 * VehicleAdapter is bridge between Vehicle list data and UI i.e RecyclerView
 *
 * It's single item is [Poi] class which then be presented in UI.
 */
class VehicleAdapter : RecyclerView.Adapter<VehicleAdapter.FleetViewHolder>() {

    inner class FleetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Poi>() {
        override fun areItemsTheSame(oldItem: Poi, newItem: Poi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Poi, newItem: Poi): Boolean {
            return oldItem == newItem
        }
    }

    val asyncDifference = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FleetViewHolder {
        return FleetViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_vehicle,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FleetViewHolder, position: Int) {
        val vehicle = asyncDifference.currentList[position]
        holder.itemView.apply {
            ivVehicleImage.setImageResource(vehicle.fleetIconResource())
            tvVehicleCategory.text = vehicle.fleetString()
            tvLocation.text = vehicle.locationName?.let {
                "Now at: $it"
            }?:"${vehicle.coordinate.latitude},${vehicle.coordinate.longitude}"

//             I thought to do it in similar fashion as Glide and other Image libraries does. But
//            for now have moved this code in view model to get Location Address with API response.
//            GlobalScope.launch(Dispatchers.IO) {
//                val location = vehicle.getAddress(tvLocation.context)
//                withContext(Dispatchers.Main) {
//                    tvLocation.text = location
//                }
//            }

            setOnClickListener {
                onItemClickListener?.let {
                    it(vehicle)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return asyncDifference.currentList.size
    }

    private var onItemClickListener: ((Poi) -> Unit)? = null

    /**
     * Set an item click listener callback which would then be called when a click initiates on UI
     *
     * @param listener takes [Poi] input to perform operation and returns nothing. Implementation
     * will be at caller side.
     */
    fun setOnItemClickListener(listener: (Poi) -> Unit) {
        onItemClickListener = listener
    }
}