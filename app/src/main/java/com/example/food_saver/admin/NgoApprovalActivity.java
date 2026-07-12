package com.example.food_saver.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_saver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NgoApprovalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NgoApprovalAdapter adapter;
    private List<NgoRequest> ngoList;
    private LinearLayout emptyStateLayout;
    private TextView tvEmptySubtext;
    private Button btnTabPending, btnTabApproved, btnTabRejected;
    private String currentFilter = "pending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_approval);

        recyclerView = findViewById(R.id.recyclerViewNgoRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvEmptySubtext = findViewById(R.id.tvEmptySubtext);

        btnTabPending = findViewById(R.id.btnTabPending);
        btnTabApproved = findViewById(R.id.btnTabApproved);
        btnTabRejected = findViewById(R.id.btnTabRejected);

        ngoList = new ArrayList<>();
        adapter = new NgoApprovalAdapter(this, ngoList);
        recyclerView.setAdapter(adapter);

        btnTabPending.setOnClickListener(v -> switchTab("pending"));
        btnTabApproved.setOnClickListener(v -> switchTab("approved"));
        btnTabRejected.setOnClickListener(v -> switchTab("rejected"));

        loadNgoRequests();
    }

    private void switchTab(String filter) {
        currentFilter = filter;

        btnTabPending.setBackgroundTintList(getColorStateList(filter.equals("pending") ? R.color.brown_primary : R.color.white));
        btnTabPending.setTextColor(getColor(filter.equals("pending") ? R.color.white : R.color.brown_primary));

        btnTabApproved.setBackgroundTintList(getColorStateList(filter.equals("approved") ? R.color.brown_primary : R.color.white));
        btnTabApproved.setTextColor(getColor(filter.equals("approved") ? R.color.white : R.color.brown_primary));

        btnTabRejected.setBackgroundTintList(getColorStateList(filter.equals("rejected") ? R.color.brown_primary : R.color.white));
        btnTabRejected.setTextColor(getColor(filter.equals("rejected") ? R.color.white : R.color.brown_primary));

        loadNgoRequests();
    }

    private void loadNgoRequests() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ngoList.clear();
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            String role = userSnap.child("role").getValue(String.class);
                            String status = userSnap.child("status").getValue(String.class);

                            if ("NGO".equals(role) && currentFilter.equals(status)) {
                                String id = userSnap.getKey();
                                String name = userSnap.child("name").getValue(String.class);
                                String email = userSnap.child("email").getValue(String.class);

                                NgoRequest ngo = new NgoRequest(id, name, email, status);
                                ngoList.add(ngo);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        android.widget.Toast.makeText(NgoApprovalActivity.this, "Error: " + error.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateEmptyState() {
        if (ngoList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            tvEmptySubtext.setText("New NGO requests will appear here");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}