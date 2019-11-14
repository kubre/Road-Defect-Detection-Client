package `in`.kubre.rddclient.service

import `in`.kubre.rddclient.data.Issue
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface AppService {

    @Multipart
    @POST("/issue")
    fun openIssue(
        @Part("name") name: String,
        @Part("mobile") mobile: String,
        @Part("details") details: String,
        @Part("latitude") latitude: String,
        @Part("longitude") longitude: String,
        @Part photo: MultipartBody.Part
    ): Call<Issue>

    @GET("issue/{mobile}")
    fun getIssues(@Path("mobile") mobile: String): Call<List<Issue>>
}