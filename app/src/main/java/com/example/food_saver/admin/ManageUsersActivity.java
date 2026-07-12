package com.example.food_saver.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<UserItem> userList;
    private LinearLayout emptyStateLayout;
    private Button btnFilterAll, btnFilterDonor, btnFilterNgo, btnFilterRecipient;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterDonor = findViewById(R.id.btnFilterDonor);
        btnFilterNgo = findViewById(R.id.btnFilterNgo);
        btnFilterRecipient = findViewById(R.id.btnFilterRecipient);

        userList = new ArrayList<>();
        adapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        btnFilterAll.setOnClickListener(v -> switchFilter("All"));
        btnFilterDonor.setOnClickListener(v -> switchFilter("Donor"));
        btnFilterNgo.setOnClickListener(v -> switchFilter("NGO"));
        btnFilterRecipient.setOnClickListener(v -> switchFilter("Recipient"));

        loadUsers();
    }

    private void switchFilter(String filter) {
        currentFilter = filter;

        btnFilterAll.setBackgroundTintList(getColorStateList(filter.equals("All") ? R.color.brown_primary : R.color.white));
        btnFilterAll.setTextColor(getColor(filter.equals("All") ? R.color.white : R.color.brown_primary));

        btnFilterDonor.setBackgroundTintList(getColorStateList(filter.equals("Donor") ? R.color.brown_primary : R.color.white));
        btnFilterDonor.setTextColor(getColor(filter.equals("Donor") ? R.color.white : R.color.brown_primary));

        btnFilterNgo.setBackgroundTintList(getColorStateList(filter.equals("NGO") ? R.color.brown_primary : R.color.white));
        btnFilterNgo.setTextColor(getColor(filter.equals("NGO") ? R.color.white : R.color.brown_primary));

        btnFilterRecipient.setBackgroundTintList(getColorStateList(filter.equals("Recipient") ? R.color.brown_primary : R.color.white));
        btnFilterRecipient.setTextColor(getColor(filter.equals("Recipient") ? R.color.white : R.color.brown_primary));

        loadUsers();
    }

    private void loadUsers() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            String role = userSnap.child("role").getValue(String.class);
                            String name = userSnap.child("name").getValue(String.class);
                            String email = userSnap.child("email").getValue(String.class);
                            String id = userSnap.getKey();

                            if (currentFilter.equals("All") || currentFilter.equals(role)) {
                                UserItem user = new UserItem(id, name, email, role);
                                userList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateEmptyState() {
        if (userList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}