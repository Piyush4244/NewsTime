package com.example.newstime.repository

import com.example.newstime.api.RetrofitSingleton
import com.example.newstime.db.ArticleDao
import com.example.newstime.models.Article

class NewsRepository(private val articleDao: ArticleDao) {

    suspend fun getBreakingNews(countryCode:String,pageNumber:Int)=
            RetrofitSingleton.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String,pageNumber: Int)=
            RetrofitSingleton.api.searchNews(searchQuery,pageNumber)

    suspend fun insert(article: Article){
        articleDao.insert(article)
    }

    suspend fun delete(article: Article){
        articleDao.delete(article)
    }

    fun getSavedNews()=articleDao.getAllArticle()
}