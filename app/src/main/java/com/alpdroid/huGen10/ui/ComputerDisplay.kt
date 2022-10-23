package com.alpdroid.huGen10.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.alpdroid.huGen10.databinding.ComputerDisplayBinding
import com.alpdroid.huGen10.ui.MainActivity.application


@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ComputerDisplay : UIFragment(250) {

    private  var fragmentBlankBinding: ComputerDisplayBinding?=null
    lateinit var ac_header : TextView
    lateinit var canframeText: TextView
    lateinit var canid: EditText
    lateinit var arduinostate : TextView
    lateinit var transmitstate : TextView
    lateinit var appState : TextView
    lateinit var trackShow:TextView
    lateinit var trackPrev:TextView
    lateinit var countCluster:TextView

    var framestring1 : String=""
    var framestring2 : String=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val binding = ComputerDisplayBinding.inflate(inflater, container, false)
        fragmentBlankBinding = binding
        return binding.root
    }
    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentBlankBinding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        canframeText = fragmentBlankBinding!!.canFrameView
        canid = fragmentBlankBinding!!.idcanframe

        ac_header = fragmentBlankBinding!!.acHeader
        arduinostate = fragmentBlankBinding!!.arduinoState
        transmitstate = fragmentBlankBinding!!.transmitState
        appState = fragmentBlankBinding!!.appstate
        trackShow = fragmentBlankBinding!!.showtrack
        trackPrev = fragmentBlankBinding!!.showprev
        countCluster = fragmentBlankBinding!!.countcluster

        timerTask = {
                activity?.runOnUiThread {
                    if (application.isBound) {
                        if (application.alpdroidServices.isServiceStarted)
                            ac_header.text = "Service Is Working"
                        else ac_header.text = "Service Stopping for some weird reason"

                        trackShow.text= application.alpdroidServices.alpine2Cluster.trackName
                        trackPrev.text= application.alpdroidServices.alpine2Cluster.prevtrackName
                        countCluster.text= application.alpdroidServices.alpine2Cluster.frameFlowTurn.toString()

                     if (application.alpdroidServices.isArduinoWorking())
                            arduinostate.text = "Arduino Is Working"
                        else arduinostate.text = "Arduino Disconnected"

                        if (application.alpineCanFrame.isFrametoSend() == true)
                            transmitstate.text = ".....Ok sending"
                        else transmitstate.text = "......No Frame to send"

                        framestring1= canid.text.toString()
                        if (framestring1.isNotEmpty()) {
                            framestring2=application.alpineCanFrame.getFrame(framestring1.toInt(16))
                                .toString()

                            canframeText.text =framestring2
                        }

                    }
                    else
                        appState.text="Application is not bound"
                }
            }
    }

}