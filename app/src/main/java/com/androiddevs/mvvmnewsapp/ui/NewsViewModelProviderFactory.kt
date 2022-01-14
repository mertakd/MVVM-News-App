package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.mvvmnewsapp.repository.NewsRepository

class NewsViewModelProviderFactory(
    val app: Application,
    val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository) as T //T fonksiyonun geri dönüş değeri oluyor
        //yani bu foksiyon şunu söylüyor yeni bir view model(görünüm modeli) döndürün
    }


    //kendi view modelimizin nasıl oluşturulmasın gerektiğini tanımlamak için fabrika sağlamamız gerekiyor

}