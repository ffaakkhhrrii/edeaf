package com.example.edeaf.ui.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.edeaf.R
import com.example.edeaf.data.mechanism.handleCollect
import com.example.edeaf.databinding.FragmentSignUpBinding
import com.example.edeaf.ui.login.LoginFragmentDirections
import com.example.edeaf.data.model.Users
import com.example.edeaf.ui.util.PresentationHelper.showMaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    val role = arrayOf("Student", "Teacher")
    private val viewModel: SignupFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, role)
        adapter.setDropDownViewResource(androidx.transition.R.layout.support_simple_spinner_dropdown_item)

        binding.apply {
            edtRoleSignUp.adapter = adapter
            btnSignUp.setOnClickListener {
                val name = edtNameSignUp.text.toString()
                val email = edtEmailSignUp.text.toString()
                val role = edtRoleSignUp.selectedItem.toString()
                val password = edtPasswordSignUp.text.toString()

                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.signUp(name, email, role, password).handleCollect(
                            onSuccess = { result->
                                requireContext().showMaterialDialog(
                                    show = true,
                                    message = "Signup Success"
                                )

                                findNavController().navigate(R.id.action_signUp_to_loginPage)
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
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Data must be filled in",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return binding.root
    }

}