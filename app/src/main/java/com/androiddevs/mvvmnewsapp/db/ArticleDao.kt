package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {

    //veri tabanına bir haber(article) eklemek için
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long


    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>
    //tüm haberleri çekme işlevi

    @Delete
    suspend fun deleteArticle(article: Article)
}