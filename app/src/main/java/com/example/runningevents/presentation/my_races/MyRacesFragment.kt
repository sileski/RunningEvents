package com.example.runningevents.presentation.my_races

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runningevents.R
import com.example.runningevents.databinding.FragmentMyRacesBinding
import com.example.runningevents.domain.models.Race
import com.example.runningevents.presentation.adapters.MyRacesRecyclerViewAdapter
import com.example.runningevents.presentation.common.ErrorSnackbar.showErrorSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRacesFragment : Fragment() {

    private val viewModel: MyRacesViewModel by viewModels()
    private lateinit var binding: FragmentMyRacesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: MyRacesRecyclerViewAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRacesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        initObservers()
        setRecyclerViewScrolling()

        binding.apply {
            if (viewModel.isUserLogged()) {
                swipeRefresh.setOnRefreshListener {
                    viewModel.getMyRaces(true)
                }
                notLoggedMessage.visibility = View.GONE
                addNewRaceFloatingButton.visibility = View.VISIBLE
                addNewRaceFloatingButton.setOnClickListener {
                    findNavController().navigate(MyRacesFragmentDirections.actionMyRacesFragmentToNewRaceFragment())
                }
            } else {
                notLoggedMessage.visibility = View.VISIBLE
                addNewRaceFloatingButton.visibility = View.GONE
            }
        }
    }

    private fun initObservers() {
        viewModel.races.observe(viewLifecycleOwner, Observer { races ->
            races?.let {
                setDataInRecyclerView(it)
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.swipeRefresh.isRefreshing = it
        })
        viewModel.eventShowError.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { error ->
                showErrorSnackbar(view = view, errorMessage = error)
            }
        })
    }

    private fun setUpRecyclerView() {
        recyclerView = binding.racesRecyclerView
        linearLayoutManager = LinearLayoutManager(view?.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerViewAdapter = MyRacesRecyclerViewAdapter { race ->
            context?.let {
                showDeleteRaceConfirmationDialog(race = race, context = it)
            }
        }
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun setDataInRecyclerView(races: List<Race>) {
        recyclerViewAdapter.setData(races)
        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun setRecyclerViewScrolling() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val firstItem = linearLayoutManager.findFirstVisibleItemPosition()
                    if (visibleItemCount + firstItem >= totalItemCount) {
                        if (!binding.swipeRefresh.isRefreshing) {
                            viewModel.getMyRaces(false)
                        }
                    }
                }
            }
        })
    }

    private fun showDeleteRaceConfirmationDialog(race: Race, context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.delete_race))
            .setMessage(getString(R.string.you_want_to_delete_race))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteRace(race = race)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .show()
    }
}