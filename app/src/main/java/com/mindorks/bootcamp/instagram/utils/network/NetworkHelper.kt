package com.mindorks.bootcamp.instagram.utils.network

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.mindorks.bootcamp.instagram.utils.log.Logger
import java.io.IOException
import java.net.ConnectException
import javax.inject.Singleton


interface NetworkHelper {

     fun isNetworkConnected(): Boolean

     fun castToNetworkError(throwable: Throwable): NetworkError

}