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
    Call<PexelsResponse> searchPictures(@Query("query") String query, @Query("page") int page, @Query("per_page") int perPage); /*{

        Thread thread = new Thread(() -> {
            ArrayList<Picture> pictures = new ArrayList<>();
            PexelsResponse pexelsResponse = new PexelsResponse(pictures);

            OkHttpClient httpClient = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
            urlBuilder.addQueryParameter("query", query);
            urlBuilder.addQueryParameter("page", String.valueOf(page));
            urlBuilder.addQueryParameter("per_page", String.valueOf(perPage));
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .addHeader("Authorization", apiKey)
                    .url(url)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray photos = json.getJSONArray("photos");

                        for (int i = 0; i < photos.length(); i++) {
                            JSONObject jsonPicture = (JSONObject) photos.get(i);
                            Integer pictureId = (Integer) jsonPicture.get("id");

                            Integer photographerId = (Integer) jsonPicture.get("photographer_id");
                            String photographerName = (String) jsonPicture.get("photographer");
                            Photographer photographer = new Photographer(photographerId, photographerName);

                            JSONObject src = (JSONObject) jsonPicture.get("src");
                            String original = (String) src.get("original");
                            String large2x = (String) src.get("large2x");
                            String large = (String) src.get("large");
                            String medium = (String) src.get("medium");
                            String small = (String) src.get("small");
                            String portrait = (String) src.get("portrait");
                            String landscape = (String) src.get("landscape");
                            String tiny = (String) src.get("tiny");
                            PictureSource pictureSource = new PictureSource(original, large2x, large, medium, small, portrait, landscape, tiny);

                            Picture picture = new Picture(pictureId, photographer, pictureSource);

                            pictures.add(picture);
                        }
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                    pexelsResponse = null;
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

            System.out.println();
        });
        thread.start();
    }*/
}
