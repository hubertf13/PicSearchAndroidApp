package pl.filipczuk.picsearch.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pl.filipczuk.picsearch.R;
import pl.filipczuk.picsearch.model.ListItemClickListener;
import pl.filipczuk.picsearch.model.Picture;

public class PexelsAdapter extends RecyclerView.Adapter<PexelsAdapter.PexelsViewHolder> {
    private List<Picture> pictures = new ArrayList<>();
    private final ListItemClickListener listItemClickListener;

    public PexelsAdapter(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    @Override
    public PexelsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.picture, parent, false);

        return new PexelsViewHolder(view, listItemClickListener);
    }

    @Override
    public void onBindViewHolder(PexelsViewHolder holder, int position) {
        holder.bind(pictures.get(position));
    }

    @Override
    public int getItemCount() {
        if (pictures == null)
            return 0;

        return pictures.size();
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
        notifyDataSetChanged();
    }


    public static class PexelsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView pictureImageView;
        private TextView photographerTextView;
        private final ListItemClickListener listItemClickListener;

        public PexelsViewHolder(View view, ListItemClickListener listItemClickListener) {
            super(view);
            this.pictureImageView = view.findViewById(R.id.pictureImageView);
            this.photographerTextView = view.findViewById(R.id.photographerTextView);
            this.listItemClickListener = listItemClickListener;

            view.setOnClickListener(this);
        }

        public void bind(Picture picture){
            String photographer = picture.getPhotographer();
            String src = picture.getSrc().getLarge();

            photographerTextView.setText(photographer);

            bindOrHideTextView(pictureImageView, src);
        }

        private void bindOrHideTextView(ImageView imageView, String data){
            if (data == null){
                imageView.setVisibility(View.GONE);
            }
            else{
                Picasso.get().load(data).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            listItemClickListener.onListItemClick(position);
        }
    }
}
