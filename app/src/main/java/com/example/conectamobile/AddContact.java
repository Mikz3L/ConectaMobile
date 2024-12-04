package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddContact extends AppCompatActivity {

    private EditText etName, etPhone, etEmail;
    private Button btnSaveContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Inicializar vistas
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btnSaveContact = findViewById(R.id.btnSaveContact);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            // Regresar a la actividad anterior (ListaContacto)
            Intent intent = new Intent(AddContact.this, ListaContacto.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });

        // Configurar el botón para guardar el contacto
        btnSaveContact.setOnClickListener(v -> {
            // Obtener los datos de los campos
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            // Validar que los campos no estén vacíos
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
                Toast.makeText(AddContact.this, "Todos los campos deben ser completados", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar formato de correo electrónico
            if (!isValidEmail(email)) {
                Toast.makeText(AddContact.this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar formato de teléfono
            if (!isValidPhoneNumber(phone)) {
                Toast.makeText(AddContact.this, "Número de teléfono inválido", Toast.LENGTH_SHORT).show();
                return;
            }


            // Obtener el usuario autenticado
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();

                // Crear un objeto Contacto con los datos válidos
                Contact contacto = new Contact(name, phone, email);

                // Guardar los datos en Firebase Database
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("Usuarios").child(userId).child("contacts").push().setValue(contacto)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddContact.this, "Contacto guardado correctamente", Toast.LENGTH_SHORT).show();
                            clearFields();

                            // Redirigir a la actividad ListaContacto
                            Intent intent = new Intent(AddContact.this, ListaContacto.class);
                            startActivity(intent);
                            finish(); // Finaliza la actividad actual para que el usuario no pueda regresar a ella con el botón "Atrás"
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddContact.this, "Error al guardar el contacto", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    // Método para validar el correo electrónico
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Método para validar el número de teléfono
    private boolean isValidPhoneNumber(String phone) {
        // Ajusta esta expresión regular según el formato de teléfono que necesites
        return phone.matches("\\+?\\d{1,4}?\\d{7,15}");
    }

    // Método para limpiar los campos
    private void clearFields() {
        etName.setText("");
        etPhone.setText("");
        etEmail.setText("");
    }

    // Clase para representar un contacto
    public static class Contact {
        public String nombre;
        public String telefono;
        public String correo;

        // Constructor vacío necesario para Firebase
        public Contact() {}

        public Contact(String nombre, String telefono, String correo) {
            this.nombre = nombre;
            this.telefono = telefono;
            this.correo = correo;
        }
    }

}
