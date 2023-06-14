package pl.filipczuk.picsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pl.filipczuk.picsearch.model.Picture;
import pl.filipczuk.picsearch.ui.view.GalleryViewModel;
import pl.filipczuk.picsearch.ui.view.ListItemClickListener;
import pl.filipczuk.picsearch.ui.view.PexelsAdapter;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements ListItemClickListener/*, LocationListener*/ {

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
//    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION};
//    final static int PERMISSIONS_ALL = 1;
//    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        requestPermissions(PERMISSIONS, PERMISSIONS_ALL);
//        requestLocation();

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

    //    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        try {
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String locality = addresses.get(0).getLocality();
//            String countryName = addresses.get(0).getCountryName();
//            System.out.println(countryName);
//            System.out.println(countryName);
//            System.out.println(countryName);
//            System.out.println(countryName);
//            searchQueryEditText.setText(countryName);
//            searchPexels();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        locationManager.removeUpdates(this);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            requestLocation();
//        }
//    }

//    public void requestLocation() {
//        if (locationManager == null) {
//            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        }
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, this);
//            }
//        }
//    }

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