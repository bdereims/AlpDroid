package com.alpdroid.huGen10.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import com.alpdroid.huGen10.AlpdroidApplication
import com.alpdroid.huGen10.R
import com.alpdroid.huGen10.databinding.ComputerDisplayBinding
import com.alpdroid.huGen10.obdUtil.DtcBody
import com.alpdroid.huGen10.obdUtil.DtcChassis
import com.alpdroid.huGen10.obdUtil.DtcNetwork
import com.alpdroid.huGen10.obdUtil.DtcPowertrain
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ComputerDisplay : UIFragment(1500) {

    private  var fragmentBlankBinding: ComputerDisplayBinding?=null
    lateinit var ac_header : TextView
    lateinit var framedatadisplay : TextView


    lateinit var iterator:Iterator<Int>

    var ptc_see:Boolean = false

    var rtxTimer:Long=0

    var sharedPreferences: SharedPreferences? = null


    lateinit var mirror_switch: SwitchMaterial

    lateinit var startstop_switch: SwitchMaterial

    lateinit var carpark_switch: SwitchMaterial


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val binding = ComputerDisplayBinding.inflate(inflater, container, false)
        fragmentBlankBinding = binding
        fragmentBlankBinding!!.ptcbutton.setOnClickListener {
               obdptclaunch()
            }

        fragmentBlankBinding!!.resetDtc.setOnClickListener {
            obdptcreset()
        }



        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        mirror_switch = fragmentBlankBinding!!.mirrorswitch
        val mirror_switchState = this.sharedPreferences?.getBoolean("mirror_switch", false)
        if (mirror_switchState != null) {
            mirror_switch.isChecked = mirror_switchState
        }

        startstop_switch = fragmentBlankBinding!!.startstopswitch
        val startStop_switchState = this.sharedPreferences?.getBoolean("startstop_switch", false)
        if (startStop_switchState != null) {
            startstop_switch.isChecked = startStop_switchState
        }

       carpark_switch = fragmentBlankBinding!!.carparkswitch
        val carpark_switchState = this.sharedPreferences?.getBoolean("carpark_switch", false)
        if (carpark_switchState != null) {
            carpark_switch.isChecked = carpark_switchState
        }


        // Create a single listener for all the switches and set it as the listener for each switch
        val switchListener = SwitchListener(sharedPreferences!!, requireContext(),
            fragmentBlankBinding!!
        )
        mirror_switch.setOnCheckedChangeListener(switchListener)
        carpark_switch.setOnCheckedChangeListener(switchListener)
        startstop_switch.setOnCheckedChangeListener(switchListener)


        return binding.root
    }

    class SwitchListener(
        private val sharedPreferences: SharedPreferences,
        private val context: Context,
        private val fragmentBlankBinding: ComputerDisplayBinding
    ) :
        CompoundButton.OnCheckedChangeListener {
        private var showDialog = true
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            // Get the ID of the switch that was changed
            val switchId = buttonView.id

            // Save the state of the switch in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putBoolean("$switchId", isChecked)
            editor.apply()
            if (showDialog) {
                // Create a dialog builder and set the message and buttons
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.switch_coomputer)
                builder.setPositiveButton(
                    R.string.oui
                ) { dialog, which ->
                    when (switchId)
                    {
                        fragmentBlankBinding.startstopswitch.id->
                        {
                            CoroutineScope(Dispatchers.Default).launch {
                                fragmentBlankBinding.ptcframe.text=String.format("OBD Process:")
                                fragmentBlankBinding.ptcframe.append(AlpdroidApplication.app.alpdroidData.set_startstop_switch(context))
                            }
                        }
                        fragmentBlankBinding.mirrorswitch.id->
                        {
                            CoroutineScope(Dispatchers.Default).launch {
                                fragmentBlankBinding.ptcframe.text=String.format("OBD Process:")
                                fragmentBlankBinding.ptcframe.append(AlpdroidApplication.app.alpdroidData.set_mirror_switch(context))
                            }
                        }

                        fragmentBlankBinding.carparkswitch.id->
                        {
                            CoroutineScope(Dispatchers.Default).launch {
                                fragmentBlankBinding.ptcframe.text=String.format("OBD Process:")
                                fragmentBlankBinding.ptcframe.append(AlpdroidApplication.app.alpdroidData.set_carpark_switch(context))
                            }
                        }
                    }
                }
                builder.setNegativeButton(
                    R.string.non
                ) { dialog, which -> // If the user clicked "No", reset the switch to its previous state
                    showDialog = false
                    buttonView.isChecked = !isChecked
                    editor.putBoolean("$switchId", !isChecked)
                    editor.apply()
                    showDialog = true
                }

                // Show the dialog
                val dialog = builder.create()
                dialog.show()
            } else {
                showDialog = true
            }
        }
    }

    private fun obdptclaunch() {
        CoroutineScope(Dispatchers.Default).launch {


       AlpdroidApplication.app.alpdroidData.ask_ptclist()
       ptc_see=true


        }

    }

    private fun obdptcreset() {
        CoroutineScope(Dispatchers.Default).launch {

            AlpdroidApplication.app.alpdroidData.reset_ptclist()
            ptc_see=true

        }

    }



    override fun onPause() {
        super.onPause()

        // Save the state of switch1 in SharedPreferences
        val editor = sharedPreferences!!.edit()
        editor.putBoolean("mirror_switch", mirror_switch.isChecked())
        editor.putBoolean("carpark_switch", carpark_switch.isChecked())
        editor.putBoolean("startstop_switch", startstop_switch.isChecked())
        editor.apply()
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        val editor = sharedPreferences!!.edit()
        editor.putBoolean("mirror_switch", mirror_switch.isChecked())
        editor.putBoolean("carpark_switch", carpark_switch.isChecked())
        editor.putBoolean("startstop_switch", startstop_switch.isChecked())
        editor.apply()

        fragmentBlankBinding = null
        super.onDestroyView()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        ac_header = fragmentBlankBinding!!.acHeader


        framedatadisplay = fragmentBlankBinding!!.ptcframe

        rtxTimer = System.currentTimeMillis()

        framedatadisplay.setMovementMethod(ScrollingMovementMethod())
        framedatadisplay.text=String.format("%s \r\n",getString(R.string.show_stored_diagnostic_trouble_codes))

        timerTask = {
            activity?.runOnUiThread {
                if (AlpdroidApplication.app.isBound) {

                    if (ptc_see) {

                        var ptc2show = String()
                        val ptc2decode:ByteArray

                        if (AlpdroidApplication.app.alpdroidData.get_ptcdtc_ECM()!=null) {

                            ptc2show+= getString(R.string.ecm_defaults)+"\r\n"
                            ptc2decode = AlpdroidApplication.app.alpdroidData.get_ptcdtc_ECM()!!.copyOfRange(1,
                                AlpdroidApplication.app.alpdroidData.get_ptcdtc_ECM()!!.size
                            )

                            var key2build: String
                            var calculatePTCode: String


                            var loop = 0

                            while (loop + 3 < ptc2decode.size)
                            // because ptcdtc begin by status byte
                            {

                                calculatePTCode =
                                    String.format("%X", ((ptc2decode[loop].toInt() and 0x30) shr 4))
                                calculatePTCode += String.format(
                                    "%X",
                                    (ptc2decode[loop].toInt() and 0x0F)
                                )
                                calculatePTCode += String.format(
                                    "%02X",
                                    ptc2decode[loop + 1]
                                )


                                when ((ptc2decode[loop].toUByte().toUInt() shr 6).toInt()) {
                                    0 -> {
                                        key2build = "P"
                                        key2build += calculatePTCode
                                        //                   Log.d("Key2Build P:",key2build)
                                        try {

                                            ptc2show += key2build + "-" +String.format("%02X",ptc2decode[loop + 2])+ " = " + DtcPowertrain.valueOf(
                                                key2build
                                            ).dtc
                                        } catch (ex: Exception) {
                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode[loop + 2]) + " = " + getString(
                                                R.string.this_dtc_code_is_not_documented
                                            )
                                        }
                                    }
                                    1 -> {
                                        key2build = "C"
                                        key2build += calculatePTCode
                                        //                      Log.d("Key2Build C:",key2build)
                                        try {

                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode[loop + 2])+ " = " + DtcChassis.valueOf(
                                                key2build
                                            ).dtc
                                        } catch (ex: Exception) {
                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode[loop + 2])+ " = " +  getString(
                                                R.string.this_dtc_code_is_not_documented
                                            )
                                        }
                                    }
                                    2 -> {
                                        key2build = "B"
                                        key2build += calculatePTCode
                                        //                      Log.d("Key2Build B:",key2build)
                                        try {

                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode[loop + 2])+ " = " + DtcBody.valueOf(
                                                key2build
                                            ).dtc
                                        } catch (ex: Exception) {
                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode[loop + 2])+ " = " + getString(
                                                R.string.this_dtc_code_is_not_documented
                                            )
                                        }
                                    }
                                    3 -> {
                                        key2build = "U"
                                        key2build += calculatePTCode
                                        //                   Log.d("Key2Build U:",key2build)
                                        try {

                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode[loop + 2])+ " = " + DtcNetwork.valueOf(
                                                key2build
                                            ).dtc
                                        } catch (ex: Exception) {
                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode[loop + 2])+ " = " +  getString(
                                                R.string.this_dtc_code_is_not_documented
                                            )
                                        }
                                    }
                                    else -> {
                                        key2build = "X"
                                        key2build += calculatePTCode
                                        ptc2show += key2build + "-" + String.format("%02X",ptc2decode[loop + 2])+ " = " + getString(
                                            R.string.this_is_bad_dtc
                                        )
                                    }
                                }


                                if ((ptc2decode[loop + 3].toUByte().toInt() and 0x02) shr 1 == 1)
                                    ptc2show += " ->"+ getString(R.string.not_confirmed)
                                if ((ptc2decode[loop + 3].toUByte().toInt() and 0x08) shr 3 == 1)
                                    ptc2show +=" ->"+ getString(R.string.confirmed)
                                if ((ptc2decode[loop + 3].toUByte().toInt() and 0x80) shr 7 == 1)
                                    ptc2show +=" "+ getString(R.string.and_light)

                                ptc2show +="\r\n"

                               loop += 4
                            }
                            framedatadisplay.setText(
                                ptc2show
                            )
                        }

                        if (AlpdroidApplication.app.alpdroidData.get_ptcdtc_ETT() != null) {

                            ptc2show+="\r\n"+ getString(R.string.defauts_ecu_entretien)+" :\r\n"
                            val ptc2decode2 =
                                AlpdroidApplication.app.alpdroidData.get_ptcdtc_ETT()!!.copyOfRange(
                                    1,
                                    AlpdroidApplication.app.alpdroidData.get_ptcdtc_ETT()!!.size
                                )

                            var key2build: String
                            var calculatePTCode: String


                            var loop = 0

                            while (loop + 3 < ptc2decode2.size)
                            // because ptcdtc begin by status byte
                            {

                               calculatePTCode = String.format(
                                    "%X",
                                    ((ptc2decode2[loop].toInt() and 0x30) shr 4)
                                )
                                calculatePTCode += String.format(
                                    "%X",
                                    (ptc2decode2[loop].toInt() and 0x0F)
                                )
                                calculatePTCode += String.format(
                                    "%02X",
                                    (ptc2decode2[loop + 1])
                                )


                                when ((ptc2decode2[loop].toUByte().toUInt() shr 6).toInt()) {
                                    0 -> {
                                        key2build = "P"
                                        key2build += calculatePTCode
                                        //                   Log.d("Key2Build P:",key2build)
                                        try {

                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + DtcPowertrain.valueOf(
                                                key2build
                                            ).dtc
                                        } catch (ex: Exception) {
                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + getString(R.string.this_dtc_code_is_not_documented)
                                        }
                                    }
                                    1 -> {
                                        key2build = "C"
                                        key2build += calculatePTCode
                                        //                      Log.d("Key2Build C:",key2build)
                                        try {

                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + DtcChassis.valueOf(
                                                key2build
                                            ).dtc + "-" + ptc2decode2[loop + 2]
                                        } catch (ex: Exception) {
                                            ptc2show +=  key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + getString(R.string.this_dtc_code_is_not_documented)
                                        }
                                    }
                                    2 -> {
                                        key2build = "B"
                                        key2build += calculatePTCode
                                        //                      Log.d("Key2Build B:",key2build)
                                        try {

                                            ptc2show += key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + DtcBody.valueOf(
                                                key2build
                                            ).dtc
                                        } catch (ex: Exception) {
                                            ptc2show +=  key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + getString(R.string.this_dtc_code_is_not_documented)
                                        }
                                    }
                                    3 -> {
                                        key2build = "U"
                                        key2build += calculatePTCode

                                        try {

                                            ptc2show +=  key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + DtcNetwork.valueOf(
                                                key2build
                                            ).dtc
                                        } catch (ex: Exception) {
                                            ptc2show +=  key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + getString(R.string.this_dtc_code_is_not_documented)
                                        }
                                    }
                                    else -> {
                                        key2build = "X"
                                        key2build += calculatePTCode
                                        ptc2show +=  key2build + "-" + String.format("%02X",ptc2decode2[loop + 2])+ " = " + getString(R.string.this_is_bad_dtc)
                                    }
                                }


                                if ((ptc2decode2[loop + 3].toUByte().toInt() and 0x02) shr 1 == 1)
                                    ptc2show +=" ->" +getString(R.string.not_confirmed)
                                if ((ptc2decode2[loop + 3].toUByte().toInt() and 0x08) shr 3 == 1)
                                    ptc2show +=" ->" +getString(R.string.confirmed)
                                if ((ptc2decode2[loop + 3].toUByte().toInt() and 0x80) shr 7 == 1)
                                    ptc2show +=" "+ getString(R.string.and_light)

                                ptc2show +="\r\n"

                                loop += 4
                            }
                            framedatadisplay.setText(
                                ptc2show
                            )
                        }
                    }

                }

            }
        }
    }
}