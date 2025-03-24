package com.example.edeaf.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.edeaf.LoginActivity
import com.example.edeaf.R
import com.example.edeaf.data.mechanism.handleCollect
import com.example.edeaf.databinding.FragmentUserProfilePageBinding
import com.example.edeaf.data.model.Users
import com.example.edeaf.ui.profile.viewmodel.UserProfileFragmentViewModel
import com.example.edeaf.ui.util.PresentationHelper.showMaterialDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfilePageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfilePageBinding.inflate(inflater, container, false)

        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.showDataProfile().handleCollect(
                    onSuccess = { result->
                        userName.text = result.data?.name
                        userEmail.text = result.data?.email
                        btnSetProfile.setOnClickListener {
                            val action = UserProfileFragmentDirections.actionUserProfilePageToSetProfilePage(
                                userName.text.toString(),
                                userEmail.text.toString(),
                                result.data?.userId.toString(),
                                result.data?.password.toString(),
                                result.data?.role.toString()
                            )
                            findNavController().navigate(action)
                        }
                    },
                    onError = { result->
                        requireContext().showMaterialDialog(
                            show = true,
                            message = result.message!!
                        )
                    }
                )
            }

            logoutUser.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.logout().handleCollect(
                        onSuccess = {
                            MaterialAlertDialogBuilder(requireContext())
                                .setMessage("Are you sure want to logout?")
                                .setPositiveButton("Yes"){_,_->
                                    clearCredentials()
                                    val intent = Intent(requireContext(), LoginActivity::class.java)
                                    startActivity(intent)
                                    activity?.finish()
                                }
                                .setNegativeButton("Cancel"){dialog,_->
                                    dialog.dismiss()
                                }
                                .show()

                        },
                        onError = { result->
                            requireContext().showMaterialDialog(
                                show = true,
                                message = result.message!!
                            )
                        },
                    )
                }
            }

            btnKeamanan.setOnClickListener {
                findNavController().navigate(R.id.action_userProfilePage_to_changePasswordPage)
            }
            btnReport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","edeaf.gmove@gmail.com",null))
                startActivity(Intent.createChooser(emailIntent,"Provide feedback"))
            }
        }
        return binding.root
    }

    private fun clearCredentials() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.remove("password")
        editor.apply()
    }
}