package com.example.conectamobile;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTHelper {

    private MqttClient client;
    private final String brokerUrl = "tcp://mqtt.eclipseprojects.io:1883"; // Cambia el broker si es necesario
    private final MessageListener messageListener;

    // Constructor que recibe el listener en lugar del contexto
    public MQTTHelper(MessageListener listener) {
        this.messageListener = listener;
        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Conectar y suscribirse a un topic específico
    public void connectAndSubscribe(String topic) {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);
            client.subscribe(topic);  // Suscripción a un topic único
            client.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String receivedMessage = new String(message.getPayload());
                    Log.d("MQTTHelper", "Mensaje recibido: " + receivedMessage);

                    // Notificar el mensaje recibido al listener
                    if (messageListener != null) {
                        messageListener.onMessageReceived(topic, receivedMessage);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTTHelper", "Conexión perdida: " + cause.getMessage());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTTHelper", "Entrega completada.");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Enviar un mensaje a un topic
    public void sendMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(topic, mqttMessage); // Publicar en el topic del chat
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Desconectar el cliente MQTT
    public void disconnect() {
        try {
            if (client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Interfaz para manejar mensajes
    public interface MessageListener {
        void onMessageReceived(String topic, String message);
    }
}
