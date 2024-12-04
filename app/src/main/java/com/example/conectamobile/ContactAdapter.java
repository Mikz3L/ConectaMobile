package com.example.conectamobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Context context;
    private OnItemClickListener listener;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactEmail.setText(contact.getEmail());
        holder.contactPhone.setText(contact.getTelefono());

        // Set listener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            // Remove the contact and notify adapter
            contactList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, contactList.size());
        });

        // Set listener for the edit button
        holder.btnEditContact.setOnClickListener(v -> {
            showEditDialog(contact, position);
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView contactName, contactEmail, contactPhone;
        ImageButton deleteButton, btnEditContact; // Declare edit button

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactEmail = itemView.findViewById(R.id.contact_email);
            contactPhone = itemView.findViewById(R.id.contact_phone);
            deleteButton = itemView.findViewById(R.id.btnDeleteContact); // Assuming a delete button is added in the layout
            btnEditContact = itemView.findViewById(R.id.btnEditContact); // Initialize edit button
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(contactList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Contact contact);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Method to show the edit dialog
    private void showEditDialog(Contact contact, int position) {
        // Create an AlertDialog to allow the user to edit the contact's name
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar Contacto");

        // Create an EditText to input the new name
        final EditText input = new EditText(context);
        input.setText(contact.getName());  // Pre-fill with the current name
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = input.getText().toString();
            if (!newName.isEmpty()) {
                contact.setName(newName); // Update the contact's name

                // Save the new name to SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("ContactApp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("contactName_" + contact.getId(), newName); // Store new name with the contact's ID
                editor.apply();

                // Notify the adapter that the item has been updated
                notifyItemChanged(position);
                Toast.makeText(context, "Nombre actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
