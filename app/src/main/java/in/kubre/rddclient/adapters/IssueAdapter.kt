package `in`.kubre.rddclient.adapters

import `in`.kubre.rddclient.R
import `in`.kubre.rddclient.data.Issue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class IssueAdapter(val appUrl: String) : ListAdapter<Issue, IssueAdapter.IssueHolder>(IssueDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueHolder {
        val issueView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_request, parent, false)
        return IssueHolder(issueView)
    }

    override fun onBindViewHolder(holder: IssueHolder, position: Int) {
        val currentIssue = getItem(position)
        holder.apply {
            lblDetails.text = "Details: ${currentIssue.details}"
            lblDate.text = "Issue reported: ${currentIssue.date}"
            lblStatus.text = "Status: " + when (currentIssue.status) {
                Issue.STATUS_ACCEPTED -> "Work is Started"
                Issue.STATUS_DECLINED -> "Issue Declined"
                Issue.STATUS_INSPECTOR -> "Issue at Road Inspection Officer"
                Issue.STATUS_MAINTAINER -> "Issue at Road Maintenance Agency"
                else -> ""
            }
            Glide.with(itemView)
                .load("${appUrl}${currentIssue.photo}")
                .into(imgPhoto)
        }
    }

    class IssueHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lblDetails: TextView = view.findViewById(R.id.li_request_txt_description)
        var lblDate: TextView = view.findViewById(R.id.li_request_txt_date)
        var lblStatus: TextView = view.findViewById(R.id.li_request_txt_status)
        var imgPhoto: ImageView = view.findViewById(R.id.li_request_img_photo)
    }

    class IssueDiff : DiffUtil.ItemCallback<Issue>() {
        override fun areItemsTheSame(oldItem: Issue, newItem: Issue) =
            oldItem.issueId == newItem.issueId

        override fun areContentsTheSame(oldItem: Issue, newItem: Issue) =
            oldItem == newItem
    }
}