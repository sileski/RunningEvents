package com.example.runningevents.presentation.races

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runningevents.R
import com.example.runningevents.databinding.FragmentRacesBinding
import com.example.runningevents.domain.models.Race
import com.example.runningevents.presentation.adapters.RacesRecyclerViewAdapter
import com.example.runningevents.presentation.common.ErrorSnackbar.showErrorSnackbar
import com.example.runningevents.utils.Constants.KEY_ON_BACK_FILTER_RACES
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RacesFragment : Fragment() {

    private val viewModel: RacesViewModel by viewModels()
    private lateinit var binding: FragmentRacesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RacesRecyclerViewAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var locationPermissionsResultLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRacesBinding.inflate(inflater, container, false)
        locationPermissionLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenu()
        setUpRecyclerView()
        setRecyclerViewScrolling()
        setBackStackEntryObserver()
        initObservers()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getRaces(true)
        }
    }

    private fun initObservers() {
        viewModel.races.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                setDataInRecyclerView(it)
            } else {
                recyclerViewAdapter.clearData()
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.loadingProgressIndicator.visibility = View.VISIBLE
                binding.swipeRefresh.isRefreshing = true
            } else {
                binding.loadingProgressIndicator.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        })
        viewModel.requestPermission.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                requestLocationPermissions()
            }
        })
        viewModel.eventShowError.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { error ->
                binding.loadingProgressIndicator.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                showErrorSnackbar(view = view, errorMessage = error)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_races, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filters -> {
                findNavController().navigate(RacesFragmentDirections.actionRacesFragmentToRaceFiltersFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setToolbarMenu() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.appToolbar.toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        setHasOptionsMenu(true)
    }

    private fun setBackStackEntryObserver() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Boolean>(KEY_ON_BACK_FILTER_RACES)
            ?.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    viewModel.getRaces(true)
                    savedStateHandle.remove<Boolean>(KEY_ON_BACK_FILTER_RACES)
                }
            })
    }

    private fun setUpRecyclerView() {
        recyclerView = binding.racesRecyclerView
        linearLayoutManager = LinearLayoutManager(view?.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerViewAdapter = RacesRecyclerViewAdapter { race ->
            onRaceCardClick(race)
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
                            viewModel.getRaces(false)
                        }
                    }
                }
            }
        })
    }

    private fun onRaceCardClick(race: Race) {
        findNavController().navigate(
            RacesFragmentDirections.actionRacesFragmentToRaceDetailsFragment(
                race
            )
        )
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireView().context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionsResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun locationPermissionLauncher() {
        locationPermissionsResultLauncher =
            registerForActivityResult(RequestPermission()) { isGranted ->
                if (isGranted) {
                    viewModel.getRaces(isRefreshing = true)
                } else {
                    context?.let {
                        showDeclinedPermissionAlertDialog(it)
                    }
                }
            }
    }

    private fun showDeclinedPermissionAlertDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setMessage("You need to grant location permission to be able to get races by location.")
            .setPositiveButton("OK") { _, _ ->
            }.show()
    }
}