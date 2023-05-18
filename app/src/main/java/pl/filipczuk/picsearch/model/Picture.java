package pl.filipczuk.picsearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Picture implements Parcelable {
    @PrimaryKey
    private Integer id;
    private String photographer;
    @Embedded
    private PictureSource src;

    public Picture(Integer id, String photographer, PictureSource src) {
        this.id = id;
        this.photographer = photographer;
        this.src = src;
    }

    protected Picture(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        photographer = in.readString();
        src = in.readParcelable(PictureSource.class.getClassLoader());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public PictureSource getSrc() {
        return src;
    }

    public void setSrc(PictureSource src) {
        this.src = src;
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(photographer);
        parcel.writeParcelable(src, i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return Objects.equals(id, picture.id) && Objects.equals(photographer, picture.photographer) && Objects.equals(src, picture.src);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, photographer, src);
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", photographer='" + photographer + '\'' +
                ", src=" + src +
                '}';
    }
}
