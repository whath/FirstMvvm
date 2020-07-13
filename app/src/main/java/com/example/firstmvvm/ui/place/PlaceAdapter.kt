package com.example.firstmvvm.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.firstmvvm.R
import com.example.firstmvvm.logic.model.Place
import com.example.firstmvvm.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.now.*

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)

        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]

            val activity = fragment.activity
            if (activity is WeatherActivity){
                activity.apply {
                    drawerLayout.closeDrawers()
                    viewModel.locationLng = place.location.lng
                    viewModel.locationLat = place.location.lat
                    viewModel.placeName = place.name
                    refreshWeather()
                }
            }else{
                WeatherActivity.start(
                    parent.context,
                    place.location.lng,
                    place.location.lat,
                    place.name
                )
                fragment.viewModel.savePlace(place)
                activity?.finish()
            }
            fragment.viewModel.savePlace(place)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address

    }
}