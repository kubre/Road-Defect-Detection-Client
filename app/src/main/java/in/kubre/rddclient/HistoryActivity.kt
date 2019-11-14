package `in`.kubre.rddclient

import `in`.kubre.rddclient.adapters.IssueAdapter
import `in`.kubre.rddclient.data.Issue
import `in`.kubre.rddclient.service.AppService
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val profileData =
            getSharedPreferences(resources.getString(R.string.profile_data), Context.MODE_PRIVATE)
                ?: return

        val appUrl = "http://${profileData.getString("ip", "192.168.43.224")}:5000"

        f_history_rec_history.layoutManager = LinearLayoutManager(this)
        f_history_rec_history.setHasFixedSize(true)

        // Instantiating Adapter
        val issueAdapter = IssueAdapter(appUrl)
        f_history_rec_history.adapter = issueAdapter

        val service = Retrofit.Builder()
            .baseUrl(appUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(AppService::class.java)

        service.getIssues(profileData.getString("mobile", "9090909090")!!).enqueue(
            object : Callback<List<Issue>> {
                override fun onFailure(call: Call<List<Issue>>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<List<Issue>>, response: Response<List<Issue>>) {
                    if (response.isSuccessful) {
                        issueAdapter.submitList(response.body())
                    }
                }
            }
        )
    }
}
