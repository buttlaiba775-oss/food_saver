package com.example.food_saver;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<Map<String, Object>> requestList;
    private DatabaseReference mDatabase;

    public RequestAdapter(Context context, List<Map<String, Object>> requestList) {
        this.context = context;
        this.requestList = requestList;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Map<String, Object> request = requestList.get(position);

        String requestId = (String) request.get("requestId");
        String foodName = (String) request.get("foodName");
        String ngoName = (String) request.get("ngoName");
        String status = (String) request.get("status");
        String postId = (String) request.get("postId");

        holder.text1.setText(foodName != null ? foodName : "Food Item");
        holder.text2.setText("Requested by: " + (ngoName != null ? ngoName : "NGO"));

        holder.text1.setTextColor(Color.parseColor("#7A4F37"));
        holder.text2.setTextColor(Color.parseColor("#8A7665"));

        // DYNAMIC BUTTON LOGIC FOR "ACCEPTED" TAB
        if (status != null && status.equalsIgnoreCase("accepted")) {
            holder.btnHandOver.setVisibility(View.VISIBLE);
            holder.btnHandOver.setOnClickListener(v -> {
                if (requestId != null) {
                    // Method typo fixed here (addOnSuccessListener)
                    mDatabase.child("Requests").child(requestId).child("status").setValue("history")
                            .addOnSuccessListener(aVoid -> {

                                // Main Food Post ka status bhi clear ya remove kar sakte hain taake woh dashboard se hat jaye
                                if (postId != null) {
                                    mDatabase.child("FoodPosts").child(postId).removeValue();
                                }

                                Toast.makeText(context, "Donation completed! Food handed over.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            holder.btnHandOver.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        MaterialButton btnHandOver;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);

            // Programmatically creating a beautiful matching button inside the row container
            Context ctx = itemView.getContext();

            // Simplified button instantiation
            btnHandOver = new MaterialButton(ctx);
            btnHandOver.setText("Food Handled Over");

            // Fixed setTextSize syntax (Java uses numbers directly, not 'sp' letters)
            btnHandOver.setTextSize(11);
            btnHandOver.setTextColor(Color.WHITE);
            btnHandOver.setBackgroundColor(Color.parseColor("#7A4F37"));
            btnHandOver.setCornerRadius(16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 4);
            btnHandOver.setLayoutParams(params);

            // Injecting button directly beneath texts if row structure allows it layout-wise
            if (itemView instanceof ViewGroup) {
                ((ViewGroup) itemView).addView(btnHandOver);
            }
        }
    }
}