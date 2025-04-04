package com.example.integradora4to.mqtt
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttHelper(serverUri: String, clientId: String) {
    private val mqttClient = MqttClient(serverUri, clientId, MemoryPersistence())

    fun connect(username: String?, password: String?, onConnected: () -> Unit, onError: (Exception) -> Unit) {
        Thread {
            try {
                val options = MqttConnectOptions().apply {
                    isCleanSession = true
                    isAutomaticReconnect = true
                    this.userName = username
                    password?.let { this.password = it.toCharArray() }
                }
                mqttClient.connect(options)
                onConnected()
            } catch (e: MqttException) {
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    onError(e)
                }
            }
        }.start()
    }

    fun publish(topic: String, message: String) {
        try {
            if (mqttClient.isConnected) {
                val mqttMessage = MqttMessage(message.toByteArray()).apply {
                    qos = 1
                }
                mqttClient.publish(topic, mqttMessage)
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            if (mqttClient.isConnected) {
                mqttClient.disconnect()
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

}