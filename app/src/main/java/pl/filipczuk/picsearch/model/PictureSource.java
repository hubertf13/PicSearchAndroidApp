package pl.filipczuk.picsearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

public class PictureSource implements Parcelable {
    private String original;
    private String large2x;
    private String large;
    private String medium;
    private String small;
    private String portrait;
    private String landscape;
    private String tiny;

    public PictureSource(String original, String large2x, String large, String medium, String small, String portrait, String landscape, String tiny) {
        this.original = original;
        this.large2x = large2x;
        this.large = large;
        this.medium = medium;
        this.small = small;
        this.portrait = portrait;
        this.landscape = landscape;
        this.tiny = tiny;
    }

    protected PictureSource(Parcel in) {
        original = in.readString();
        large2x = in.readString();
        large = in.readString();
        medium = in.readString();
        small = in.readString();
        portrait = in.readString();
        landscape = in.readString();
        tiny = in.readString();
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getLarge2x() {
        return large2x;
    }

    public void setLarge2x(String large2x) {
        this.large2x = large2x;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public String getTiny() {
        return tiny;
    }

    public void setTiny(String tiny) {
        this.tiny = tiny;
    }

    public static final Creator<PictureSource> CREATOR = new Creator<PictureSource>() {
        @Override
        public PictureSource createFromParcel(Parcel in) {
            return new PictureSource(in);
        }

        @Override
        public PictureSource[] newArray(int size) {
            return new PictureSource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(original);
        parcel.writeString(large2x);
        parcel.writeString(large);
        parcel.writeString(medium);
        parcel.writeString(small);
        parcel.writeString(portrait);
        parcel.writeString(landscape);
        parcel.writeString(tiny);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureSource that = (PictureSource) o;
        return Objects.equals(original, that.original) && Objects.equals(large2x, that.large2x) && Objects.equals(large, that.large) && Objects.equals(medium, that.medium) && Objects.equals(small, that.small) && Objects.equals(portrait, that.portrait) && Objects.equals(landscape, that.landscape) && Objects.equals(tiny, that.tiny);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original, large2x, large, medium, small, portrait, landscape, tiny);
    }

    @Override
    public String toString() {
        return "PictureSource{" +
                "original='" + original + '\'' +
                ", large2x='" + large2x + '\'' +
                ", large='" + large + '\'' +
                ", medium='" + medium + '\'' +
                ", small='" + small + '\'' +
                ", portrait='" + portrait + '\'' +
                ", landscape='" + landscape + '\'' +
                ", tiny='" + tiny + '\'' +
                '}';
    }
}
