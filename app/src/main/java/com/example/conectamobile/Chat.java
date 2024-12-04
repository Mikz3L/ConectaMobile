package com.example.conectamobile;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class Chat extends AppCompatActivity implements MQTTHelper.MessageListener {

    private MQTTHelper mqttHelper;
    private EditText topicInput;
    private EditText messageInput;
    private LinearLayout messageContainer;
    private ScrollView scrollView;
    private String currentTopic;
    private String clientId; // Identificador único para este dispositivo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        topicInput = findViewById(R.id.topicInput);
        Button connectButton = findViewById(R.id.connectButton);
        Button sendButton = findViewById(R.id.buttonSend);
        messageInput = findViewById(R.id.editTextMessage);
        messageContainer = findViewById(R.id.messageContainer);
        scrollView = findViewById(R.id.scrollView);

        clientId = UUID.randomUUID().toString(); // Generar un identificador único
        mqttHelper = new MQTTHelper(this);

        connectButton.setOnClickListener(view -> {
            String topic = topicInput.getText().toString().trim();
            if (!topic.isEmpty()) {
                currentTopic = topic;
                mqttHelper.connectAndSubscribe(currentTopic);
                Toast.makeText(Chat.this, "Conectado al tópico: " + currentTopic, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Chat.this, "Por favor, ingresa un tópico", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (currentTopic != null && !message.isEmpty()) {
                String formattedMessage = clientId + ":" + message; // Añadir clientId al mensaje
                mqttHelper.sendMessage(currentTopic, formattedMessage);
                addMessage(message, true); // Mostrar el mensaje enviado a la derecha
                messageInput.setText(""); // Limpiar el campo de texto
            } else {
                Toast.makeText(Chat.this, "Conéctate a un tópico y escribe un mensaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMessageReceived(String topic, String message) {
        runOnUiThread(() -> {
            if (!message.startsWith(clientId + ":")) { // Ignorar mensajes propios
                String cleanMessage = message.contains(":") ? message.split(":", 2)[1] : message; // Extraer mensaje
                addMessage(cleanMessage, false); // Mostrar el mensaje recibido a la izquierda
            }
        });
    }

    private void addMessage(String message, boolean isSent) {
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setPadding(16, 8, 16, 8);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);

        if (isSent) {
            params.gravity = Gravity.END; // Alinear a la derecha
            messageView.setBackgroundResource(R.drawable.sent_message_bg);
        } else {
            params.gravity = Gravity.START; // Alinear a la izquierda
            messageView.setBackgroundResource(R.drawable.received_message_bg);
        }

        messageView.setLayoutParams(params);
        messageContainer.addView(messageView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.disconnect();
    }
}
