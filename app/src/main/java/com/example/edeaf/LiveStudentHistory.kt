package com.example.edeaf

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.edeaf.databinding.FragmentLiveStudentHistoryBinding
import com.example.edeaf.databinding.FragmentLiveTranslationStudentBinding

class LiveStudentHistory : Fragment() {

    private var _binding: FragmentLiveStudentHistoryBinding? = null
    private val binding get() = _binding!!

    private val args: LiveStudentHistoryArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveStudentHistoryBinding.inflate(inflater,container,false)

        binding.apply {
            titleDetailHistory.text = args.title
            historyTextDetail.text = args.historyText

            btnBack.setOnClickListener {
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

}