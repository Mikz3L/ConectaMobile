package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    // Declaración de variables
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);  // Asegúrate de que Firebase esté inicializado
        }

        // Inicializa FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Asigna las vistas
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextNumberPassword);
        loginButton = findViewById(R.id.btiniciarSesion);
        progressBar = findViewById(R.id.progressBar);
        registerTextView = findViewById(R.id.textView3);

        // Configura el botón de login
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Por favor ingresa tus credenciales", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Configura el texto de "Registrarse"
        registerTextView.setOnClickListener(v -> {
            // Redirige a la actividad de registro
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });
    }

    // Método para realizar el login
    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);  // Muestra el progressBar mientras se realiza el login

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);  // Oculta el progressBar después de la operación

                    if (task.isSuccessful()) {
                        // El inicio de sesión fue exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                        // Redirige a la actividad principal
                        Intent intent = new Intent(Login.this, ListaContacto.class);
                        startActivity(intent);
                        finish();  // Cierra la actividad de login
                    } else {
                        // Si el inicio de sesión falla
                        Toast.makeText(Login.this, "Error en el login: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verifica si el usuario ya está logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si ya está logueado, redirige a la actividad principal
            Intent intent = new Intent(Login.this, ListaContacto.class);
            startActivity(intent);
            finish();
        }
    }
}
