package com.example.conectamobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Perfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView userName, userEmail;
    private ImageView profileImage;
    private Button backButton;
    private SharedPreferences sharedPreferences;
    private Uri imageUri; // Para almacenar la URI de la imagen seleccionada
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar vistas
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        profileImage = findViewById(R.id.profile_image);
        backButton = findViewById(R.id.back_button);

        // Inicializar Firebase y SharedPreferences
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios");
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Cargar datos del usuario
        loadUserData();

        // Cargar la imagen desde SharedPreferences si existe
        loadImageFromPreferences();

        // Configurar el ImageView para seleccionar imagen al tocar
        profileImage.setOnClickListener(v -> openFileChooser());

        // Configurar el botón de volver
        backButton.setOnClickListener(v -> {
            if (imageUri != null) {
                saveImageToInternalStorage(imageUri);
            }
            navigateToListaContacto();
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void saveImageToInternalStorage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            File file = new File(getFilesDir(), "profile_image.jpg");

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profile_image_path", file.getAbsolutePath());
            editor.apply();
        } catch (IOException e) {
            Toast.makeText(Perfil.this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageFromPreferences() {
        String imagePath = sharedPreferences.getString("profile_image_path", null);
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profileImage.setImageBitmap(bitmap);
            }
        }
    }

    private void navigateToListaContacto() {
        Intent intent = new Intent(Perfil.this, ListaContacto.class);
        startActivity(intent);
        finish();
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("nombre").getValue(String.class);
                    String email = snapshot.child("correo").getValue(String.class);

                    userName.setText(name);
                    userEmail.setText(email);

                    Glide.with(Perfil.this)
                            .load(R.drawable.ic_profile_placeholder)
                            .circleCrop()
                            .into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Perfil.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
