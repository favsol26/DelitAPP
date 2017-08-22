package com.example.faustino.delitapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
//import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * //Created by faustino on 21/08/17.
 */

class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Context context;
    private List<Item> items;

    ItemAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final Item item = this.items.get(position);
        holder.mDescTextView.setText(item.description);
        holder.mRatingTextView.setText(item.url == null ? "N/A" : context.getString(R.string.shareRequest));
        holder.mEmailTextView.setText(item.email);
        holder.mImageView.setImageBitmap(null);
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference("Images").child(item.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.mImageView);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.url != null) {
                    String shara = (item.url) + "  \n \n " + context.getString(R.string.shareInfo);
                    Toast.makeText(context, R.string.NotShare, Toast.LENGTH_LONG).show();
                    Log.d(ItemAdapter.class.getSimpleName(), shara);
//                    share sh = new share();
//                    sh.sharing(shara);

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, shara);
                    context.startActivity(Intent.createChooser(share, context.getString(R.string.shareTitle)));
                } else {
                    Toast.makeText(context, R.string.shareNotice, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    void addItem(Item item) {
        this.items.add(0, item);
        this.notifyItemInserted(0);
    }

    void updateItem(Item item) {
        int position = this.items.indexOf(item);
        if (position >= 0) {
            this.items.remove(position);
            this.items.add(position, item);
        }
        this.notifyItemChanged(position);
    }

    void removeItem(Item item) {
        int position = this.items.indexOf(item);
        if (position >= 0) {
            this.items.remove(position);
        }
        this.notifyItemRemoved(position);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final TextView mDescTextView;
        private final TextView mRatingTextView;
        private final TextView mEmailTextView;

        ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_image_view);
            mDescTextView = itemView.findViewById(R.id.item_description_text_view);
            mRatingTextView = itemView.findViewById(R.id.item_rating_text_view);
            mEmailTextView = itemView.findViewById(R.id.item_email_text_view);

        }
    }
}

class share extends AppCompatActivity {

//    void sharing(@NonNull final String message) {
////        message = "Text I want to share.";
//        Intent share = new Intent(Intent.ACTION_SEND);
//        share.setType("text/plain");
//        share.putExtra(Intent.EXTRA_TEXT, message);
//        startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
//    }
}
