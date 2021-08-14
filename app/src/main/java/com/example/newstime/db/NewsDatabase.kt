package com.example.newstime.db

import android.content.Context
import androidx.room.*
import com.example.newstime.models.Article

@Database(entities = [Article::class],version = 1,exportSchema = false)
@TypeConverters(Converters::class)
abstract class NewsDatabase: RoomDatabase() {

    abstract fun getArticleDao():ArticleDao

    companion object{

        @Volatile
        private var INSTANCE:NewsDatabase?=null

        fun getDatabase(context: Context):NewsDatabase{
            return INSTANCE?: synchronized(this){
                val instance= Room.databaseBuilder(
                        context.applicationContext,
                        NewsDatabase::class.java,
                        "news_database"
                ).build()
                INSTANCE=instance
                instance
            }
        }
    }
}