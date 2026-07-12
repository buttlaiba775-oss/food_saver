package com.example.food_saver.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_saver.R;

import java.util.List;

public class NgoApprovalAdapter extends RecyclerView.Adapter<NgoApprovalAdapter.NgoViewHolder> {

    private Context context;
    private List<NgoRequest> ngoList;

    public NgoApprovalAdapter(Context context, List<NgoRequest> ngoList) {
        this.context = context;
        this.ngoList = ngoList;
    }

    @NonNull
    @Override
    public NgoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ngo_request, parent, false);
        return new NgoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NgoViewHolder holder, int position) {
        NgoRequest ngo = ngoList.get(position);

        holder.tvNgoName.setText(ngo.getName());
        holder.tvNgoEmail.setText(ngo.getEmail());
        holder.tvNgoStatus.setText("Status: " + ngo.getStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NgoRequestDetailActivity.class);
            intent.putExtra("ngoId", ngo.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ngoList.size();
    }

    public static class NgoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNgoName, tvNgoEmail, tvNgoStatus;

        public NgoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNgoName = itemView.findViewById(R.id.tvNgoName);
            tvNgoEmail = itemView.findViewById(R.id.tvNgoEmail);
            tvNgoStatus = itemView.findViewById(R.id.tvNgoStatus);
        }
    }
}