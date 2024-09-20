package com.inoo.dicodingevent.ui.upcoming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inoo.dicodingevent.databinding.FragmentUpcomingBinding
import com.inoo.dicodingevent.ui.MainViewModel
import com.inoo.dicodingevent.ui.adapter.ListItemAdapter
import androidx.appcompat.widget.SearchView
import com.inoo.dicodingevent.util.NetworkUtil

class UpcomingFragment : Fragment() {

    private var binding: FragmentUpcomingBinding? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private lateinit var listrecyclerView: RecyclerView
    private lateinit var listAdapter: ListItemAdapter
    private lateinit var searchViewUpcoming: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewUpcoming = binding!!.searchViewUpcoming
        searchViewUpcoming.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.searchActiveEvents(query)
                } else {
                    viewModel.fetchActiveEvents()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.fetchActiveEvents()
                    viewModel.clearError()
                }
                return true
            }
        })

        progressBar = binding!!.progressBar
        listrecyclerView = binding!!.upcomingListRecyclerView

        listAdapter = ListItemAdapter(
            onClickedItem = { id ->
                navigateToDetail(id)
            }
                ,viewType = 2)
        listrecyclerView.adapter = listAdapter

        listrecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            listAdapter.setEvents2(events)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { events ->
            listAdapter.setEvents2(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                listrecyclerView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                listrecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.fetchActiveEvents()

    }

    override fun onResume() {
        super.onResume()
        NetworkUtil.checkInternet(requireContext())
        searchViewUpcoming.setQuery(null, false)
        searchViewUpcoming.clearFocus()
        searchViewUpcoming.isIconified = true
        viewModel.fetchActiveEvents()
    }

    private fun navigateToDetail(eventId: Int?) {
        eventId?.let {
            val action = UpcomingFragmentDirections.actionNavigationUpcomingToDetailFragment(it)
            findNavController().navigate(action)
        }
    }

}