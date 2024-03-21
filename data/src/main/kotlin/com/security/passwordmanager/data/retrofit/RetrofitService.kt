package com.security.passwordmanager.data.retrofit

import com.security.passwordmanager.domain.model.IconSite
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface RetrofitService {
    @GET
    fun getHtml(@Url url: HttpUrl): Call<String>

    @GET("https://besticon-demo.herokuapp.com/allicons.json?url={website}")
    fun getWebsiteIcons(@Query("website") websiteUrl: String): Call<IconSite>
}