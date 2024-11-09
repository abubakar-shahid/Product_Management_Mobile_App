package com.example.navigation_smd_7a;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {
    Context context;
    int resource;
    ProductUpdateListener updateListener;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> objects,
            ProductUpdateListener listener) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.updateListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView tvTitle = v.findViewById(R.id.tvProductTitle);
        ImageView ivEdit = v.findViewById(R.id.ivEdit);
        ImageView ivDelete = v.findViewById(R.id.ivDelete);

        Product p = getItem(position);
        tvTitle.setText(p.getPrice() + " : " + p.getTitle());

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create popup menu
                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.status_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ProductDB db = new ProductDB(context);
                        db.open();

                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_scheduled) {
                            db.updateStatus(p.getId(), "scheduled");
                            Toast.makeText(context, "Moved to Scheduled", Toast.LENGTH_SHORT).show();
                        } else if (itemId == R.id.menu_delivered) {
                            db.updateStatus(p.getId(), "delivered");
                            Toast.makeText(context, "Moved to Delivered", Toast.LENGTH_SHORT).show();
                        }

                        db.close();
                        remove(p);
                        notifyDataSetChanged();

                        // Notify fragment to refresh
                        if (updateListener != null) {
                            updateListener.onProductUpdated();
                        }

                        // Notify MainActivity to refresh all fragments
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).refreshAllFragments();
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductDB db = new ProductDB(context);
                db.open();
                db.remove(p.getId());
                db.close();
                remove(p);
                notifyDataSetChanged();
            }
        });

        return v;
    }
}
