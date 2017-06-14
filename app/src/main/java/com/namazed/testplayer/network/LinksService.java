package com.namazed.testplayer.network;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface LinksService {

    @GET("rq75b47dtzomw0q/urls_of_music.txt?dl=1")
    Single<ResponseBody> getFileWithUrls();

    @GET
    Single<ResponseBody> getSong(@Url String urlFile);
}
