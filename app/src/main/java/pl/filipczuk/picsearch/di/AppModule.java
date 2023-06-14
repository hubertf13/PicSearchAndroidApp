package pl.filipczuk.picsearch.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import pl.filipczuk.picsearch.PicturesDatabase;
import pl.filipczuk.picsearch.api.PexelsApi;
import pl.filipczuk.picsearch.database.PictureDao;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(PexelsApi.apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public PexelsApi providePexelsApi(Retrofit retrofit) {
        return retrofit.create(PexelsApi.class);
    }

    @Provides
    @Singleton
    public PicturesDatabase provideDatabase(Application application) {
        return PicturesDatabase.getInstance(application.getApplicationContext());
    }

    @Provides
    public PictureDao providePictureDao(PicturesDatabase db) {
        return db.pictureDao();
    }
}
