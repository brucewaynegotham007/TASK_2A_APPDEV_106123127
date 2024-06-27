package com.example.tom_and_jerry_game_task_2a

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import java.lang.Exception

private val retrofit = Retrofit.Builder()
    .baseUrl("https://chasedeux.vercel.app")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val retrofitServiceForObstacleLimit: ApiServiceForObstacleLimit = retrofit.create(ApiServiceForObstacleLimit::class.java)

data class ObstacleLimitResponse(
    @SerializedName("obstacleLimit") val limit : Int
)

interface ApiServiceForObstacleLimit {
    @GET("/obstacleLimit")
    suspend fun getObstacleLimit() : ObstacleLimitResponse
}