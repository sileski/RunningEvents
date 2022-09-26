package com.example.runningevents.presentation.adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.runningevents.R
import com.example.runningevents.databinding.RaceCardItemBinding
import com.example.runningevents.domain.models.Race
import java.text.SimpleDateFormat
import java.util.*

class RacesRecyclerViewAdapter(
    private val cardClickListener: (Race) -> Unit
) : RecyclerView.Adapter<RaceViewHolder>() {

    private val racesList: MutableList<Race> = mutableListOf()

    fun setData(races: List<Race>) {
        racesList.clear()
        racesList.addAll(races)
    }

    fun clearData() {
        racesList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val binding =
            RaceCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        holder.bind(racesList[position], cardClickListener)
    }

    override fun getItemCount(): Int {
        return racesList.size
    }
}

class RaceViewHolder(binding: RaceCardItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private val raceCard = binding.raceCard
    private val raceName = binding.raceName
    private val raceImage = binding.raceImage
    private val raceDate = binding.raceDateText
    private val raceTime = binding.raceTimeText
    private val raceCategories = binding.raceCategoriesText
    private val raceLocation = binding.raceLocationText
    private val progressLoading = binding.progressLoading

    fun bind(race: Race, cardClickListener: (Race) -> Unit) {
        raceName.text = race.raceName
        raceLocation.text = "${race.city}, ${race.country}"

        val dateTime = race.date.toDate()
        val dateFormat = SimpleDateFormat("E, d MMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm z", Locale.getDefault())
        val date = dateFormat.format(dateTime)
        val time = timeFormat.format(dateTime)

        raceDate.text = date
        raceTime.text = time

        progressLoading.visibility = View.VISIBLE
        raceImage.load(race.imageUrl) {
            listener(
                onSuccess = { _, _ ->
                    progressLoading.visibility = View.GONE
                },
                onError = { _, _ ->
                    progressLoading.visibility = View.GONE
                })
        }

        val categoryDistanceWithNumbers = race.raceCategories.filter {
            val num = it.distance.substring(0, it.distance.length - 2)
            num.isDigitsOnly()
        }.sortedBy {
            "s(\\d+)".toRegex().matchEntire(it.distance)?.groups?.get(1)?.value?.toInt()
        }
        val categoryDistanceWithWords = race.raceCategories.filter {
            val num = it.distance.substring(0, it.distance.length - 2)
            !num.isDigitsOnly()
        }.sortedBy {
            it.distance
        }
        val categories = categoryDistanceWithNumbers + categoryDistanceWithWords
        raceCategories.text = ""
        for (category in categories) {
            val spannable = SpannableString("${category.distance} ")
            spannable.setSpan(
                ForegroundColorSpan(
                    getDistanceColor(distance = category.distance)
                ), 0, category.distance.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            raceCategories.append(spannable)
        }

        raceCard.setOnClickListener {
            cardClickListener(race)
        }
    }

    private fun getDistanceColor(distance: String): Int {
        val distanceWithoutUnit = distance.substring(0, distance.length - 2)
        when (distanceWithoutUnit.isDigitsOnly()) {
            false -> {
                return when (distance) {
                    "Marathon" -> ContextCompat.getColor(
                        raceCategories.context,
                        R.color.category_red
                    )
                    "Half-Marathon" -> ContextCompat.getColor(
                        raceCategories.context,
                        R.color.category_purple
                    )
                    else -> ContextCompat.getColor(raceCategories.context, R.color.teal_700)
                }
            }
            true -> {
                val distanceNumber = distanceWithoutUnit.toInt()
                distanceNumber.let {
                    return when {
                        it <= 5 -> ContextCompat.getColor(
                            raceCategories.context,
                            R.color.category_blue
                        )
                        it <= 10 -> ContextCompat.getColor(
                            raceCategories.context,
                            R.color.category_gold
                        )
                        else -> ContextCompat.getColor(raceCategories.context, R.color.teal_700)
                    }
                }
            }
        }
    }
}