package com.example.todoapplication.fragments.list

import android.os.Bundle
import android.view.*
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.example.todoapplication.R
import com.example.todoapplication.data.models.ToDoData
import com.example.todoapplication.data.viewmodel.ToDoViewModel
import com.example.todoapplication.databinding.FragmentListBinding
import com.example.todoapplication.fragments.SharedViewModel
import com.example.todoapplication.fragments.list.adapter.ListAdapter
import com.example.todoapplication.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener {
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter: ListAdapter by lazy { ListAdapter() }
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View and Data binding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        // Setup RecyclerView
        setupRecyclerView()
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        // Observe LiveData
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        // set menu
        setHasOptionsMenu(true)

        // Hide soft keyboard
        hideKeyboard(requireActivity())

        return binding.root
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallBack = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = adapter.dataList[viewHolder.adapterPosition]
                // Delete Item
                mToDoViewModel.deleteData(itemToDelete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                // restore Deleted Item
                restoreDeletedData(viewHolder.itemView, itemToDelete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = AlphaInAnimationAdapter(adapter)
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
        // Swipe to Delete
        swipeToDelete(binding.recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData) {
        val snackbar =
            Snackbar.make(
                view,
                "O\'chirilgan '${deletedItem.title}'",
                Snackbar.LENGTH_LONG
            )
        snackbar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackbar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> deleteAllData()
            R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(
                this,
                Observer { adapter.setData(it) })
            R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(
                this,
                Observer { adapter.setData(it) })

        }
        return super.onOptionsItemSelected(item)
    }

    // show AlertDialog to confirm remove All Items form Database table
    private fun deleteAllData() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAllData()
            Toast.makeText(
                requireContext(),
                "Barcha ma\'lumotlar o\'chirildi",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Barchasini o\'chirish")
        builder.setMessage("Barchasini o\'chirishni roshtan ham xohlaysizmi ?")
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        mToDoViewModel.searchDatabase("%$query%").observe(this, Observer { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }
}