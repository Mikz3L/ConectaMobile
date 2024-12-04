package com.example.conectamobile;

public class Contact {

    private String id; // Identificador único
    private String name;
    private String telefono;
    private String email;
    private boolean isDeleted;

    public Contact() {} // Constructor vacío requerido por Firebase

    public Contact(String id, String name, String telefono, String email) {
        this.id = id; // Identificador único para cada contacto
        this.name = name;
        this.telefono = telefono;
        this.email = email;
        this.isDeleted = false; // Inicialmente no está eliminado
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
