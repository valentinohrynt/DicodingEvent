package com.inoo.dicodingevent.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inoo.dicodingevent.databinding.FragmentFavoriteBinding
import com.inoo.dicodingevent.ui.viewmodel.MainViewModel
import com.inoo.dicodingevent.ui.adapter.ListItemAdapter
import com.inoo.dicodingevent.ui.setting.SettingPreferences
import com.inoo.dicodingevent.ui.viewmodel.ViewModelFactory
import com.inoo.dicodingevent.ui.setting.dataStore

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listAdapter: ListItemAdapter
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext(), SettingPreferences.getInstance(requireContext().applicationContext.dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = binding.progressBar
        listRecyclerView = binding.favoriteListRecyclerView
        setupRecyclerView()
        observeFavoritedEvents()
    }

    private fun setupRecyclerView() {
        listAdapter = ListItemAdapter(viewModel)
        listRecyclerView.adapter = listAdapter
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeFavoritedEvents() {
        viewModel.fetchFavoritedEvents().observe(viewLifecycleOwner) { result ->
            listAdapter.setEvents(result)
        }
    }

    override fun onResume() {
        super.onResume()
        observeFavoritedEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}