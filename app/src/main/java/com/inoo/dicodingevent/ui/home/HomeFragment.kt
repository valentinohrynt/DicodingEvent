package com.inoo.dicodingevent.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inoo.dicodingevent.databinding.FragmentHomeBinding
import com.inoo.dicodingevent.networkUtil
import com.inoo.dicodingevent.ui.MainViewModel
import com.inoo.dicodingevent.ui.adapter.GridItemAdapter
import com.inoo.dicodingevent.ui.adapter.ListItemAdapter

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var gridRecyclerView: RecyclerView
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var gridAdapter: GridItemAdapter
    private lateinit var listAdapter: ListItemAdapter
    private lateinit var progressBar: ProgressBar
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridRecyclerView = binding!!.gridRecyclerView
        listRecyclerView = binding!!.listRecyclerView
        progressBar = binding!!.progressBar

        gridAdapter = GridItemAdapter(
            onClickedItem = {
                id -> navigateToDetail(id)
            }
        )
        listAdapter = ListItemAdapter(
            onClickedItem = {
                id -> navigateToDetail(id)
            }, viewType = 1
        )

        gridRecyclerView.adapter = gridAdapter
        listRecyclerView.adapter = listAdapter


        val gridLayoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        gridRecyclerView.layoutManager = gridLayoutManager
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            gridAdapter.setEvents(events)
        }
        viewModel.inactiveEvents.observe(viewLifecycleOwner) { events ->
            listAdapter.setEvents1(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                gridRecyclerView.visibility = View.GONE
                listRecyclerView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                gridRecyclerView.visibility = View.VISIBLE
                listRecyclerView.visibility = View.VISIBLE
            }
        }
        viewModel.fetchActiveEvents()
        viewModel.fetchInactiveEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        networkUtil.checkInternet(requireContext())
    }
    private fun navigateToDetail(eventId: Int?) {
        eventId?.let {
            val action = HomeFragmentDirections.actionNavigationHomeToDetailFragment(it)
            findNavController().navigate(action)
        }
    }
}
