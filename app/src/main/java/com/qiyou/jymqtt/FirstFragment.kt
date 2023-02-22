package com.qiyou.jymqtt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qiyou.jymqtt.databinding.FragmentFirstBinding
import com.qiyou.mqtt.mqttv5.IPublishActionListener
import com.qiyou.mqtt.mqttv5.MqttV5.QOS_REPEAT
import org.eclipse.paho.mqttv5.client.IMqttToken

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            context?.let { it1 -> MqttUtils.connect(it1) }
        }

        binding.buttonDisconnect.setOnClickListener {
            MqttUtils.disConnect()
        }

        binding.buttonSubscribe.setOnClickListener {
            MqttUtils.mqttV5?.subscribe("testtopic/test", null)
        }

        binding.buttonUnsubscribe.setOnClickListener {
            MqttUtils.mqttV5?.unSubscribe("testtopic/test")
        }

        binding.buttonSend.setOnClickListener {
            MqttUtils.mqttV5?.publishMsg("testtopic/test", QOS_REPEAT, true, "woshipuyang", object : IPublishActionListener {
                override fun onSuccess(p0: IMqttToken?) {
                    Log.d("buttonSend", "send onSuccess")
                }

                override fun onFailure(p0: IMqttToken?, p1: Throwable?) {
                }

                override fun onConnectionLost() {
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}