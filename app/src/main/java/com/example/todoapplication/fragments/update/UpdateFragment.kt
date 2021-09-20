package com.example.todoapplication.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapplication.R
import com.example.todoapplication.data.models.Priority
import com.example.todoapplication.data.models.ToDoData
import com.example.todoapplication.data.viewmodel.ToDoViewModel
import com.example.todoapplication.databinding.FragmentUpdateBinding
import com.example.todoapplication.fragments.SharedViewModel

class UpdateFragment : Fragment() {
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val toDoViewModel: ToDoViewModel by viewModels()
    private val args by navArgs<UpdateFragmentArgs>()
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(
            inflater,
            container,
            false
        )
        // Set Menu
        setHasOptionsMenu(true)
        binding.toDoData = args.currrentItem
        binding.currentPrioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemDelete()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {

        val title = binding.currentTitleTextView.text.toString()
        val desc = binding.currentDescEditText.text.toString()
        val getPriority = binding.currentPrioritiesSpinner.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title, desc)
        if (validation) {
            // Update current Item
            val updatedItem = ToDoData(
                args.currrentItem.id,
                title,
                mSharedViewModel.parsePriority(getPriority),
                desc
            )
            toDoViewModel.updateData(updatedItem)
            Toast.makeText(
                requireContext(),
                "Yangilash muvaffaqiyatli amalga oshdi",
                Toast.LENGTH_SHORT
            ).show()
            // Navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(
                requireContext(),
                "Barcha maydonlarni to\'ldiring",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // show AlertDialog to Confirm item remove
    private fun confirmItemDelete() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            // deleting toDoData
            toDoViewModel.deleteData(args.currrentItem)
            Toast.makeText(
                requireContext(),
                "Muvaffaqiyatli o\'chirildi ${args.currrentItem.title}",
                Toast.LENGTH_SHORT
            ).show()
            // Navigate Back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete '${args.currrentItem.title}' ?")
        builder.setMessage("'${args.currrentItem.title}' ni olib tashlashni rostan ham xohlaysizmi?")
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}