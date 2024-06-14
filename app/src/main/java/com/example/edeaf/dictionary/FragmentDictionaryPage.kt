package com.example.edeaf.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.edeaf.adapter.DictionaryAdapter
import com.example.edeaf.databinding.FragmentDictionaryPageBinding
import com.example.edeaf.model.DictionaryModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentDictionaryPage : Fragment() {

    private var _binding: FragmentDictionaryPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var kamusList: ArrayList<DictionaryModel>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference

    private val args: FragmentDictionaryPageArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryPageBinding.inflate(inflater,container,false)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Kamus")
        kamusList = arrayListOf()

        getKamus(args.jenis)
        binding.apply {
            rvKamus.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 2)
            }
            back.setOnClickListener {
                // Di dalam Fragment 2
                getActivity()?.getSupportFragmentManager()?.popBackStack()
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getKamus(filter:String) {
        firebaseRef.orderByChild("jenis").equalTo(filter).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kamusList.clear()
                if (snapshot.exists()){
                    for (kamusSnap in snapshot.children){
                        val kamus = kamusSnap.getValue(DictionaryModel::class.java)
                        binding.jenisText.text = kamus?.jenis.toString()
                        kamusList.add(kamus!!)
                    }
                }
                val rvAdapter = DictionaryAdapter(kamusList)
                binding.rvKamus.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

}