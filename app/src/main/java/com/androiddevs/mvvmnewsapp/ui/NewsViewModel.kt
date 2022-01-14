package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.bumptech.glide.load.engine.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app){

    //isteklerimizin yanıtlarını burada ele alacağız
    //burada live data mız olacak
    //bu yapılan isteklerle ilgili bir depişiklik olduğunda live data fragmentları bu değişiklikler hakkında bilgilendirecek

    //live data bizim fragment larımız için kullanılır. Böylece fragmentler gözlemci(observers) olarak bu live data ya abone(subscribe) olabilir


    val breakingNews: MutableLiveData<com.androiddevs.mvvmnewsapp.util.Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1 //bunu view model de yapmayıp, sadece fragment da yapsaydık sayfa sürekli sıfırlanacaktı.
    var breakingNewsResponse: NewsResponse? = null
    //gelen yanıtı breakingNewResponse a kaydedicez
    //breakingNewsResponse bu değişkeni koymamızın sebebi pagination işlemi yaparken diğer yüklenecek sayfları kaydetmemiz lazım kaydetmezsek ekranı yana döndürdüğümüzde veriler gidecek. Normal de 20 haber geliyordu daha fazlasını alacaz bunları kaydediceğiz



    val searchNews: MutableLiveData<com.androiddevs.mvvmnewsapp.util.Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null


    init {
        getBreakingNews("tr")
    }


    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : com.androiddevs.mvvmnewsapp.util.Resource<NewsResponse>{
        //breakingNews değişkeninin yani response nin başarı veya hata durumunu belirtecek. Yani yanıtın başarılı olup olmadığını kontrol ediyoruz
        if (response.isSuccessful){
            response.body()?.let {  resultResponse ->
                breakingNewsPage++ // başarılı yanıt aldığınımızda yapmak istiyeceğimiz ilk şey, bundan sonraki sayfayı yükleyebilmek için mevcut sayfa numarasını arttırmak
                if (breakingNewsResponse == null){ //ilk sayfa olmaması durumunda, api den alacağımız breaking news  yanıtı
                    breakingNewsResponse = resultResponse
                }else{ //yeni yanıtı almak istiyoruz
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return com.androiddevs.mvvmnewsapp.util.Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return com.androiddevs.mvvmnewsapp.util.Resource.Error(response.message()) //hata alırsak eğer hata mesajı
    }


    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : com.androiddevs.mvvmnewsapp.util.Resource<NewsResponse>{
        //breakingNews değişkeninin yani response nin başarı veya hata durumunu belirtecek. Yani yanıtın başarılı olup olmadığını kontrol ediyoruz
        if (response.isSuccessful){
            response.body()?.let {  resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null){ //ilk sayfa olmaması durumunda, api den alacağımız breaking news  yanıtı
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return com.androiddevs.mvvmnewsapp.util.Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return com.androiddevs.mvvmnewsapp.util.Resource.Error(response.message()) //hata alırsak eğer hata mesajı
    }


    //YEREL DEPOLAMA
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }


    private suspend fun  safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(com.androiddevs.mvvmnewsapp.util.Resource.Loading())
        try {
            if (hasInternetConnection()){ //internet varsa
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)  //yanıtımızı alıyoruz retrofitten, yani ağ yanıtımızı bu satırdan alıyoruz
                searchNews.postValue(handleSearchNewsResponse(response))
                //Burada olduğu  gibi canlı verilerde değişiklik yayınladığımızda, yükleme durumunu göndeririz
                //veya burada yanıt başarısını veya hatasını göndeririz. O zaman fragmentlarımız bu değişiklik hakkında otomatik olarak bilgilendirilecek böylece telefonu çevirdiğimizde veri kaybı yaşamayacağız
                //Repository den API yi çağırıyoruz

            }else{ // else internet olmaması durumunda
                searchNews.postValue(com.androiddevs.mvvmnewsapp.util.Resource.Error("İnternet Bağlantınız Yok :(  "))
            }

        }catch (t: Throwable){
            //son dakika haberlerinin olduğu kısında bir hata fırlatabilir.Bunu catch bloğunda kontrol ediyoruz
            when(t) {
                is IOException -> searchNews.postValue(com.androiddevs.mvvmnewsapp.util.Resource.Error("Ağ Hatası"))
                else -> searchNews.postValue(com.androiddevs.mvvmnewsapp.util.Resource.Error("Dönüştürme Hatası"))
            }
        }
    }




    private suspend fun  safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(com.androiddevs.mvvmnewsapp.util.Resource.Loading())
        try {
            if (hasInternetConnection()){ //internet varsa
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)  //yanıtımızı alıyoruz retrofitten, yani ağ yanıtımızı bu satırdan alıyoruz
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{ // else internet olmaması durumunda
                breakingNews.postValue(com.androiddevs.mvvmnewsapp.util.Resource.Error("İnternet Bağlantınız Yok :(  "))
            }

        }catch (t: Throwable){
            //son dakika haberlerinin olduğu kısında bir hata fırlatabilir.Bunu catch bloğunda kontrol ediyoruz
            when(t) {
                is IOException -> breakingNews.postValue(com.androiddevs.mvvmnewsapp.util.Resource.Error("Ağ Hatası"))
                else -> breakingNews.postValue(com.androiddevs.mvvmnewsapp.util.Resource.Error("Dönüştürme Hatası"))
            }
        }
    }



    private fun hasInternetConnection() : Boolean{
        //internetin var olup olmadığını kontrol ediyoruz
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        //connectivity manager sadece kullanıcının şu anda internete bağlı olupolmadığını tespit etmek için kullanılacak
        )as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false

                }
            }
        }
        return false
    }
}