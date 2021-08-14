package com.example.newstime.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newstime.NewsApplication
import com.example.newstime.R
import com.example.newstime.adapter.NewsListAdapter
import com.example.newstime.databinding.FragmentSavedNewsBinding
import com.example.newstime.ui.NewsViewModel
import com.example.newstime.ui.NewsViewModelFactory
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment :Fragment(R.layout.fragment_saved_news){
    private val viewModel: NewsViewModel by activityViewModels{ NewsViewModelFactory(activity?.application!!,((activity?.application) as NewsApplication).repository) }
    private lateinit var adapter: NewsListAdapter
    private val TAG="SavedNewsFragment"
    private var binding: FragmentSavedNewsBinding?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        val itemTouchHelperCallback=object :ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or  ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val article=adapter.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"Successfully article deleted",Snackbar.LENGTH_LONG).apply {
                    setAction("undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding?.rvSavedNews)
        adapter.setOnItemClickListener {article ->
            viewModel.article=article
            findNavController().navigate(SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment())
        }

        viewModel.savedNews.observe(viewLifecycleOwner,{ articles->
            adapter.submitList(articles)
        })
    }

    private fun setUpRecyclerView(){
        adapter= NewsListAdapter(this)
        binding?.apply {
            rvSavedNews.adapter=adapter
            rvSavedNews.layoutManager= LinearLayoutManager(activity)
            Log.e(TAG,"activity $activity")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentSavedNewsBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }


}