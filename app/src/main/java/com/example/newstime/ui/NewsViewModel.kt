package com.example.newstime.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.newstime.NewsApplication
import com.example.newstime.models.Article
import com.example.newstime.models.NewsResponse
import com.example.newstime.repository.NewsRepository
import com.example.newstime.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app:Application,private val repository: NewsRepository) : AndroidViewModel(app) {

    val breakingNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage=1
    var breakingNewsResponse:NewsResponse?=null

    val searchNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage=1
    var searchNewsResponse:NewsResponse?=null

    var article:Article?=null
    val savedNews:LiveData<List<Article>> =repository.getSavedNews().asLiveData()

    fun getBreakingNews(countryCode:String)=viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response=repository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }
            else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->breakingNews.postValue(Resource.Error("Network Failure"))
                else ->breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {newsResponse->
                breakingNewsPage++
                if(breakingNewsResponse==null){
                    breakingNewsResponse=newsResponse
                }else {
                    val oldArticles=breakingNewsResponse?.articles
                    val newArticles=newsResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse?:newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun searchNews(searchQuery:String)=viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response=repository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }
            else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->searchNews.postValue(Resource.Error("Network Failure"))
                else ->searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {newsResponse->
                searchNewsPage++
                if(searchNewsResponse==null){
                    searchNewsResponse=newsResponse
                }else {
                    val oldArticles=searchNewsResponse?.articles
                    val newArticles=newsResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?:newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article)=viewModelScope.launch {
        repository.insert(article)
    }

    fun deleteArticle(article: Article)=viewModelScope.launch {
        repository.delete(article)
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager=getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            val activeNetwork=connectivityManager.activeNetwork?:return false
            val capabilities=connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI)->true
                capabilities.hasTransport((TRANSPORT_CELLULAR))->true
                capabilities.hasTransport(TRANSPORT_ETHERNET)->true
                else ->false
            }
        }
        else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI->true
                    TYPE_ETHERNET->true
                    TYPE_MOBILE->true
                    else->false
                }
            }
        }
        return false
    }
}

class NewsViewModelFactory(private val app: Application,private val repository: NewsRepository): ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T{
        if(modelClass.isAssignableFrom(NewsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(app,repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}