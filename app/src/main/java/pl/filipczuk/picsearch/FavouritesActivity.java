package pl.filipczuk.picsearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pl.filipczuk.picsearch.model.ListItemClickListener;
import pl.filipczuk.picsearch.model.Picture;
import pl.filipczuk.picsearch.ui.view.GalleryViewModel;
import pl.filipczuk.picsearch.ui.view.PexelsAdapter;

@AndroidEntryPoint
public class FavouritesActivity extends AppCompatActivity implements ListItemClickListener {
    private RecyclerView recyclerView;
    private PexelsAdapter pexelsAdapter;
    private List<Picture> pictures;
    private GalleryViewModel viewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        recyclerView = findViewById(R.id.pictures_list);
        progressBar = findViewById(R.id.progress_bar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        pexelsAdapter = new PexelsAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pexelsAdapter);

        pexelsAdapter.setPictures(pictures);

        viewModel.getFavPictures().observe(this, pexelsPictures -> {
            pictures = pexelsPictures;
            pexelsAdapter.setPictures(pexelsPictures);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (pictures.size() == 0) {
                Toast.makeText(getApplicationContext(), "No pictures", Toast.LENGTH_SHORT).show();
            }
        });

        searchForFavourite();
    }

    @Override
    public void onListItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), FavouritePictureActivity.class);
        intent.putExtra("selectedPicture", pictures.get(position));

        startActivity(intent);
    }

    private void searchForFavourite() {
        viewModel.getDatabasePics();
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
}
