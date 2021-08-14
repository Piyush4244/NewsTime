package com.example.newstime.db

import androidx.room.*
import com.example.newstime.models.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Delete
    suspend fun delete(article: Article)

    @Query("select * from article")
    fun getAllArticle(): Flow<List<Article>>

}