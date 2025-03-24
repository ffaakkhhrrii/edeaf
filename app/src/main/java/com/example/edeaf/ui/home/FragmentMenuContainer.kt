package com.example.edeaf.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.edeaf.R
import com.example.edeaf.databinding.FragmentHomePageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentMenuContainer : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomMenu: BottomNavigationView = binding.bottomNavigationView

        bottomMenu.setupWithNavController(navController)

        binding.apply {
            bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> {
                        navController.navigate(R.id.homeFragment)
                        true
                    }
                    R.id.menu_community -> {
                        Toast.makeText(requireContext(),"Coming Soon",Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.menu_profile -> {
                        navController.navigate(R.id.userProfile)
                        true
                    }
                    else -> false
                }
            }
        }
        return binding.root
    }
}