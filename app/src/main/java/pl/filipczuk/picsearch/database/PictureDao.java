package pl.filipczuk.picsearch.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pl.filipczuk.picsearch.model.Picture;

@Dao
public interface PictureDao {

    @Query("SELECT * FROM Picture")
    List<Picture> getPictures();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Picture picture);

    @Delete
    void delete(Picture picture);

    @Query("DELETE FROM Picture")
    void deleteAll();
}
