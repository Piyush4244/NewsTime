package com.example.newstime.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.newstime.NewsApplication
import com.example.newstime.R
import com.example.newstime.databinding.FragmentArticleBinding
import com.example.newstime.ui.NewsViewModel
import com.example.newstime.ui.NewsViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ArticleFragment :Fragment(R.layout.fragment_article){

    private val viewModel: NewsViewModel by activityViewModels{ NewsViewModelFactory(activity?.application!!,((activity?.application) as NewsApplication).repository) }
    private var binding:FragmentArticleBinding?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article=viewModel.article
        article!!.url?.let { binding?.webView?.loadUrl(it) }
        binding?.fab?.setOnClickListener {
            viewModel.saveArticle(article!!)
            Snackbar.make(view,"Article Saved",Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentArticleBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }
}