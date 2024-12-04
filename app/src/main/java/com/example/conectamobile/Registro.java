package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private Button btRegistro, btVolverLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar vistas
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        btRegistro = findViewById(R.id.btRegistro);
        btVolverLogin = findViewById(R.id.btVolverLogin);

        // Inicializar Firebase Auth y referencia a Firebase Realtime Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");

        // Botón "Registrarse"
        btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextName.getText().toString().trim();
                String correo = editTextEmail.getText().toString().trim();
                String contraseña = editTextPassword.getText().toString().trim();
                String telefono = editTextPhone.getText().toString().trim();

                // Validar campos
                if (nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty() || telefono.isEmpty()) {
                    Toast.makeText(Registro.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    Toast.makeText(Registro.this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (contraseña.length() < 6) {
                    Toast.makeText(Registro.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Registrar usuario en Firebase Authentication
                mAuth.createUserWithEmailAndPassword(correo, contraseña)
                        .addOnCompleteListener(Registro.this, task -> {
                            if (task.isSuccessful()) {
                                // Si el registro es exitoso, obtener el usuario y guardar en la base de datos
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid(); // Obtener UID del usuario
                                    Usuario usuario = new Usuario(nombre, correo, telefono);

                                    // Guardar datos del usuario en Firebase Realtime Database
                                    databaseReference.child(userId).setValue(usuario)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(Registro.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                                    // Redirigir al login
                                                    Intent intent = new Intent(Registro.this, Login.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(Registro.this, "Error al guardar los datos en Firebase", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // Si el registro falla
                                Toast.makeText(Registro.this, "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Botón "Volver al Login"
        btVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Registro.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    // Clase Usuario para representar los datos
    public static class Usuario {
        public String nombre;
        public String correo;
        public String telefono;

        public Usuario() {
            // Constructor vacío necesario para Firebase
        }

        public Usuario(String nombre, String correo, String telefono) {
            this.nombre = nombre;
            this.correo = correo;
            this.telefono = telefono;
        }
    }
}
