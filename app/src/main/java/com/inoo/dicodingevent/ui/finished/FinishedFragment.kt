package com.inoo.dicodingevent.ui.finished

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
import com.inoo.dicodingevent.databinding.FragmentFinishedBinding
import com.inoo.dicodingevent.ui.MainViewModel
import com.inoo.dicodingevent.ui.adapter.ListItemAdapter
import androidx.appcompat.widget.SearchView
import com.inoo.dicodingevent.util.networkUtil


class FinishedFragment : Fragment() {

    private var binding: FragmentFinishedBinding? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var listrecyclerView: RecyclerView
    private lateinit var listAdapter: ListItemAdapter
    private lateinit var searchViewFinished: SearchView
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewFinished = binding!!.searchViewFinished
        searchViewFinished.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.searchInactiveEvents(query)
                } else {
                    viewModel.fetchInactiveEvents()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.searchInactiveEvents(newText)
                } else {
                    viewModel.fetchInactiveEvents()
                }
                return true
            }
        })

        progressBar = binding!!.progressBar
        listrecyclerView = binding!!.finishedListRecyclerView

        listAdapter = ListItemAdapter(
            onClickedItem = { id ->
                navigateToDetail(id)
            }
                ,viewType = 1)
        listrecyclerView.adapter = listAdapter
        listrecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.inactiveEvents.observe(viewLifecycleOwner) { events ->
            listAdapter.setEvents1(events)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { events ->
            if (events.isEmpty() && viewModel.isSearching) {
                Toast.makeText(requireContext(), "Tidak ada event dengan judul tersebut pada daftar event yang sudah selesai", Toast.LENGTH_SHORT).show()
            }
            listAdapter.setEvents1(events)
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
        viewModel.fetchInactiveEvents()

    }

    override fun onResume() {
        super.onResume()
        networkUtil.checkInternet(requireContext())
        searchViewFinished.setQuery("", false)
        searchViewFinished.clearFocus()
        searchViewFinished.isIconified = true
        viewModel.fetchActiveEvents()
    }
    
    private fun navigateToDetail(eventId: Int?) {
        eventId?.let {
            val action = FinishedFragmentDirections.actionNavigationFinishedToDetailFragment(it)
            findNavController().navigate(action)
        }
    }

}

