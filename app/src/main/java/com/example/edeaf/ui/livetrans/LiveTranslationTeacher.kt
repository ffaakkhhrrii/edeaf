package com.example.edeaf.ui.livetrans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.edeaf.data.mechanism.handleCollect
import com.example.edeaf.databinding.FragmentLiveTranslationTeacherBinding
import com.example.edeaf.ui.livetrans.viewmodel.LiveTranslationTeacherViewModel
import com.example.edeaf.ui.util.PresentationHelper.showMaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LiveTranslationTeacher : Fragment() {
    private var _binding: FragmentLiveTranslationTeacherBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LiveTranslationTeacherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveTranslationTeacherBinding.inflate(inflater,container,false)

        binding.apply {
            btnCreateRoom.setOnClickListener{
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.makeRoom(edtTitleRoom.text.toString()).handleCollect(
                        onSuccess = { result->
                            val direct = LiveTranslationTeacherDirections.actionLiveTranslationTeacherToLiveTeacherRoom(result.data!!)
                            findNavController().navigate(direct)
                        },
                        onLoading = {
                            requireContext().showMaterialDialog(
                                show = true,
                                message = "Loading..."
                            )
                        },
                        onError = { result->
                            requireContext().showMaterialDialog(
                                show = true,
                                message = result.message!!
                            )
                        }
                    )
                }
            }

            backFragment.setOnClickListener {
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
        return binding.root
    }

}