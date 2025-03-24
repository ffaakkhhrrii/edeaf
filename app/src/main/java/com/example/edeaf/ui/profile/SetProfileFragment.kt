package com.example.edeaf.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.edeaf.R
import com.example.edeaf.data.mechanism.handleCollect
import com.example.edeaf.databinding.FragmentSetProfilePageBinding
import com.example.edeaf.ui.profile.viewmodel.SetProfileFragmentViewModel
import com.example.edeaf.ui.util.PresentationHelper.showMaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetProfileFragment : Fragment() {
    private var _binding: FragmentSetProfilePageBinding? = null
    private val binding get() = _binding!!

    private val args: SetProfileFragmentArgs by navArgs()

    private val viewModel: SetProfileFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetProfilePageBinding.inflate(inflater, container, false)

        binding.apply {
            edtNameUser.setText(args.name)
            edtEmailUser.setText(args.email)

            binding.btnSaveProfile.setOnClickListener {
                updateData()
            }
            backFragment.setOnClickListener {
                findNavController().navigate(R.id.action_setProfilePage_to_userProfilePage)
            }

        }
        return binding.root
    }

    private fun updateData() {
        val name = binding.edtNameUser.text.toString()
        val email = binding.edtEmailUser.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.changeProfile(args.uid, name, email, args.password, args.role).handleCollect(
                onSuccess = {
                    if (email.trim() != args.email.trim()){
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                viewModel.sendEmailUpdate(email).handleCollect(
                                    onSuccess = {
                                        Toast.makeText(
                                            requireContext(),
                                            "Email successfully sent to $email",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        Log.i("updateEmailError","Send email success")

                                        findNavController().navigate(R.id.action_setProfilePage_to_userProfilePage)
                                    },
                                    onError = {result->
                                        Toast.makeText(
                                            requireContext(),
                                            "Failed to update email: ${result.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        Log.i("updateEmailError",result.message.toString())
                                    }
                                )
                            }
                        }
                    }else{
                        findNavController().navigate(R.id.action_setProfilePage_to_userProfilePage)
                    }
                },
                onError = { result->
                    requireContext().showMaterialDialog(
                        show = true,
                        message = result.message!!
                    )
                },
                onLoading = {
                    requireContext().showMaterialDialog(
                        show = true,
                        message = "Loading..."
                    )
                }
            )
        }

    }

}