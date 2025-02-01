package com.example.appointment.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appointment.R
import com.example.appointment.databinding.FragmentVisaBinding
import com.example.appointment.ui.adapders.VisaAdapter
import com.example.appointment.ui.viewmodels.VisaViewModel
import com.example.appointment.util.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VisaFragment : Fragment() {

    private var _binding: FragmentVisaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VisaViewModel by viewModels()
    private var visaAdapter: VisaAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        visaAdapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSpinner()
        setupSwipeRefresh()
        setupResetButton()
        observeData()

    }
    private fun setupRecyclerView() {
        binding.visaRv.apply {
            setHasFixedSize(true)
            visaAdapter = VisaAdapter()
            adapter = visaAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupSpinner(){
        val countryList = listOf("France","Slovenia", "Finland", "Croatia",  "Germany", "Estonia", "Netherlands", "Czechia", "Brazil", "Austria", "Switzerland", "Norway", "Luxembourg",
                                 "Hungary", "Italy", "Lithuania", "Sweden", "Denmark", "Portugal", "Ireland", "Latvia", "Lithuania TRP and National Visa", "New Zealand", "Greece")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countryList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerCountry.adapter = adapter

        // Kullanıcı bir ülke seçtiğinde
        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCountry = countryList[position]
                viewModel.visaRequest(selectedCountry) // Seçilen ülkeyi API'ye gönder
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.isRefreshing = true
            val selectedCountry = binding.spinnerCountry.selectedItem as String
            viewModel.visaRequest(selectedCountry)
        }
    }

    private fun setupResetButton() {
        binding.btnReset.setOnClickListener {
            // Spinner'ı ilk pozisyona getir
            binding.spinnerCountry.setSelection(0)
            // İlk ülkeyi seç ve verileri yenile
            val firstCountry = binding.spinnerCountry.selectedItem as String
            viewModel.visaRequest(firstCountry)
        }
    }

    private fun observeData() {
        viewModel.visaList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Log.d("VisaFragment", "Success: Received ${resource.data?.size} items")
                    binding.swipeRefresh.isRefreshing = false
                    hideProgressBar()

                    resource.data?.let { postList ->
                        if (postList.isEmpty()) {
                            binding.tvEmptyState.visibility = View.VISIBLE
                            visaAdapter?.differ?.submitList(emptyList())
                        } else {
                            binding.tvEmptyState.visibility = View.GONE
                            visaAdapter?.differ?.submitList(postList)
                            Log.d("VisaFragment", "Submitted ${postList.size} items to adapter")
                        }
                    }
                }
                is Resource.Loading -> {
                    Log.d("VisaFragment", "Loading...")
                    if (!viewModel.isRefreshing) {
                        showProgressBar()
                    }
                    binding.tvEmptyState.visibility = View.GONE
                    visaAdapter?.differ?.submitList(emptyList())
                }
                is Resource.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    hideProgressBar()
                    binding.tvEmptyState.visibility = View.VISIBLE
                    visaAdapter?.differ?.submitList(emptyList())
                    resource.message?.let { error ->
                        Log.e("VisaFragment", "Error: $error")
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }



}