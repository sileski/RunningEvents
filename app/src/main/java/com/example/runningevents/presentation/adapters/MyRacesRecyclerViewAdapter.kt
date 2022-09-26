package com.example.runningevents.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runningevents.databinding.MyRaceItemBinding
import com.example.runningevents.domain.models.Race
import java.text.SimpleDateFormat
import java.util.*

class MyRacesRecyclerViewAdapter(
    private val deleteRaceClickListener: (Race) -> Unit
) : RecyclerView.Adapter<MyRaceViewHolder>() {

    private val myRacesList: MutableList<Race> = mutableListOf()

    fun setData(myRaces: List<Race>) {
        myRacesList.clear()
        myRacesList.addAll(myRaces)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRaceViewHolder {
        val binding = MyRaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRaceViewHolder(binding = binding)
    }

    override fun onBindViewHolder(holder: MyRaceViewHolder, position: Int) {
        holder.bind(race = myRacesList[position], deleteRaceClickListener = deleteRaceClickListener)
    }

    override fun getItemCount(): Int {
        return myRacesList.size
    }
}

class MyRaceViewHolder(binding: MyRaceItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val myRaceName = binding.myRaceName
    private val myRaceDate = binding.myRaceDate
    private val deleteRaceBtn = binding.deleteRaceBtn

    fun bind(race: Race, deleteRaceClickListener: (Race) -> Unit) {
        myRaceName.text = race.raceName

        val dateTime = race.date.toDate()
        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        val date = dateFormat.format(dateTime)
        myRaceDate.text = date

        deleteRaceBtn.setOnClickListener {
            deleteRaceClickListener(race)
        }
    }
}