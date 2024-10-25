package com.inoo.dicodingevent.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inoo.dicodingevent.databinding.FragmentHomeBinding
import com.inoo.dicodingevent.ui.viewmodel.MainViewModel
import com.inoo.dicodingevent.ui.adapter.GridItemAdapter
import com.inoo.dicodingevent.ui.adapter.ListItemAdapter
import com.inoo.dicodingevent.ui.setting.SettingPreferences
import com.inoo.dicodingevent.ui.viewmodel.ViewModelFactory
import com.inoo.dicodingevent.ui.setting.dataStore
import com.inoo.dicodingevent.data.Result

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var gridRecyclerView: RecyclerView
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var gridAdapter: GridItemAdapter
    private lateinit var listAdapter: ListItemAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var pref: SettingPreferences
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext(), pref)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridRecyclerView = binding.gridRecyclerView
        listRecyclerView = binding.listRecyclerView
        progressBar = binding.progressBar
        pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)

        gridAdapter = GridItemAdapter ({ eventEntity ->
            navigateToDetail(eventEntity.id.toInt())
        }, viewModel)

        listAdapter = ListItemAdapter({ eventEntity ->
            navigateToDetail(eventEntity.id.toInt())
        }, viewModel)

        gridRecyclerView.adapter = gridAdapter
        listRecyclerView.adapter = listAdapter

        gridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        observeGridRecycler()
        observeListRecycler()
    }

    private fun observeGridRecycler(){
        viewModel.fetchActiveEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        gridAdapter.setEvents(result.data.take(5))
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Error: " + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun observeListRecycler() {
        viewModel.fetchInactiveEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        listAdapter.setEvents(result.data.take(5))
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun navigateToDetail(eventId: Int?) {
        eventId?.let {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationDetail(it)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
