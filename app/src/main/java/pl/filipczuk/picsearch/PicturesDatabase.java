package pl.filipczuk.picsearch;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import pl.filipczuk.picsearch.database.PictureDao;
import pl.filipczuk.picsearch.model.Picture;

@Database(entities = {Picture.class}, version = 3)
public abstract class PicturesDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = BuildConfig.DATABASE_NAME;

    public abstract PictureDao pictureDao();

    public static volatile PicturesDatabase INSTANCE;

    public static PicturesDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PicturesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, PicturesDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(callback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static Callback callback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsyn(INSTANCE);
        }
    };

    static class PopulateDbAsyn extends AsyncTask<Void, Void, Void> {
        private PictureDao pictureDao;

        public PopulateDbAsyn(PicturesDatabase picturesDatabase) {
            pictureDao = picturesDatabase.pictureDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            pictureDao.deleteAll();
            return null;
        }
    }
}
