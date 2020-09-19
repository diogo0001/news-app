package domain.ckl_1_android_diogo_tavares;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiClient {

    @GET("challenge")
    Call<List<Article>> getArticlesService();
}
