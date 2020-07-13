package com.example.firstmvvm.ui.place

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstmvvm.MainActivity
import com.example.firstmvvm.R
import com.example.firstmvvm.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment : Fragment() {
    val viewModel by lazy {
        ViewModelProviders.of(this).get(PlaceViewModel::class.java)
    }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val savePlace = viewModel.getSavePlace()
            WeatherActivity.start(
                context!!,
                savePlace.location.lng,
                savePlace.location.lat,
                savePlace.name
            )
            activity?.finish()
            return
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlace(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.placeLiveData.observe(this, Observer { result ->
            val place = result.getOrNull()
            if (place != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(place)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查到任何地点", Toast.LENGTH_LONG).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

    }
}