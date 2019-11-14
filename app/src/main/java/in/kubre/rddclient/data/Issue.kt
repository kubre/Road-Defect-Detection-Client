package `in`.kubre.rddclient.data

import com.google.gson.annotations.SerializedName

data class Issue(
    @SerializedName("id") val issueId: String = "",
    @SerializedName("name") val name: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("details") val details: String,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("status") val status: String = "",
    @SerializedName("date") val date: String = "",
    @SerializedName("photo") val photo: String = ""
) {
    companion object {
        const val STATUS_INSPECTOR = "ins"
        const val STATUS_MAINTAINER = "mai"
        const val STATUS_ACCEPTED = "acc"
        const val STATUS_DECLINED = "dec"
    }
}
