package com.example.newstime.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newstime.NewsApplication
import com.example.newstime.R
import com.example.newstime.adapter.NewsListAdapter
import com.example.newstime.databinding.FragmentBreakingNewsBinding
import com.example.newstime.ui.NewsActivity
import com.example.newstime.ui.NewsViewModel
import com.example.newstime.ui.NewsViewModelFactory
import com.example.newstime.utils.Resource
import com.example.newstime.utils.constants.Companion.QUERY_PAGE_SIZE
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BreakingNewsFragment :Fragment(R.layout.fragment_breaking_news){

    private val viewModel: NewsViewModel by activityViewModels{ NewsViewModelFactory(activity?.application!!, ((activity?.application) as NewsApplication).repository) }
    private var binding:FragmentBreakingNewsBinding?=null
    private lateinit var adapter:NewsListAdapter
    private val TAG="BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        //viewModel=(activity as NewsActivity).viewModel
        setUpRecyclerView()
        adapter.setOnItemClickListener { article->
            viewModel.article=article
            findNavController().navigate(BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment())
        }
        viewModel.breakingNews.observe(viewLifecycleOwner, {response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let { newsResponse->
                        adapter.submitList(newsResponse.articles.toList())
                        val totalPages=newsResponse.totalResults/ QUERY_PAGE_SIZE+1
                        isLastPage=totalPages==(viewModel.breakingNewsPage-1)
                        Log.i(TAG,"success$isLastPage${newsResponse.totalResults} $totalPages ${viewModel.breakingNewsPage}")
                        if(isLastPage){
                            binding?.rvBreakingNews?.setPadding(0,0,0,0)
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
        viewModel.getBreakingNews("us")
    }

    private fun hideProgressBar(){
        binding?.paginationProgressBar?.visibility=View.INVISIBLE
        isLoading=false
    }

    private fun showProgressBar(){
        binding?.paginationProgressBar?.visibility=View.VISIBLE
        isLoading=true
    }

    var isLoading=false
    var isLastPage=false
    var isScrolling=false

    private val scrollListener=object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
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
            val isTotalMoreThanVisible=totalItemCount>=QUERY_PAGE_SIZE
            val shouldPaginate=isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                Log.i(TAG,"paginate kar")
                viewModel.getBreakingNews("us")
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
            rvBreakingNews.adapter=adapter
            rvBreakingNews.layoutManager=LinearLayoutManager(activity)
            rvBreakingNews.addOnScrollListener(this@BreakingNewsFragment.scrollListener)
            Log.i(TAG,"activity $activity")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentBreakingNewsBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}