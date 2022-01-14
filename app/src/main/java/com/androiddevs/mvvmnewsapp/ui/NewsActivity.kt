package com.androiddevs.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController

import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R

import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.androiddevs.mvvmnewsapp.R.layout.activity_news)

        //supportActionBar?.hide()

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        //bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
        val navHostFragment= supportFragmentManager.findFragmentById(com.androiddevs.mvvmnewsapp.R.id.newsNavHostFragment) as NavHostFragment
        val navController= navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)


        //navigation component label dan Toolbar üzerindeki isimleri değiştiremiyoruz bazen, bu kod bloğunu eklememiz lazım.
        navController.addOnDestinationChangedListener{ controller, destination, arguments ->
            title = when (destination.id) {
                com.androiddevs.mvvmnewsapp.R.id.breakingNewsFragment -> "Son Dakika Haberleri"
                com.androiddevs.mvvmnewsapp.R.id.savedNewsFragment -> "Kaydedilen Haberler"
                com.androiddevs.mvvmnewsapp.R.id.searchNewsFragment -> "Haber Ara"
                com.androiddevs.mvvmnewsapp.R.id.articleFragment -> "HABERLER"
                else -> "NEWS"
            }

        }


        //appbar üzerindeki geri gelme butonu <--
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(
            navController = navController,
            configuration = appBarConfiguration
        )

    }

    //appbar üzerindeki geri gelme butonu <--
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.newsNavHostFragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}
