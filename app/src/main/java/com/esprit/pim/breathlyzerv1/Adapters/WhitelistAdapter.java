package com.esprit.pim.breathlyzerv1.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esprit.pim.breathlyzerv1.Entity.Contact;
import com.esprit.pim.breathlyzerv1.R;
import co.dift.ui.SwipeToAction;
import java.util.List;

/**
 * Created by zaineb on 17/01/16.
 */
public class WhitelistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<Contact> list;
    Context c;
    public class contactViewHolder extends SwipeToAction.ViewHolder<Contact> {
        public TextView nameView;
        public TextView numberView;

        public contactViewHolder(View v) {
            super(v);

            nameView = (TextView) v.findViewById(R.id.userName);
            numberView = (TextView) v.findViewById(R.id.userNumber);

        }
    }

   public WhitelistAdapter(Context context, List<Contact> listitem) {
        c = context;
        list = listitem;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_item, parent, false);

        return new contactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Contact item = list.get(position);
        contactViewHolder vh = (contactViewHolder) holder;
        vh.nameView.setText(item.getName());
        vh.numberView.setText(String.valueOf(item.getNumber()));
       // vh.imageView.setImageResource(R.drawable.trash);
        vh.data = item;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub

        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View row = inflater.inflate(R.layout.listview_item, null, false);
//
//        TextView name = (TextView) row.findViewById(R.id.userName);
//        TextView number = (TextView) row.findViewById(R.id.userNumber);
//        Contact cont = list.get(position);
//        name.setText(cont.name);
//        number.setText(cont.number);
//
//        return row;
//
//    }



}
