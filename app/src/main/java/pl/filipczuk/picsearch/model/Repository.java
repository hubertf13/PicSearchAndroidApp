package pl.filipczuk.picsearch.model;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.filipczuk.picsearch.BuildConfig;
import pl.filipczuk.picsearch.api.PexelsApi;
import pl.filipczuk.picsearch.api.PexelsResponse;
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
//        PexelsResponse response;
//        try {
            pexelsApi.searchPictures(query, page, perPage).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<PexelsResponse> call, Response<PexelsResponse> response) {
                    PexelsResponse pexelsResponse = response.body();
                    List<Picture> pictures = pexelsResponse.getPhotos();
                    liveDataPictures.postValue(pictures);
                }

                @Override
                public void onFailure(Call<PexelsResponse> call, Throwable t) {

                }
            });

//            Response<PexelsResponse> execute = pexelsApi.searchPictures(query, page, perPage).execute();
//            response = execute.body();
//            System.out.println(execute);
//
//            if (response != null) {
//                List<Picture> pictures = response.getPhotos();
//                liveDataPictures.postValue(pictures);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
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
