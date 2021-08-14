package com.example.newstime.models

import com.example.newstime.models.Article

data class NewsResponse(
        val articles: MutableList<Article>,
        val status: String,
        val totalResults: Int
)