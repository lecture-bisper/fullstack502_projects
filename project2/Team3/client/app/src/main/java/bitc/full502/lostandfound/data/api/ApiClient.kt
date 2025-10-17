package bitc.full502.lostandfound.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object ApiClient {

    // JSON용
    fun <T> createJsonService(baseUrl: String, service: Class<T>): T {
        val retrofitJson = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofitJson.create(service)
    }

    // Scalars용
    fun <T> createScalarService(baseUrl: String, service: Class<T>): T {
        val retrofitScalar = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        return retrofitScalar.create(service)
    }
}