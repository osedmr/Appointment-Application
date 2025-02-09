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
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorldFragment : Fragment() {

    private var _binding: FragmentWorldBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WorldViewModel by viewModels()
    private var visaAdapter: VisaAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        visaAdapter = null
        // RecyclerView'ı temizle
        binding.recyclerView.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSpinners()
        setupSwipeRefresh()
        setupSearchButton()
        observeData()
        observeCountries()
    }

    private fun setupSpinners() {
        // Boş adaptörleri oluştur
        val emptyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Loading...")
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerDestination.adapter = emptyAdapter
        binding.spinnerOrigin.adapter = emptyAdapter
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
            setHasFixedSize(true) // Performans iyileştirmesi
            visaAdapter = VisaAdapter()
            adapter = visaAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // İsteğe bağlı RecyclerView önbellekleme
            recycledViewPool.setMaxRecycledViews(0, 20)
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
                    
                    resource.data?.let { visaList ->
                        if (visaList.isEmpty()) {
                            showEmptyState()
                        } else {
                            hideEmptyState()
                            visaAdapter?.differ?.submitList(visaList)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    hideProgressBar()
                    showEmptyState()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun observeCountries() {
        viewModel.countries.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { countries ->
                        updateSpinners(countries.toList())
                    }
                }
                is Resource.Error -> {
                    // Hata durumunda kullanıcıya bilgi ver
                    Toast.makeText(context, "Ülke listesi yüklenemedi", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // İsteğe bağlı loading gösterilebilir
                }
            }
        }
    }

    private fun updateSpinners(countries: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            countries
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerDestination.adapter = adapter
        binding.spinnerOrigin.adapter = adapter
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