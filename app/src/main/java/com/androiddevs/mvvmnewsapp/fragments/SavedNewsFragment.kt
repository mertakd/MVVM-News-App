package com.androiddevs.mvvmnewsapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news){

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        //SWİPE/KAYDIRARAK SİLME İŞLEMİ
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            //recyclerview da süreklememiz gereken yönleri belirtmemiz gerekiyor
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition  //sildiğimiz ögenin pozisyonunu alıyoruz.Böylece sola veya sağa kaydırırız
                val article = newsAdapter.differ.currentList[position] //ardından bağdaştırıcı konumunu ve veritabanımızda silmek istediğimiz verileri çağırıyoruz
                viewModel.deleteArticle(article)
                Snackbar.make(view, "Haber başarıyla silindi", Snackbar.LENGTH_LONG).apply {
                    setAction("GERİ AL"){
                        viewModel.saveArticle(article)
                        //silinen haberi tekrar kaydetmemize yarar
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        //tüm haberleri çekiyor
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles) //eski verileri yenisiyle otomatik güncelliyor
            //Fragment ın içindeyiz, observer yani gözlemcimiz var. ne zaman veri tabanındaki veriler değişse bu gözlemci yani Observer çağrılır.
            //Bize article listesini verir bu listeyle istediğimiz işlemi yaparız
        })

    }


    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}