package pl.filipczuk.picsearch.ui.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.filipczuk.picsearch.database.Repository;
import pl.filipczuk.picsearch.model.Picture;

@HiltViewModel
public class GalleryViewModel extends ViewModel {
    private Repository repository;
    private LiveData<List<Picture>> pictures;
    private LiveData<List<Picture>> favPictures;
    private final SavedStateHandle savedStateHandle;

    @Inject
    public GalleryViewModel(Repository repository, SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
        this.repository = repository;
        pictures = repository.getLiveDataPictures();
        favPictures = repository.getLiveDataFavPictures();
    }

    public Repository getRepository() {
        return repository;
    }

    public LiveData<List<Picture>> getPictures() {
        return pictures;
    }

    public LiveData<List<Picture>> getFavPictures() {
        return favPictures;
    }

    public void searchPictures(String query, int page) {
        repository.searchPictures(query, page);
    }

    public void insertToFavs(Picture picture) {
        repository.insert(picture);
    }

    public void deleteFromFavs(Picture picture) {
        repository.delete(picture);
    }

    public void getDatabasePics() {
        repository.searchForFavsPics();
    }
}
