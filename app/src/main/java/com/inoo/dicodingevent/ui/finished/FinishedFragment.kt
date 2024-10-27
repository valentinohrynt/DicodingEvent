package com.inoo.dicodingevent.ui.finished

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inoo.dicodingevent.databinding.FragmentFinishedBinding
import com.inoo.dicodingevent.ui.viewmodel.MainViewModel
import com.inoo.dicodingevent.ui.adapter.ListItemAdapter
import androidx.appcompat.widget.SearchView
import com.inoo.dicodingevent.data.Result
import com.inoo.dicodingevent.data.local.entity.EventEntity
import com.inoo.dicodingevent.ui.setting.SettingPreferences
import com.inoo.dicodingevent.ui.viewmodel.ViewModelFactory
import com.inoo.dicodingevent.ui.setting.dataStore


class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listAdapter: ListItemAdapter
    private lateinit var searchViewFinished: SearchView
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext(), SettingPreferences.getInstance(requireContext().applicationContext.dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewFinished = binding.searchViewFinished
        progressBar = binding.progressBar
        listRecyclerView = binding.finishedListRecyclerView

        searchViewFinished.setQuery(null, false)
        searchViewFinished.clearFocus()
        searchViewFinished.isIconified = true

        setupSearchView()
        setupRecyclerView()
        observeInactiveEvents()
    }

    private fun setupSearchView() {
        searchViewFinished.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                doSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isBlank()) {
                    observeInactiveEvents()
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        listAdapter = ListItemAdapter(viewModel)
        listRecyclerView.adapter = listAdapter
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeInactiveEvents() {
        Log.d("FinishedFragment", "observeInactiveEvents called")
        viewModel.fetchInactiveEvents().observe(viewLifecycleOwner) { result ->
            resultHandler(result)
        }
    }

    private fun doSearch(query: String) {
        Log.d("FinishedFragment", "doSearch called")
        val active = 0
        viewModel.searchEvents(active, query).observe(viewLifecycleOwner) { result ->
            resultHandler(result)
        }
    }

    private fun resultHandler(result: Result<List<EventEntity>>) {
        when (result) {
            is Result.Loading -> {
                progressBar.visibility = View.VISIBLE
            }
            is Result.Success -> {
                progressBar.visibility = View.GONE
                if (result.data.isEmpty()) {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                }
                listAdapter.setEvents(result.data)
            }
            is Result.Error -> {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        searchViewFinished.setQuery(null, false)
//        searchViewFinished.clearFocus()
//        searchViewFinished.isIconified = true
//        observeInactiveEvents()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


