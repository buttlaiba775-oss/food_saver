package com.example.food_saver;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class request_screen extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tabPending, tabAccepted, tabHistory;
    private LinearLayout emptyState;
    private RecyclerView recyclerRequests;
    private Button btnFoodHandledOver;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentUserId;

    private RequestAdapter adapter;
    private List<Map<String, Object>> requestList;
    private String currentSelectedTab = "pending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_screen);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        }

        btnBack = findViewById(R.id.btnBack);
        tabPending = findViewById(R.id.tabPending);
        tabAccepted = findViewById(R.id.tabAccepted);
        tabHistory = findViewById(R.id.tabHistory);
        emptyState = findViewById(R.id.emptyState);
        recyclerRequests = findViewById(R.id.recyclerRequests);
        btnFoodHandledOver = findViewById(R.id.btnFoodHandedOver);

        requestList = new ArrayList<>();
        adapter = new RequestAdapter(this, requestList);
        if (recyclerRequests != null) {
            recyclerRequests.setLayoutManager(new LinearLayoutManager(this));
            recyclerRequests.setAdapter(adapter);
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (tabPending != null) tabPending.setOnClickListener(v -> switchTab("pending"));
        if (tabAccepted != null) tabAccepted.setOnClickListener(v -> switchTab("accepted"));
        if (tabHistory != null) tabHistory.setOnClickListener(v -> switchTab("history"));

        if (btnFoodHandledOver != null) {
            btnFoodHandledOver.setVisibility(View.GONE);
            btnFoodHandledOver.setOnClickListener(this::handleFoodHandledOverClick);
        }

        fetchRequestsFromFirebase();
    }

    private void switchTab(String tabType) {
        currentSelectedTab = tabType;
        if (tabType.equals("pending")) {
            setTabActive(tabPending); setTabInactive(tabAccepted); setTabInactive(tabHistory);
            if (btnFoodHandledOver != null) btnFoodHandledOver.setVisibility(View.GONE);
        } else if (tabType.equals("accepted")) {
            setTabInactive(tabPending); setTabActive(tabAccepted); setTabInactive(tabHistory);
            // Yahan button ko "Accepted" tab par hamesha visible kar diya hai
            if (btnFoodHandledOver != null) btnFoodHandledOver.setVisibility(View.VISIBLE);
        } else {
            setTabInactive(tabPending); setTabInactive(tabAccepted); setTabActive(tabHistory);
            if (btnFoodHandledOver != null) btnFoodHandledOver.setVisibility(View.GONE);
        }
        fetchRequestsFromFirebase();
    }

    private void setTabActive(TextView tv) { if (tv != null) { tv.setBackgroundResource(R.drawable.tab_active_background); tv.setTextColor(Color.WHITE); } }
    private void setTabInactive(TextView tv) { if (tv != null) { tv.setBackgroundResource(R.drawable.tab_inactive_background); tv.setTextColor(Color.parseColor("#7A4F37")); } }

    private void fetchRequestsFromFirebase() {
        if (currentUserId == null) return;

        mDatabase.child("Requests").orderByChild("donorId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Map<String, Object> reqMap = (Map<String, Object>) data.getValue();
                                if (reqMap != null) {
                                    reqMap.put("requestId", data.getKey());
                                    String status = (String) reqMap.get("status");
                                    if (status != null && status.equalsIgnoreCase(currentSelectedTab)) requestList.add(reqMap);
                                    else if (currentSelectedTab.equals("accepted") && status != null && status.equalsIgnoreCase("Received_By_NGO")) requestList.add(reqMap);
                                }
                            }
                        }
                        if (emptyState != null) emptyState.setVisibility(requestList.isEmpty() ? View.VISIBLE : View.GONE);
                        if (recyclerRequests != null) recyclerRequests.setVisibility(requestList.isEmpty() ? View.GONE : View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void handleFoodHandledOverClick(View v) {
        if (requestList.isEmpty()) {
            Toast.makeText(this, "No requests to mark as handed over", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> selectedRequest = requestList.get(0);
        String requestId = (String) selectedRequest.get("requestId");
        if (requestId == null) return;
        mDatabase.child("Requests").child(requestId).child("status").setValue("Handled_By_Donor")
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Status Updated!", Toast.LENGTH_SHORT).show());
    }
}