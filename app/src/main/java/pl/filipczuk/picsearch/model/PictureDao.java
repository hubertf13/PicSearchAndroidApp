package pl.filipczuk.picsearch.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

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
