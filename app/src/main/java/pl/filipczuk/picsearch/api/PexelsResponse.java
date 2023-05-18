package pl.filipczuk.picsearch.api;

import java.util.List;

import pl.filipczuk.picsearch.model.Picture;

public class PexelsResponse {
    private List<Picture> photos;

    public PexelsResponse(List<Picture> photos) {
        this.photos = photos;
    }

    public List<Picture> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Picture> photos) {
        this.photos = photos;
    }
}
