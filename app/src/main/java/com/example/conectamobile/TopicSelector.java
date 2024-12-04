package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TopicSelector extends AppCompatActivity {

    private EditText editTextTopic;
    private Button buttonStartChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_selector);

        editTextTopic = findViewById(R.id.editTextTopic);
        buttonStartChat = findViewById(R.id.buttonStartChat);

        buttonStartChat.setOnClickListener(v -> {
            String topic = editTextTopic.getText().toString().trim();
            if (!topic.isEmpty()) {
                Intent chatIntent = new Intent(TopicSelector.this, Chat.class);
                chatIntent.putExtra("topic", topic); // Enviar el tópico al Chat
                startActivity(chatIntent);
            } else {
                Toast.makeText(this, "Por favor, ingresa un tópico", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
