package com.example.tom_and_jerry_game_task_2a

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.lang.Exception

private val retrofit = Retrofit.Builder()
    .baseUrl("https://chasedeux.vercel.app")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val retrofitServiceForObstacleLimit: ApiServiceForObstacleLimit = retrofit.create(ApiServiceForObstacleLimit::class.java)
val retrofitServiceForTomImage: ApiServiceForImageOfTom = retrofit.create(ApiServiceForImageOfTom::class.java)
val retrofitServiceForJerryImage : ApiServiceForJerryImage = retrofit.create(ApiServiceForJerryImage::class.java)
val retrofitServiceForObstacleImage : ApiServiceForObstacleImage = retrofit.create(ApiServiceForObstacleImage::class.java)
val retrofitServiceForHitHindrance: ApiServiceForhitHindrance = retrofit.create(ApiServiceForhitHindrance::class.java)
val retrofitServiceForRandomWord: ApiServiceForRandomWord = retrofit.create(ApiServiceForRandomWord::class.java)
val retrofitServiceForTheme : ApiServiceForTheme = retrofit.create(ApiServiceForTheme::class.java)
val retrofitServiceForObstacleCourse : ApiServiceForObstacleCourse = retrofit.create(ApiServiceForObstacleCourse::class.java)

data class ObstacleLimitResponse(
    @SerializedName("obstacleLimit") val limit : Int
)

data class HitHindranceResponse(
    @SerializedName("type") val type : Int,
    @SerializedName("amount") val amount : Int,
    @SerializedName("description") val description : String
)

data class RandomWordRequest(
    @SerializedName("length") val length: Int
)

data class RandomWordResponse(
    @SerializedName("word") val word : String
)

data class ThemeRequest(
    @SerializedName("date") val date : String,
    @SerializedName("time") val time : String
)

data class ThemeResponse(
    @SerializedName("theme") val theme : String
)

data class ObstacleCourseRequest(
    @SerializedName("extent") val extent : Int
)

data class ObstacleCourseResponse(
    @SerializedName("obstacleCourse") val obstacleCourse : List<String>
)

interface ApiServiceForObstacleLimit {
    @GET("/obstacleLimit")
    suspend fun getObstacleLimit() : ObstacleLimitResponse
}

interface ApiServiceForImageOfTom {
    @GET("/image")
    suspend fun getTomImage(
        @Query("character") character : String
    ) : ResponseBody
}

interface ApiServiceForJerryImage {
    @GET("/image")
    suspend fun getJerryImage(
        @Query("character") character: String
    ) : ResponseBody
}

interface ApiServiceForObstacleImage {
    @GET("/image")
    suspend fun getObstacleImage(
        @Query("character") character : String
    ) : ResponseBody
}

interface ApiServiceForhitHindrance {
    @GET("/hitHindrance")
    suspend fun getHitHindrance() : HitHindranceResponse
}

interface ApiServiceForRandomWord {
    @POST("/randomWord")
    suspend fun getRandomWord(
        @Body length : RandomWordRequest
    ) : Response<RandomWordResponse>
}

interface ApiServiceForTheme {
    @POST("/theme")
    suspend fun getTheme(
        @Body dateAndTime : ThemeRequest
    ) : Response<ThemeResponse>
}

interface ApiServiceForObstacleCourse {
    @POST("/obstacleCourse")
    suspend fun getObstacleCourse(
        @Body extent : ObstacleCourseRequest
    ) : Response<ObstacleCourseResponse>
}