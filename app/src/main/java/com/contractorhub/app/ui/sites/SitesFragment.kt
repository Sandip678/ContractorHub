package com.contractorhub.app.ui.sites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.databinding.FragmentSitesBinding
import kotlinx.coroutines.launch

/**
 * PART 15 — Site List. Phase 3 पासून हा खरा Room data दाखवतो (यापुढे dummy नाही).
 */
class SitesFragment : Fragment() {

    private var _binding: FragmentSitesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SitesViewModel by viewModels()
    private lateinit var adapter: SiteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSitesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SiteListAdapter { site ->
            findNavController().navigate(
                R.id.addEditSiteFragment,
                bundleOf("siteId" to site.siteId)
            )
        }
        binding.recyclerSites.adapter = adapter

        binding.fabAddSite.setOnClickListener { openAddSite() }
        binding.buttonEmptyAddSite.setOnClickListener { openAddSite() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sites.collect { list ->
                    adapter.submitList(list)
                    binding.layoutEmptyState.isVisible = list.isEmpty()
                    binding.recyclerSites.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    private fun openAddSite() {
        findNavController().navigate(
            R.id.addEditSiteFragment,
            bundleOf("siteId" to null as String?)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerSites.adapter = null
        _binding = null
    }
}
