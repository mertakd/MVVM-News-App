package com.androiddevs.mvvmnewsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Serializable


//Android'de nesneleri yalnızca etkinliklere(fragmentlara ya da aktiviylere aktaramayız. Bunu yapmak için nesnelerin ya uygulanması Serializableya da Parcelablearabirimi olmalıdır .

/*
   MAKALE NOTLARI

 Java platformunda bilindiği gibi, int, double, byte gibi primitive tipler dışındaki herşey nesnedir.

Ancak Java’da kullanılan nesneler, Java platformunda (JVM) hayat bulurlar.
Platform dışında nesnelerin, hiçbir anlamı yoktur. Nesne yönelimli programlama paradigmasını destekleyen Java’da, tasarlanan nesnelerin tekrar kullanılabilmesi (reuse) önemli bir konu olduğuna göre, bu nesneleri Java platformu dışında da hayata geçirmek gerçekten önemlidir.
Bahsedilen bu problem, Java Serialization API sayesinde çok kolay bir şekilde aşılabiliyor.
 */


/*
    MEDİUM NOT https://medium.com/@kilictugba8/activityler-aras%C4%B1-veri-ta%C5%9F%C4%B1ma-y%C3%B6ntemleri-fdd031bb9631

Yukarıda da belirttiğim gibi primitive tipler için Intent ve Bundle kullanılır. Fakat daha büyük projeler için referans tipler için Serializable ve Parcelable veri gönderme/taşıma/aktarım yöntemleri vardır.
Serializable ve Parcelable; daha büyük verileri obje yani nesnelerin gönderimde kullandığımız yöntemlerdir. Fakat bu yöntemler arasında da farklar bulunmaktadır. Örnek verecek olursak;
Serializable bir çok ortamda kullanılabilir; mobil, masaüstü vs.
Parcelable sadece Android için geliştirilmiştir.
Parcelable, Seralizable’dan 10 kat daha hızlıdır diyebiliriz.
Yani Parcelable, Serializable’a göre daha performanslıdır diyebiliriz.
 */