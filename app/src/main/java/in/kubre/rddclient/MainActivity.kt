package `in`.kubre.rddclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val profileData =
            getSharedPreferences(resources.getString(R.string.profile_data), Context.MODE_PRIVATE)
                ?: return

        if (!profileData.contains("name")) {
            val i = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(i)
        }

        f_main_lbl_name.text = profileData.getString("name", "User")
        f_main_lbl_mobile.text = profileData.getString("mobile", "9090909090")

        f_main_ll_request.setOnClickListener {
            val i = Intent(this@MainActivity, RequestActivity::class.java)
            startActivity(i)
        }

        f_main_ll_history.setOnClickListener {
            val i = Intent(this@MainActivity, HistoryActivity::class.java)
            startActivity(i)
        }

        f_main_btn_edit_profile.setOnClickListener {
            val i = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(i)
        }

        f_main_ll_help.setOnClickListener {
            AlertDialog.Builder(this@MainActivity).apply {
                setTitle("About & Help")
                setView(R.layout.help_view)
                setNeutralButton("Ok") { d, _ -> d.dismiss() }
            }.show()
        }
    }
}
