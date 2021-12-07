/*
 * Copyright (c) 2021 Samson Achiaga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.certified.audionote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.certified.audionote.R
import com.certified.audionote.adapter.NoteRecyclerAdapter
import com.certified.audionote.databinding.FragmentHomeBinding
import com.certified.audionote.model.Note
import com.certified.audionote.utils.Extensions.flags
import com.certified.audionote.utils.colors
import com.certified.audionote.utils.notes

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding?
        get() = _binding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val viewModelFactory = NotesViewModelFactory(notes)
        val viewModel: NotesViewModel by lazy {
            ViewModelProvider(this, viewModelFactory)[NotesViewModel::class.java]
        }

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        viewModel.showEmptyNotesDesign.observe(viewLifecycleOwner) {
            if (it)
                binding?.groupEmptyNotes?.visibility = View.VISIBLE
            else
                binding?.groupEmptyNotes?.visibility = View.GONE
        }

        binding?.apply {
            btnSettings.setOnClickListener { navController.navigate(R.id.action_homeFragment_to_settingsFragment) }
            fabAddNote.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(-1, colors.random())
                navController.navigate(action)
            }

            val layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerViewNotes.layoutManager = layoutManager

            val adapter = NoteRecyclerAdapter(notes)
            recyclerViewNotes.adapter = adapter
            adapter.setOnItemClickedListener(object : NoteRecyclerAdapter.OnItemClickedListener{
                override fun onItemClick(item: Note) {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(item.id, item.color)
                    navController.navigate(action)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        flags(R.color.fragment_background)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}