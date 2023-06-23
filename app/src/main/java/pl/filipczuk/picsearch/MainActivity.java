package pl.filipczuk.picsearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import pl.filipczuk.picsearch.model.Picture;
import pl.filipczuk.picsearch.ui.view.GalleryViewModel;
import pl.filipczuk.picsearch.ui.view.ListItemClickListener;
import pl.filipczuk.picsearch.ui.view.PexelsAdapter;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements ListItemClickListener {

    private EditText searchQueryEditText;
    private Button searchButton;
    private Button nextPageButton;
    private Button prevPageButton;
    private ProgressBar progressBar;
    private List<Picture> pictures;
    private RecyclerView recyclerView;
    private PexelsAdapter pexelsAdapter;
    private GalleryViewModel viewModel;
    private Integer page;
    private final int FINE_PERMISSION_CODE = 1;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        new LocationAsyncTask().execute();

        getComponents();
        setOnButtonsClick();
        setAdapterToLayout();

        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        viewModel.getPictures().observe(this, pexelsPictures -> {
            pictures = pexelsPictures;
            pexelsAdapter.setPictures(pexelsPictures);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (pictures.size() == 0) {
                Toast.makeText(getApplicationContext(), "No results for this search", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class LocationAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getLastLocation();
            return null;
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    try {
                        currentLocation = location;
                        double latitude = currentLocation.getLatitude();
                        double longitude = currentLocation.getLongitude();

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String countryName = addresses.get(0).getCountryName();
                        String locality = addresses.get(0).getLocality();
                        if (locality != null) {
                            searchQueryEditText.setText(locality);
                        } else {
                            searchQueryEditText.setText(countryName);
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        task.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                page = 1;
                searchPexels();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefers", MODE_PRIVATE).edit();
        editor.putString("query", searchQueryEditText.getText().toString());
        editor.putInt("page", page);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("MyPrefers", MODE_PRIVATE);
        String query = prefs.getString("query", "");
        int pageNumber = prefs.getInt("page", 1);

        searchQueryEditText.setText(query);
        page = pageNumber;
    }

    private void setAdapterToLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        pexelsAdapter = new PexelsAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pexelsAdapter);
    }

    private void getComponents() {
        searchQueryEditText = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        nextPageButton = findViewById(R.id.next_page_button);
        prevPageButton = findViewById(R.id.prev_page_button);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.pictures_list);
    }

    private void setOnButtonsClick() {
        if (page == null) {
            page = 1;
        }
        searchButton.setOnClickListener(view -> {
            page = 1;
            searchPexels();
        });

        nextPageButton.setOnClickListener(view -> {
            page = page + 1;
            searchPexels();
        });

        prevPageButton.setOnClickListener(view -> {
            if (page > 1) {
                page = page - 1;
                searchPexels();
            } else {
                Toast.makeText(getApplicationContext(), "It's the first page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPexels() {
        String pexelsSearchQuery = searchQueryEditText.getText().toString();
        Context context = getApplicationContext();

        if (pexelsSearchQuery.equals("")) {
            Toast.makeText(context, "Enter something to search", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.searchPictures(pexelsSearchQuery, page);
            recyclerView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onListItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
        intent.putExtra("selectedPicture", pictures.get(position));

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.favouritesMenu) {
            Intent intent = new Intent(getApplicationContext(), FavouritesActivity.class);

            startActivity(intent);
        } else if (id == R.id.locationPicturesMenu) {
            getLastLocation();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", searchQueryEditText.getText().toString());
        outState.putInt("page", page);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String query = savedInstanceState.getString("query");
        int pageNumber = savedInstanceState.getInt("page");

        searchQueryEditText.setText(query);
        page = pageNumber;
    }
}