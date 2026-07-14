package com.example.food_saver;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodPostAdapter extends RecyclerView.Adapter<FoodPostAdapter.PostViewHolder> {

    private Context context;
    private List<Map<String, Object>> foodPostList;
    private DatabaseReference mDatabase;

    public FoodPostAdapter(Context context, List<Map<String, Object>> foodPostList) {
        this.context = context;
        this.foodPostList = foodPostList;
        this.mDatabase = FirebaseDatabase.getInstance().getReference().child("FoodPosts");
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Map<String, Object> post = foodPostList.get(position);

        String postId = (String) post.get("postId");
        String foodName = (String) post.get("foodName");
        String quantity = (String) post.get("quantity");
        String expiresIn = (String) post.get("expiresIn");
        String description = (String) post.get("description");

        holder.tvFoodTitle.setText(foodName);
        holder.tvQuantity.setText("Servings: " + quantity);

        // DELETE BUTTON CLICK
        holder.ivDeletePost.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this food post?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (postId != null) {
                            mDatabase.child(postId).removeValue()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        holder.ivEditPost.setOnClickListener(v -> {
            showEditDialog(postId, foodName, quantity, expiresIn, description);
        });
    }

    private void showEditDialog(String postId, String currentName, String currentQty, String currentExpiry, String currentDesc) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_post, null);

        AlertDialog editDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        EditText etEditFoodTitle = dialogView.findViewById(R.id.etEditFoodTitle);
        EditText etEditQuantity = dialogView.findViewById(R.id.etEditQuantity);
        EditText etEditExpiryTime = dialogView.findViewById(R.id.etEditExpiryTime);
        EditText etEditDescription = dialogView.findViewById(R.id.etEditDescription);
        MaterialButton btnSaveChanges = dialogView.findViewById(R.id.btnSaveChanges);

        etEditFoodTitle.setText(currentName);
        etEditQuantity.setText(currentQty);
        etEditExpiryTime.setText(currentExpiry);
        etEditDescription.setText(currentDesc);

        btnSaveChanges.setOnClickListener(v -> {
            String updatedName = etEditFoodTitle.getText().toString().trim();
            String updatedQty = etEditQuantity.getText().toString().trim();
            String updatedExpiry = etEditExpiryTime.getText().toString().trim();
            String updatedDesc = etEditDescription.getText().toString().trim();

            if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedQty)) {
                Toast.makeText(context, "Food name and Quantity cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("foodName", updatedName);
            updates.put("quantity", updatedQty);
            updates.put("expiresIn", updatedExpiry);
            updates.put("description", updatedDesc);

            if (postId != null) {
                mDatabase.child(postId).updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Changes saved!", Toast.LENGTH_SHORT).show();
                            editDialog.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        editDialog.show();
    }

    @Override
    public int getItemCount() {
        return foodPostList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodTitle, tvQuantity;
        ImageView ivEditPost, ivDeletePost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodTitle = itemView.findViewById(R.id.tvFoodTitle);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivEditPost = itemView.findViewById(R.id.ivEditPost);
            ivDeletePost = itemView.findViewById(R.id.ivDeletePost);
        }
    }
}
