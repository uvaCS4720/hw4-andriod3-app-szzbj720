package edu.nd.pmcburne.hello.data.repo

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import edu.nd.pmcburne.hello.data.api.PlacemarkService
import edu.nd.pmcburne.hello.data.db.AppDatabase
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AppGraph {
    fun createRepository(context: Context): PlacemarkRepository {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cs.virginia.edu/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val service = retrofit.create(PlacemarkService::class.java)

        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "campus.db"
        ).fallbackToDestructiveMigration().build()

        return PlacemarkRepository(service, db.placemarkDao())
    }
}