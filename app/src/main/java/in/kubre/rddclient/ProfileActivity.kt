package `in`.kubre.rddclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.text.trimmedLength
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileData =
            getSharedPreferences(resources.getString(R.string.profile_data), Context.MODE_PRIVATE)
                ?: return

        f_profile_btn_save.setOnClickListener {
            if (f_profile_txt_name.text.isBlank()) {
                f_profile_txt_name.error = "Required!"
            } else if (f_profile_txt_mobile.text.isBlank()
                || !f_profile_txt_mobile.text.isDigitsOnly()
                || f_profile_txt_mobile.text.trimmedLength() != 10
            ) {
                f_profile_txt_mobile.error = "Required and should be 10 digits only!"
            } else {
                with(profileData.edit()) {
                    putString("name", f_profile_txt_name.text.toString())
                    putString("mobile", f_profile_txt_mobile.text.toString())
                    putString("ip", f_profile_txt_port.text.toString())
                    commit()
                }
                val i = Intent(this@ProfileActivity, MainActivity::class.java)
                startActivity(i)
            }
        }
    }
}
