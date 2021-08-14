package com.example.newstime

import android.app.Application
import com.example.newstime.db.NewsDatabase
import com.example.newstime.repository.NewsRepository

class NewsApplication :Application(){
    val database by lazy{ NewsDatabase.getDatabase(this)}
    val repository by lazy { NewsRepository(database.getArticleDao()) }
}