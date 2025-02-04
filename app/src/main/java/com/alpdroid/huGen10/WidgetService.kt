package com.alpdroid.huGen10

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.alpdroid.huGen10.ui.WidgetProvider
import com.alpdroid.huGen10.AlpdroidApplication

class WidgetService : RemoteViewsService() {


    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListRemoteViewsFactory(applicationContext)
    }

    class WidgetItem(
        /**
         * Label to display in the list.
         */
        val mLabel: String, val mValue: String, val mValueInt:Int
    )

    class ListRemoteViewsFactory(private val mContext: Context) :
        RemoteViewsFactory {

        private val mWidgetItems: MutableList<WidgetItem> = ArrayList()

        override fun onCreate() {

        }

        override fun onDestroy() {
            mWidgetItems.clear()
        }

        override fun getCount(): Int {
            return mWidgetItems.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            // Position will always range from 0 to getCount() - 1.

            var mValue = "Extra"

            // Construct remote views item based on the item xml file and set text based on position.
            val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
            // set ImageView et TexView d'une seule ROW
            // set mValue
            when (position) {
                0 -> {
                    rv.setTextViewText(R.id.widget_item, mWidgetItems[findElement("Cool")].mValue)
                    rv.setImageViewResource(R.id.widget_item_view, R.drawable.cool)
                    mValue=
                        "${mWidgetItems[findElement("Cool")].mLabel}:${mWidgetItems[findElement("Cool")].mValue}"
                }
                1 -> {
                    rv.setTextViewText(R.id.widget_item, mWidgetItems[findElement("Oil")].mValue)
                    rv.setImageViewResource(R.id.widget_item_view, R.drawable.oil)
                    mValue=mWidgetItems[findElement("Oil")].mLabel+":"+mWidgetItems[findElement("Oil")].mValue
                }
                2 -> {
                    rv.setTextViewText(
                        R.id.widget_item,
                        mWidgetItems[findElement("Voltage")].mValue
                    )
                    rv.setImageViewResource(R.id.widget_item_view, R.drawable.batterie)
                    mValue=mWidgetItems[findElement("Voltage")].mLabel+":"+mWidgetItems[findElement("Voltage")].mValue
                }
                3 -> {
                    rv.setTextViewText(R.id.widget_item, mWidgetItems[findElement("Gear")].mValue)
                    rv.setImageViewResource(R.id.widget_item_view, R.drawable.gear)
                    mValue=mWidgetItems[findElement("Gear")].mLabel+":"+mWidgetItems[findElement("Gear")].mValue
                }

                4 -> {
                    rv.setTextViewText(R.id.widget_item, mWidgetItems[findElement("Brake")].mValue)
                    rv.setImageViewResource(R.id.widget_item_view, R.drawable.brake)
                    mValue=mWidgetItems[findElement("Brake")].mLabel+":"+mWidgetItems[findElement("Brake")].mValue
                }
                5 -> {
                    rv.setTextViewText(
                        R.id.widget_item,
                        mWidgetItems[findElement("Throttle")].mValue
                    )
                    rv.setImageViewResource(R.id.widget_item_view, R.drawable.speed)
                    mValue=mWidgetItems[findElement("Throttle")].mLabel+":"+mWidgetItems[findElement("Throttle")].mValue
                }
            }

            // Next, we set a fill-intent which will be used to fill-in the pending intent template
            // which is set on the collection view in AppWidgetProvider.
            val fillInIntent =
                Intent().putExtra(WidgetProvider.EXTRA_CLICKED_FILE, mValue)
            rv.setOnClickFillInIntent(R.id.widget_item_layout, fillInIntent)


            // You can do heaving lifting in here, synchronously. For example, if you need to
            // process an image, fetch something from the network, etc., it is ok to do it here,
            // synchronously. A loading view will show up in lieu of the actual contents in the
            // interim.
            return rv
        }

        override fun getLoadingView(): RemoteViews? {
            // You can create a custom loading view (for instance when getViewAt() is slow.) If you
            // return null here, you will get the default loading view.
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun onDataSetChanged() {


            try{

                    val alpineServices: VehicleServices = AlpdroidApplication.app.alpdroidData


                    mWidgetItems.clear()
                    mWidgetItems.add(
                        WidgetItem(
                            "Brake",
                            String.format(
                                "%2d %%",
                                (  alpineServices.get_BrakingPressure().toFloat() / 2).toInt()
                            ), ( alpineServices.get_BrakingPressure().toFloat() / 2).toInt()
                        )
                    )

                    mWidgetItems.add(
                        WidgetItem(
                            "Throttle",
                            String.format(
                                "%2d %%",
                               (alpineServices.get_RawSensor().toFloat() / 8).toInt()
                            ), (alpineServices.get_RawSensor().toFloat() / 8).toInt())
                    )


                    mWidgetItems.add(
                        WidgetItem(
                            "Voltage",
                            String.format("%.1f V", 6+((alpineServices.get_BatteryVoltage_V2())/16.67).toFloat()),(6+((alpineServices.get_BatteryVoltage_V2())/16.67).toFloat().toInt())
                        )
                    )

                    mWidgetItems.add(
                        WidgetItem(
                            "Oil",
                            String.format("%3d °C", (alpineServices.get_OilTemperature() -40 )),(alpineServices.get_OilTemperature() - 40)
                        )
                    )

                    mWidgetItems.add(
                        WidgetItem(
                            "Cool",
                            String.format("%3d °C", (alpineServices.get_EngineCoolantTemp() - 40)),(alpineServices.get_EngineCoolantTemp() - 40)
                        )
                    )

                    mWidgetItems.add(
                        WidgetItem(
                            "Gear",
                            String.format("%3d °C", (alpineServices.get_RST_ATClutchTemperature() + 60)),(alpineServices.get_RST_ATClutchTemperature() + 60)
                        )
                    )

                }
                catch (e:Exception)
                {
                    // do nothing


                }


        }


        private fun findElement(element: String): Int {
            for (i in 0 until mWidgetItems.size) {
                if (this.mWidgetItems[i].mLabel == element) {
                    return i
                }
            }
            return -1 // if element not found
        }


    }
  }
