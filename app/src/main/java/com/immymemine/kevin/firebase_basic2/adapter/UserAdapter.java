package com.immymemine.kevin.firebase_basic2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.immymemine.kevin.firebase_basic2.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quf93 on 2017-10-31.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder>{
    Callback callback;
    List<User> data = new ArrayList<>();
    public UserAdapter(Callback callback) {
        this.callback = callback;
    }

    public void setDataAndRefresh(List<User> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        User user = data.get(position);
        holder.tv_id.setText( user.getId() );
        holder.token = user.getToken();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tv_id;
        String token;
        public Holder(View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(android.R.id.text1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = tv_id.getText().toString();
                    if(id != null && token != null)
                        callback.setIdAndToken(tv_id.getText().toString(), token);
                }
            });
        }
    }

    public interface Callback {
        void setIdAndToken(String id, String token);
    }
}
