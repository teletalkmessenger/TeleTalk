package org.telegram.hojjat.network;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OddgramService {
    public static final String baseUrl = "http://94.182.183.156:6743/api/telegram/";

    @GET("list-hits?")
    Call<POJOS.ListHitsResponse> listHits(
            @Query("type") String type,
            @Query("local") boolean local,
            @Query("from") int from,
            @Query("count") int count);

    @FormUrlEncoded
    @POST("log")
    Call<POJOS.LogResponse> log(
            @Field("type") String type,
            @Field("id") int id,
            @Field("channel") String channel,
            @Field("hits") int hits);

    class POJOS {
        public class TrendItem {
            public String channel;
            public int id;
            public int hits;

            @Override
            public String toString() {
                return "TrendItem{" +
                        "channel='" + channel + '\'' +
                        ", id=" + id +
                        ", hits=" + hits +
                        '}';
            }
        }

        public class ListHitsResponse {
            public int errorCode;
            public List<TrendItem> entity;

            @Override
            public String toString() {
                return "ListHitsResponse{" +
                        "errorCode=" + errorCode +
                        ", entity=" + entity +
                        '}';
            }
        }

        public class LogResponse {
            public int errorCode;
            public String entitiy;

            @Override
            public String toString() {
                return "LogResponse{" +
                        "errorCode=" + errorCode +
                        ", entitiy='" + entitiy + '\'' +
                        '}';
            }
        }
    }

    class ServiceGenerator {
        private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        private static Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create());

        public static <S> S createService(Class<S> serviceClass) {
            Retrofit retrofit = builder.client(httpClient.build()).build();
            return retrofit.create(serviceClass);
        }
    }
}
