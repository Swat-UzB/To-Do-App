package com.example.todoapplication.fragments

import android.app.Application
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.todoapplication.R
import com.example.todoapplication.data.models.Priority
import com.example.todoapplication.data.models.ToDoData

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    /** ==================================List Fragment ==================================*/
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)
    fun checkIfDatabaseEmpty(toDoData: List<ToDoData>) {
        emptyDatabase.value = toDoData.isEmpty()
    }

    /** ==================================Add/Update Fragment ==================================*/
    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            when (position) {
                2 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.red
                        )
                    )
                }
                1 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.yellow
                        )
                    )
                }
                0 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.green
                        )
                    )
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

    }

    fun verifyDataFromUser(title: String, desc: String): Boolean {
        return !(title.isEmpty() || desc.isEmpty())
    }

    fun parsePriority(priority: String): Priority {
        return when (priority) {
            "High priority" -> {
                Priority.HIGH
            }
            "Medium priority" -> {
                Priority.MEDIUM
            }
            "Low priority" -> {
                Priority.LOW
            }
            else -> {
                Priority.LOW
            }
        }
    }
}