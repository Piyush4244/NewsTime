package com.example.newstime.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newstime.NewsApplication
import com.example.newstime.R
import com.example.newstime.adapter.NewsListAdapter
import com.example.newstime.databinding.FragmentSearchNewsBinding
import com.example.newstime.ui.NewsActivity
import com.example.newstime.ui.NewsViewModel
import com.example.newstime.ui.NewsViewModelFactory
import com.example.newstime.utils.Resource
import com.example.newstime.utils.constants
import com.example.newstime.utils.constants.Companion.SEARCH_NEWS_DELAY_TIME
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment :Fragment(R.layout.fragment_search_news){

    private val viewModel: NewsViewModel by activityViewModels{ NewsViewModelFactory(activity?.application!!,((activity?.application) as NewsApplication).repository) }
    private lateinit var adapter:NewsListAdapter
    private val TAG="SearchNewsFragment"
    private var binding:FragmentSearchNewsBinding?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel=(activity as NewsActivity).viewModel
        setUpRecyclerView()

        adapter.setOnItemClickListener {article ->
            viewModel.article=article
            findNavController().navigate(SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment())
        }

        var job:Job?=null
        binding?.etSearch?.addTextChangedListener{ editable->
            job= MainScope().launch {
                delay(SEARCH_NEWS_DELAY_TIME)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchNewsPage=1
                        isLastPage=false
                        isScrolling=false
                        isLoading=false
                        viewModel.searchNewsResponse=null
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, {response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let { newsResponse->
                        adapter.submitList(newsResponse.articles)
                        Log.i(TAG,"success")
                        val totalPages=newsResponse.totalResults/ constants.QUERY_PAGE_SIZE +1
                        isLastPage=totalPages==(viewModel.searchNewsPage-1)
                        //Log.i(TAG,"success$isLastPage${newsResponse.totalResults} $totalPages ${viewModel.breakingNewsPage}")
                        if(isLastPage){
                            binding?.rvSearchNews?.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let {
                        Log.e(TAG,"an error occured $it")
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar(){
        binding?.paginationProgressBar?.visibility= View.INVISIBLE
        isLoading=false
    }

    private fun showProgressBar(){
        binding?.paginationProgressBar?.visibility= View.VISIBLE
        isLoading=true
    }

    var isLoading=false
    var isLastPage=false
    var isScrolling=false

    private val scrollListener=object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling=true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition=layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount=layoutManager.childCount
            val totalItemCount=layoutManager.itemCount

            val isNotLoadingAndNotLastPage=!isLoading && !isLastPage
            val isAtLastItem=firstVisibleItemPosition+visibleItemCount+2>=totalItemCount
            val isNotAtBeginning=firstVisibleItemPosition>0
            val isTotalMoreThanVisible=totalItemCount>= constants.QUERY_PAGE_SIZE
            val shouldPaginate=isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                Log.i(TAG,"paginate kar")
                viewModel.searchNews(binding?.etSearch?.editableText.toString())
                isScrolling=false
            }
            else {
                Log.i(TAG,"paginate mat kar$isNotLoadingAndNotLastPage$isAtLastItem$isNotAtBeginning$isTotalMoreThanVisible$isScrolling")
            }
        }
    }

    private fun setUpRecyclerView(){
        adapter= NewsListAdapter(this)
        binding?.apply {
            rvSearchNews.adapter=adapter
            rvSearchNews.layoutManager= LinearLayoutManager(activity)
            rvSearchNews.addOnScrollListener(this@SearchNewsFragment.scrollListener)
            Log.e(TAG,"activity $activity")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentSearchNewsBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}