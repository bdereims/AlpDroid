package com.alpdroid.huGen10.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alpdroid.huGen10.AlpdroidApplication
import com.alpdroid.huGen10.R
import com.alpdroid.huGen10.databinding.ConfortDisplayBinding
import com.github.anastr.speedviewlib.TubeSpeedometer


@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ConfortDisplay : UIFragment(250) {

    private  var fragmentBlankBinding: ConfortDisplayBinding?=null
    lateinit var battstate:ImageView
    lateinit var batttext:TextView
    var battvalue:Float = 0.0f
    lateinit var enginestate:ImageView
    var enginevalue:Int =0
    var tankvalue:Float = 0.0f
    lateinit var tanklevel:ImageView
    lateinit var tanktext:TextView
    lateinit var washerlevel:ImageView
    lateinit var externaltemp:TextView
    lateinit var internaltemp:TextView
    lateinit var fanspeedstate:ImageView
    lateinit var nextoverhaul:TextView
    lateinit var opendoorFront:ImageView
    lateinit var opendoorRear:ImageView
    lateinit var opendoorLeft:ImageView
    lateinit var opendoorRight:ImageView

    lateinit var climfanspeed:TubeSpeedometer

    lateinit var humidityvalue:TextView

    lateinit var humiditypicture:ImageView

    lateinit var startstopstate : ImageView
    lateinit var escstate : ImageView
    lateinit var absstate:ImageView



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding: ConfortDisplayBinding = ConfortDisplayBinding.inflate(inflater, container, false)
        fragmentBlankBinding = binding

        try {
            if (this.context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this.context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    101
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return binding.root
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentBlankBinding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        externaltemp=fragmentBlankBinding!!.externalTemp
        internaltemp=fragmentBlankBinding!!.internalTemp

        battstate=fragmentBlankBinding!!.batterieState
        batttext=fragmentBlankBinding!!.batterieValue


        tanklevel=fragmentBlankBinding!!.gastankLevel
        tanktext=fragmentBlankBinding!!.tankValue

        enginestate=fragmentBlankBinding!!.engineState

        washerlevel=fragmentBlankBinding!!.washerLevel

        fanspeedstate=fragmentBlankBinding!!.fanSpeedstate

        nextoverhaul=fragmentBlankBinding!!.nextOverhaulKM

        opendoorFront=fragmentBlankBinding!!.cardoorFront
        opendoorRear=fragmentBlankBinding!!.cardoorRear
        opendoorLeft=fragmentBlankBinding!!.cardoorLeft
        opendoorRight=fragmentBlankBinding!!.cardoorRight

        startstopstate=fragmentBlankBinding!!.startstopState
        escstate=fragmentBlankBinding!!.escState
        absstate=fragmentBlankBinding!!.absstate

        climfanspeed=fragmentBlankBinding!!.fanspeedGauge

        humidityvalue=fragmentBlankBinding!!.humidity
        humiditypicture=fragmentBlankBinding!!.humidityState

        humiditypicture.setImageResource(R.drawable.humid_clim)

        timerTask = {
            activity?.runOnUiThread {
            if (AlpdroidApplication.app.isBound) {

                val alpineServices= AlpdroidApplication.app.alpdroidData

                climfanspeed.speedTo(alpineServices.get_IH_CoolingFanSpeed(),2)

                humidityvalue.text=String.format(" %.1f %%", alpineServices.get_IH_humidity())

                externaltemp.text=String.format(
                    " %d °C",
                    alpineServices.get_MM_ExternalTemp()-40)

                internaltemp.text=String.format(
                    " %.1f °C",
                    alpineServices.get_internalTemp())

                battvalue= 6+((alpineServices.get_BatteryVoltage_V2())/16.67).toFloat()

                tankvalue= alpineServices.get_FuelLevelDisplayed().toFloat()


                nextoverhaul.text= String.format(" %d Km", alpineServices.get_MilageMinBeforeOverhaul()*250)

                battstate.setImageResource(R.drawable.batterie_ok)
                batttext.text=String.format(
                    "%.2f V",
                    battvalue)

                if (battvalue<9.5)
                    battstate.setImageResource(R.drawable.batterie_ko)
                else if (battvalue<13.5)
                    battstate.setImageResource(R.drawable.batterie_norm)

                tanklevel.setImageResource(R.drawable.gastank_levelfull)
                tanktext.text=String.format(
                    " %.2f l",
                   tankvalue)

                if (tankvalue<5)
                    tanklevel.setImageResource(R.drawable.gastank_levellow)
                else if (tankvalue<15)
                    tanklevel.setImageResource(R.drawable.gastank_levelmed)

                enginestate.setImageResource(R.drawable.engine_ok)
                enginevalue= alpineServices.get_GlobalVehicleWarningState()
                if (enginevalue!=0)
                    enginestate.setImageResource(R.drawable.engine_check)

                washerlevel.setImageResource(R.drawable.washerlevel_norm)

                if (alpineServices.get_WasherLevelWarningState())
                    washerlevel.setImageResource(R.drawable.washerlevel_low)

                val id =
                    resources.getIdentifier(
                        "enginefanspeed_on${alpineServices.get_EngineFanSpeedRequest()}",
                        "drawable",
                        context?.packageName
                    )

                fanspeedstate.setImageResource(id)

                if (alpineServices.get_FrontLeftDoorOpenWarning()>0)
                    opendoorLeft.setImageResource((R.drawable.cardoor_leftopen))
                else
                    opendoorLeft.setImageResource((R.drawable.cardoor_left))
                if (alpineServices.get_FrontRightDoorOpenWarning()>0)
                    opendoorRight.setImageResource(R.drawable.cardoor_rightopen)
                else
                    opendoorRight.setImageResource(R.drawable.cardoor_right)
                if (alpineServices.get_BootOpenWarning()>0)
                    opendoorRear.setImageResource(R.drawable.cardoor_rearopen)
                else
                    opendoorRear.setImageResource(R.drawable.cardoor_rear)

                opendoorFront.setImageResource((R.drawable.cardoor_front))

                when (alpineServices.get_StartAutoAuthorization()) {
                    0 -> startstopstate.setImageResource(R.drawable.sas_available)
                    1 -> startstopstate.setImageResource(R.drawable.sas_on)
                    2 -> startstopstate.setImageResource(R.drawable.sas_off)
                    3 -> startstopstate.setImageResource(R.drawable.sas_off)
                }

                if (alpineServices.get_ESPDeactivatedByDriverForDisplay())
                    escstate.setImageResource(R.drawable.esc_off)
                else
                    escstate.setImageResource(R.drawable.esc_on)

            }

            }
        }
   }
}