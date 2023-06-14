package pl.filipczuk.picsearch.database;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.filipczuk.picsearch.BuildConfig;
import pl.filipczuk.picsearch.api.PexelsApi;
import pl.filipczuk.picsearch.api.PexelsResponse;
import pl.filipczuk.picsearch.model.Picture;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class Repository {
    private final PexelsApi pexelsApi;
    private MutableLiveData<List<Picture>> liveDataPictures = new MutableLiveData<>();
    private MutableLiveData<List<Picture>> liveDataFavPictures = new MutableLiveData<>();
    private Thread searchThread;
    private Thread favsThread;
    private int perPage = BuildConfig.perPage;
    private PictureDao pictureDao;

    @Inject
    public Repository(PexelsApi pexelsApi, PictureDao pictureDao) {
        this.pexelsApi = pexelsApi;
        this.pictureDao = pictureDao;
    }

    public MutableLiveData<List<Picture>> getLiveDataPictures() {
        return liveDataPictures;
    }

    public MutableLiveData<List<Picture>> getLiveDataFavPictures() {
        return liveDataFavPictures;
    }

    public void searchPictures(String query, int page) {
        if (searchThread != null) {
            searchThread.interrupt();
        }

        searchThread = new Thread(() -> queryPexelsSearchApi(query, page));
        searchThread.start();
    }



    private void queryPexelsSearchApi(String query, int page) {
            pexelsApi.searchPictures(query, page, perPage).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<PexelsResponse> call, @NonNull Response<PexelsResponse> response) {
                    System.out.println(response);
                    PexelsResponse pexelsResponse = response.body();
                    if (pexelsResponse != null) {
                        List<Picture> pictures = pexelsResponse.getPhotos();
                        liveDataPictures.postValue(pictures);
                    } else {
                        onFailure(call, new Throwable("PexelsResponse is null"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PexelsResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
    }

    public void insert(Picture picture) {
        new InsertAsyncTask(pictureDao).execute(picture);
    }

    public void delete(Picture picture) {
        new DeleteAsyncTask(pictureDao).execute(picture);
    }

    public void searchForFavsPics() {
        if (favsThread != null) {
            favsThread.interrupt();
        }

        favsThread = new Thread(this::getDatabasePictures);
        favsThread.start();
    }

    private void getDatabasePictures() {
        List<Picture> dbPictures = pictureDao.getPictures();
        liveDataFavPictures.postValue(dbPictures);
    }

    private static class InsertAsyncTask extends AsyncTask<Picture, Void, Void> {
        private PictureDao pictureDao;

        public InsertAsyncTask(PictureDao pictureDao) {
            this.pictureDao = pictureDao;
        }

        @Override
        protected Void doInBackground(Picture... lists) {
            pictureDao.insert(lists[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Picture, Void, Void> {
        private PictureDao pictureDao;

        public DeleteAsyncTask(PictureDao pictureDao) {
            this.pictureDao = pictureDao;
        }

        @Override
        protected Void doInBackground(Picture... lists) {
            pictureDao.delete(lists[0]);
            return null;
        }
    }
}
