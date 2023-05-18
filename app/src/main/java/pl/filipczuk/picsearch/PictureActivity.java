package pl.filipczuk.picsearch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import pl.filipczuk.picsearch.model.Picture;
import pl.filipczuk.picsearch.ui.view.GalleryViewModel;

@AndroidEntryPoint
public class PictureActivity extends AppCompatActivity {

    private ImageView image;
    private TextView photographerName;
    private Button button;
    private Integer page;
    private GalleryViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        page = getIntent().getIntExtra("pageNumber", 1);
        image = findViewById(R.id.separate_picture_iv);
        photographerName = findViewById(R.id.pic_photographer_tv);
        button = findViewById(R.id.save_button);
        button.setText("Add to favourite");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Picture picture = getIntent().getParcelableExtra("selectedPicture", Picture.class);

            button.setOnClickListener(view -> {
                viewModel.insertToFavs(picture);
            });

            String photographer = picture.getPhotographer();
            String src = picture.getSrc().getLarge();

            Picasso.get().load(src).into(image);
            photographerName.setText(String.valueOf(photographer));
        }
    }

    @Override
    protected void onPause() {
        getIntent().putExtra("pageNumber", page);

        super.onPause();
    }
}