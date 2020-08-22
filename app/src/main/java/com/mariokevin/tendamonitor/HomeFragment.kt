package com.mariokevin.tendamonitor

import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mariokevin.tendamonitor.models.DevicesAdapter
import com.mariokevin.tendamonitor.util.TendaAPI
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONArray

class HomeFragment : Fragment() {
    var TAG = "homeF"
    var __ROUTERIP: String? = null
    var __REFRESH_RATE: Long = 1000
    var dispositivos = ArrayList<DevicesAdapter.onlineList>()
    var adapter: DevicesAdapter? = null
    var routerName: String? = null
    var tenda: TendaAPI? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        configureChart(view)
        tenda = TendaAPI("123asd", activity!!)
        print(tenda!!.checkSesion())
        Thread {
            getSpeedInfo()
        }.start()
        Thread {
            getDevicesList()
        }.start()

        return view
    }

    private fun configureChart(view: View) {
        val data = LineData()
        view.chart2?.data = data
        view.chart2?.description?.isEnabled = true
        view.chart2?.description?.text = "Download"
        view.chart2?.setTouchEnabled(false)
        view.chart2?.isDragEnabled = false
        view.chart2?.animateXY(1000, 1000)
        view.chart2?.setDrawGridBackground(false)
        view.chart2?.axisLeft?.setDrawGridLines(false)
        view.chart2?.axisRight?.setDrawGridLines(false)
        view.chart2?.axisRight?.setDrawLabels(false)
        view.chart2?.axisLeft?.setDrawLabels(false)
        view.chart2?.xAxis?.setDrawLabels(false)
        view.chart2?.setDrawBorders(false)
        view.chart2?.axisLeft?.setDrawAxisLine(false)
        view.chart2?.axisRight?.setDrawAxisLine(false)
        view.chart2?.xAxis?.setDrawGridLines(false)
        view.chart2?.xAxis?.setDrawAxisLine(false)
        view.chart2?.legend?.isEnabled = false
        view.chart2?.data?.addEntry(Entry(0.toFloat(), 0.toFloat()), 0)
        view.chart2?.extraBottomOffset = 5.toFloat()
        view.chart2?.extraTopOffset = 5.toFloat()
    }

    private fun getDevicesList() {
        adapter = DevicesAdapter(dispositivos, context!!)
        view?.deviceList?.adapter = adapter
        while (true) {
            try {
                val objct = tenda!!.getOnlineDevices()
                val jsonA = objct.get("onlineList") as JSONArray
                var deviceObjct: DevicesAdapter.onlineList?
                dispositivos.clear()

                for (x in 0 until jsonA.length()) {
                    val device = jsonA.getJSONObject(x)
                    deviceObjct = DevicesAdapter.onlineList(
                        device["qosListHostname"].toString(),
                        device["qosListRemark"].toString(),
                        device["qosListIP"].toString(),
                        device["qosListConnectType"].toString(),
                        device["qosListMac"].toString(),
                        device["qosListDownSpeed"].toString(),
                        device["qosListUpSpeed"].toString(),
                        device["qosListDownLimit"].toString(),
                        device["qosListUpLimit"].toString(),
                        device["qosListAccess"].toString()
                    )
                    dispositivos.add(deviceObjct)
                }
                activity?.runOnUiThread {
                    adapter!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Thread.sleep(5000)
        }
    }


    private fun getSpeedInfo() {
        var up: String? = null
        var down: String? = null
        while (true) {
            try {
                val json = tenda!!.getDeviceStatistics()
                up = json.getJSONObject("deviceStastics").getString("statusUpSpeed")
                down = json.getJSONObject("deviceStastics").getString("statusDownSpeed")
                if (routerName == null) {
                    routerName = json.getJSONObject("deviceStastics").getString("routerName")
                    activity?.runOnUiThread { view?.routerName?.setText(routerName) }
                }
                val data = view?.chart2?.data
                var set = data?.getDataSetByIndex(0)

                if (set == null) {
                    set = createSet()
                    data?.addDataSet(set)
                }

                if (set.entryCount > 10) {
                    set.removeFirst()
                    for (i in 0 until set.entryCount) {
                        val entryToChange =
                            set.getEntryForIndex(i)
                        entryToChange.x = entryToChange.x - 1
                    }
                }

                val entry = Entry(set.entryCount.toFloat(), down.toFloat())
                data?.addEntry(entry, 0)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            activity?.runOnUiThread {
                dSpeed.text = down
                uSpeed.text = up
                view?.chart2?.data?.notifyDataChanged()
                view?.chart2?.invalidate()
                view?.chart2?.notifyDataSetChanged()
            }
            Thread.sleep(__REFRESH_RATE)
        }
    }

    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, null)
        set.fillAlpha = 0
        set.setDrawIcons(false)
        set.highLightColor = resources.getColor(R.color.colorPrimary, null)
        set.setCircleColor(resources.getColor(R.color.colorPrimary, null))
        set.circleRadius = 8.toFloat()
        set.circleHoleRadius = 5.toFloat()
        set.setDrawFilled(true)
        set.lineWidth = 3f
        set.color = resources.getColor(R.color.colorPrimary, null)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    private fun setRouterIp() {
        if (__ROUTERIP.isNullOrBlank()) {
            val manager = super.getActivity()?.getApplicationContext()
                ?.getSystemService(WIFI_SERVICE) as WifiManager
            __ROUTERIP = Formatter.formatIpAddress(manager.dhcpInfo.gateway)
        }
    }
}