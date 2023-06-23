package pl.filipczuk.picsearch.api;

import pl.filipczuk.picsearch.BuildConfig;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface PexelsApi {
    String apiUrl = BuildConfig.pexelsUrl;
    String apiKey = BuildConfig.pexelsKey;

    @Headers("Authorization: " + apiKey)
    @GET(apiUrl)
    Call<PexelsResponse> searchPictures(@Query("query") String query, @Query("page") int page, @Query("per_page") int perPage);
}
