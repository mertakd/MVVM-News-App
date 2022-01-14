package com.androiddevs.mvvmnewsapp.util

sealed class Resource<T>(val data: T? = null,
                         val message: String? = null) {


    class Success<T>(data: T) : Resource<T>(data) //eğer başarımız varsa bu null olamaz
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()

    //T şunu ifade ediyor istediğimzi türden işlemleri gerçekleştirebileceğiz(string,int,Boolean vs)
    //bu yapı olmasaydı string için ayrı int için ayrı ayrı sınıflar oluşturmak zorunda kalacaktık

    //Bu sınıfı oluşturmamızın sebebi başarı ve hata yanıtları arasında ayrım yapmak

    //Ayrıca yükleme(loading) durumunu daha rahat ele alıyoruz.ProgressBar kullanıyoruz

    //when yapısını kullanıyoruz


    //GENERIC CLASS LAR
    //Genericler kodların tekrar tekrar yazılmasını önleyen yapılardır.
    //Farklı tip de  class ya da fonksiyonlarlar çalışmak için generic yapıları kullanıyoruz


    //SEALED CLASS
    //Soyut bir sınıftır, ancak hangi sınıfların bu kaynak sınıftan miras almasına izin verildiğini tanımlayabiliriz.
}