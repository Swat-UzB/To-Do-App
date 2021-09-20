package com.example.todoapplication.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapplication.R
import com.example.todoapplication.data.models.Priority
import com.example.todoapplication.data.models.ToDoData
import com.example.todoapplication.data.viewmodel.ToDoViewModel
import com.example.todoapplication.databinding.FragmentAddBinding
import com.example.todoapplication.fragments.SharedViewModel

class AddFragment : Fragment() {
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        // set menu
        setHasOptionsMenu(true)
        binding.prioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {
        with(binding) {
            val mTitle = titleTextView.text.toString()
            val mPriority = prioritiesSpinner.selectedItem.toString()
            val mDesc = descEditText.text.toString()
            val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDesc)
            if (validation) {
                // Insert data to Database
                val newData = ToDoData(
                    0,
                    mTitle,
                    mSharedViewModel.parsePriority(mPriority),
                    mDesc

                )
                mToDoViewModel.insertData(newData)
                Toast.makeText(
                    requireContext(),
                    "Muvaffaqiyatli qo\'shildi",
                    Toast.LENGTH_SHORT
                ).show()
                // Navigate Back
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Barcha maydonlarni to\'ldiring",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}