package com.mariokevin.tendamonitoring.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.mariokevin.tendamonitoring.R
import kotlinx.android.synthetic.main.device_list_item.view.*
import kotlin.math.truncate

class DevicesAdapter(items: ArrayList<onlineList>, context: Context) :
    RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    var items: ArrayList<onlineList>? = null
    var context: Context? = null

    init {
        this.items = items
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.device_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.items?.count()!!
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)
        holder.hostName?.text = item?.qosListHostname
        holder.dSpeed?.text = truncate(item?.qqosListDownSpeed!!.toFloat()).toString() + " KB/s"
        holder.uSpeed?.text = truncate(item?.qqosListUpSpeed.toFloat()).toString() + " KB/s"
        if (item.qqosListConnectType.equals("wifi"))
            holder.conType?.setImageDrawable(
                ResourcesCompat.getDrawable(context!!.resources, R.drawable.ic_wifi, null)
            )
        else holder.conType?.setImageDrawable(
            ResourcesCompat.getDrawable(context!!.resources, R.drawable.ic_ethernet, null)
        )
        holder.hostIP?.text = item.qqosListIP
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var hostName: TextView? = null
        var hostIP: TextView? = null
        var conType: ImageView? = null
        var dSpeed: TextView? = null
        var uSpeed: TextView? = null

        init {
            hostIP = itemView.deviceIP
            hostName = itemView.deviceName
            conType = itemView.connectionTypeIcon
            dSpeed = itemView.dSpeedDevice
            uSpeed = itemView.uSpeedDevice

        }
    }

    class onlineList(
        qosListHostname: String,
        qosListRemark: String,
        qqosListIP: String,
        qqosListConnectType: String,
        qqosListMac: String,
        qqosListDownSpeed: String,
        qqosListUpSpeed: String,
        qqosListDownLimit: String,
        qqosListUpLimit: String,
        qqosListAccess: String
    ) {
        var qosListHostname: String = ""
        var qosListRemark: String = ""
        var qqosListIP: String = ""
        var qqosListConnectType: String = ""
        var qqosListMac: String = ""
        var qqosListDownSpeed: String = ""
        var qqosListUpSpeed: String = ""
        var qqosListDownLimit: String = ""
        var qqosListUpLimit: String = ""
        var qqosListAccess: String = ""

        init {
            this.qosListHostname = qosListHostname
            this.qosListRemark = qosListRemark
            this.qqosListIP = qqosListIP
            this.qqosListConnectType = qqosListConnectType
            this.qqosListMac = qqosListMac
            this.qqosListDownSpeed = qqosListDownSpeed
            this.qqosListUpSpeed = qqosListUpSpeed
            this.qqosListDownLimit = qqosListDownLimit
            this.qqosListUpLimit = qqosListUpLimit
            this.qqosListAccess = qqosListAccess
        }

    }
}