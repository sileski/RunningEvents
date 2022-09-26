package com.example.runningevents.presentation.race_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.runningevents.R
import com.example.runningevents.databinding.DetailsCategoryCardItemBinding
import com.example.runningevents.databinding.FragmentRaceDetailsBinding
import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.models.RaceCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class RaceDetailsFragment : Fragment() {

    private val viewModel: RaceDetailsViewModel by viewModels()
    private lateinit var binding: FragmentRaceDetailsBinding
    private lateinit var race: Race
    private var expandedDescription = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRaceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            race = RaceDetailsFragmentArgs.fromBundle(it).race
        }

        setToolbar()

        binding.apply {
            raceName.text = race.raceName
            raceImage.load(race.imageUrl)

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
            val raceCategories = categoryDistanceWithNumbers + categoryDistanceWithWords
            raceCategories.forEach { raceCategory ->
                addRaceCategory(
                    raceCategory = raceCategory,
                    containerCategories = containerCategories
                )
            }

            val date = race.date.toDate()
            raceDate.text = viewModel.getDateFormat(date)
            raceTime.text = viewModel.getTimeFormat(date)

            appbar.addOnOffsetChangedListener { _, verticalOffset ->
                if (verticalOffset == 0) {
                    (activity as AppCompatActivity).supportActionBar?.title = ""
                } else {
                    (activity as AppCompatActivity).supportActionBar?.title = race.raceName
                }
            }

            raceDescription.visibility = View.VISIBLE
            raceDescription.text = race.description.replace("\\n", "\n")
            showMoreDescriptionButton.visibility = View.GONE
            raceDescription.getTextLineCount(raceDescription.text.toString()) { numberOfLines ->
                val collapsedNumberOfLines = 8
                if (numberOfLines <= collapsedNumberOfLines) {
                    showMoreDescriptionButton.visibility = View.GONE
                } else {
                    showMoreDescriptionButton.visibility = View.VISIBLE
                }
                raceDescription.visibility = View.VISIBLE
                raceDescription.maxLines = collapsedNumberOfLines
                expandingDescriptionTextView(
                    numberOfLines = numberOfLines,
                    collapsedNumberOfLines = collapsedNumberOfLines
                )
            }

            if (race.websiteUrl.isNotEmpty()) {
                raceWebsite.visibility = View.VISIBLE
                raceWebsite.setOnClickListener {
                    race.websiteUrl.let {
                        var url = it
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "http://$url"
                        }
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(browserIntent)
                    }
                }
            } else {
                raceWebsite.visibility = View.GONE
            }
        }
    }

    private fun setToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun addRaceCategory(raceCategory: RaceCategory, containerCategories: LinearLayout) {
        val bindingItem =
            DetailsCategoryCardItemBinding.inflate(layoutInflater, null, false)
        val categoryView = bindingItem.root
        val margins = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        margins.topMargin = 10
        margins.bottomMargin = 10
        margins.leftMargin = 35
        margins.rightMargin = 35
        categoryView.layoutParams = margins

        val date = raceCategory.date.toDate()
        val startTime = viewModel.getTimeFormat(date)
        val entryFee = if (raceCategory.price != 0.0) {
            val red = ContextCompat.getColor(
                bindingItem.feeCard.context,
                R.color.red
            )
            bindingItem.feeCard.background.setTint(red)
            viewModel.getCurrencyFormat(
                price = raceCategory.price,
                currency = raceCategory.currency
            )
        } else {
            val green = ContextCompat.getColor(
                bindingItem.feeCard.context,
                R.color.green
            )
            bindingItem.feeCard.background.setTint(green)
            getString(R.string.free)
        }
        bindingItem.distance.text = raceCategory.distance
        bindingItem.startTime.text = startTime
        bindingItem.fee.text = entryFee
        containerCategories.addView(categoryView)
    }

    private fun expandingDescriptionTextView(numberOfLines: Int, collapsedNumberOfLines: Int) {
        binding.apply {
            showMoreDescriptionButton.setOnClickListener {
                if (numberOfLines > collapsedNumberOfLines) {
                    if (expandedDescription) {
                        expandedDescription = false
                        raceDescription.maxLines = collapsedNumberOfLines
                        showMoreDescriptionButton.text = getString(R.string.show_more)
                    } else {
                        expandedDescription = true
                        raceDescription.maxLines = 500
                        showMoreDescriptionButton.text = getString(R.string.show_less)
                    }
                }
            }
        }
    }

    private fun TextView.getTextLineCount(text: String, lineCount: (Int) -> (Unit)) {
        val params: PrecomputedTextCompat.Params = TextViewCompat.getTextMetricsParams(this)
        val ref: WeakReference<TextView> = WeakReference(this)

        lifecycleScope.launch(Dispatchers.Default) {
            val txt = PrecomputedTextCompat.create(text, params)
            lifecycleScope.launch(Dispatchers.Main) {
                ref.get()?.let { textView ->
                    TextViewCompat.setPrecomputedText(textView, txt)
                    lineCount.invoke(textView.lineCount)
                }
            }
        }
    }
}