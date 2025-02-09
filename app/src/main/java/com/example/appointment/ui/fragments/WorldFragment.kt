package com.example.appointment.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.databinding.FragmentWorldBinding
import com.example.appointment.ui.adapders.VisaAdapter
import com.example.appointment.ui.viewmodels.WorldViewModel
import com.example.appointment.util.Resource
import android.widget.ArrayAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorldFragment : Fragment() {

    private lateinit var binding: FragmentWorldBinding
    private val viewModel: WorldViewModel by viewModels()
    private var visaAdapter: VisaAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentWorldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSpinners()
        setupSwipeRefresh()
        setupSearchButton()
        observeData()
    }

    private fun setupSpinners() {
        // Ülke listelerini oluştur

        val originCountries = listOf("All", "Turkiye","France","India","Slovenia", "Finland", "Croatia",  "Germany", "Estonia", "Netherlands", "Czechia", "Brazil", "Austria", "Switzerland", "Norway", "Luxembourg",
            "Hungary", "Italy", "Lithuania", "Sweden", "Denmark", "Portugal", "Ireland", "Latvia", "Lithuania TRP and National Visa", "New Zealand", "Greece")
        val destinationCountries = listOf("All", "Turkiye","France","India","Slovenia", "Finland", "Croatia",  "Germany", "Estonia", "Netherlands", "Czechia", "Brazil", "Austria", "Switzerland", "Norway", "Luxembourg",
            "Hungary", "Italy", "Lithuania", "Sweden", "Denmark", "Portugal", "Ireland", "Latvia", "Lithuania TRP and National Visa", "New Zealand", "Greece")

        // Spinner adaptörlerini oluştur
        val destinationAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            destinationCountries
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val originAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            originCountries
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Spinner'ları ayarla
        binding.spinnerDestination.adapter = destinationAdapter
        binding.spinnerOrigin.adapter = originAdapter
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val destinationCountry = binding.spinnerDestination.selectedItem as String
            val originCountry = binding.spinnerOrigin.selectedItem as String
            
            // Listeyi temizle ve yükleme göster
            visaAdapter?.differ?.submitList(emptyList())
            showProgressBar()
            
            // Filtreleme işlemini başlat
            viewModel.filterVisas(destinationCountry, originCountry)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            visaAdapter = VisaAdapter()
            adapter = visaAdapter
            layoutManager = LinearLayoutManager(requireContext())
            
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && 
                        !viewModel.isLastPage && 
                        !viewModel.isRefreshing) {
                        viewModel.loadMoreWorld()
                    }
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            visaAdapter?.differ?.submitList(emptyList())
            viewModel.refreshWorld()
        }
    }

    private fun observeData() {
        viewModel.response10World.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    hideProgressBar()
                    hidePaginationProgressBar()
                    
                    resource.data?.let { visaList ->
                        if (visaList.isEmpty()) {
                            showEmptyState()
                        } else {
                            hideEmptyState()
                            if (viewModel.currentPage == 1) {
                                // İlk sayfa için listeyi tamamen değiştir
                                visaAdapter?.differ?.submitList(visaList)
                            } else {
                                // Sonraki sayfalar için mevcut listeye ekle
                                visaAdapter?.differ?.submitList(
                                    visaAdapter?.differ?.currentList?.plus(visaList)
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    hideProgressBar()
                    hidePaginationProgressBar()
                    showEmptyState()
                }
                is Resource.Loading -> {
                    if (!viewModel.isPagination) {
                        showProgressBar()
                    } else {
                        showPaginationProgressBar()
                    }
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.textView.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun hideEmptyState() {
        binding.textView.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        binding.progressBar.visibility = View.GONE
    }
    private fun showPaginationProgressBar(){
        binding.pbPagination.visibility = View.VISIBLE
    }
    private fun hidePaginationProgressBar(){
        binding.pbPagination.visibility = View.GONE

    }
}