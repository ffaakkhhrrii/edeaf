package com.example.edeaf.ui.dictionary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.edeaf.data.mechanism.handleCollect
import com.example.edeaf.databinding.FragmentDictionaryPageBinding
import com.example.edeaf.ui.adapter.DictionaryAdapter
import com.example.edeaf.ui.util.PresentationHelper.showMaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryPageBinding? = null
    private val binding get() = _binding!!

    private val args: DictionaryFragmentArgs by navArgs()
    
    private val viewModel: DictionaryFragmentViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryPageBinding.inflate(inflater,container,false)

        viewModel.fetchDictionary(args.jenis)
        Log.e("DictionaryRepository", args.jenis)

        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch { 
                viewModel.listDictionary.handleCollect(
                    onSuccess = {result->
                        dictionaryPb.visibility = View.GONE
                        val rvAdapter = DictionaryAdapter(result.data!!)
                        rvKamus.adapter = rvAdapter
                        rvKamus.apply {
                            setHasFixedSize(true)
                            layoutManager = GridLayoutManager(requireContext(), 2)
                        }
                        jenisText.text = result.data.first().jenis
                    },
                    onError = {result->
                        dictionaryPb.visibility = View.GONE
                        requireContext().showMaterialDialog(
                            show = true,
                            message = result.message!!
                        )
                    },
                    onLoading = {
                        dictionaryPb.visibility = View.VISIBLE
                    }
                )
            }
            back.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        return binding.root
    }

}