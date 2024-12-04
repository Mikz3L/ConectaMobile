package com.example.conectamobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListaContacto extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contacto);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios");

        ImageView profileImage = findViewById(R.id.profileImage);

        // Cargar la imagen de perfil desde SharedPreferences
        loadProfileImage(profileImage);

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewContactos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList);
        recyclerView.setAdapter(contactAdapter);

        // Configurar el botón de cerrar sesión
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        // Configurar el botón para añadir contacto
        findViewById(R.id.fabAddContact).setOnClickListener(v -> addContact());

        // Cargar contactos
        loadContacts();

        // Configurar click en la imagen de perfil
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(ListaContacto.this, Perfil.class);
            startActivity(intent);
        });

        // Configurar el clic en el contacto
        contactAdapter.setOnItemClickListener(contact -> {
            // Obtener el tema del chat basado en el contacto seleccionado
            String currentUser = mAuth.getCurrentUser().getDisplayName();
            String topic = "chat/" + contact.getName() + "/" + currentUser;

            // Iniciar la actividad de Chat con el tema
            Intent intent = new Intent(ListaContacto.this, Chat.class);
            intent.putExtra("TOPIC", topic); // Pasar el tema como extra
            startActivity(intent);
        });
    }

    // Cargar la imagen de perfil desde SharedPreferences
    private void loadProfileImage(ImageView profileImage) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("profile_image_path", null);
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profileImage.setImageBitmap(bitmap); // Establecer la imagen en el ImageView
            }
        }
    }

    // Método para cerrar sesión
    private void logout() {
        // Eliminar la imagen de perfil
        deleteProfileImage();

        // Cierra la sesión del usuario
        mAuth.signOut();
        Intent intent = new Intent(ListaContacto.this, Login.class);
        startActivity(intent);
        finish(); // Cierra la actividad actual
    }

    // Método para eliminar la imagen de perfil
    private void deleteProfileImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("profile_image_path", null);

        if (imagePath != null) {
            File imgFile = new File(imagePath);

            // Elimina el archivo de imagen si existe
            if (imgFile.exists() && imgFile.delete()) {
                Toast.makeText(this, "Imagen de perfil eliminada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se pudo eliminar la imagen de perfil", Toast.LENGTH_SHORT).show();
            }

            // Limpia la información de SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("profile_image_path");
            editor.apply();
        }
    }


    private void loadContacts() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userContactsRef = mDatabase.child(userId).child("contacts");

        userContactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    if (contact != null && !contact.isDeleted()) {
                        contactList.add(contact);
                    }
                }
                Toast.makeText(ListaContacto.this, "Total de Contactos: " + contactList.size(), Toast.LENGTH_SHORT).show();
                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ListaContacto.this, "Error al cargar contactos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addContact() {
        Intent intent = new Intent(ListaContacto.this, AddContact.class);
        startActivity(intent); // Inicia la actividad de agregar contacto
    }
}
