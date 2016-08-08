package org.telegram.hojjat.network;

import org.telegram.hojjat.DTOS.AckDTO;
import org.telegram.hojjat.DTOS.AudioDTO;
import org.telegram.hojjat.DTOS.BaseContentDTO;
import org.telegram.hojjat.DTOS.ChannelDTO;
import org.telegram.hojjat.DTOS.ContentLogDTO;
import org.telegram.hojjat.DTOS.VideoDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface OddgramService {
    public static final String baseUrl = "http://94.182.183.156:7743/api/";

    @Multipart
    @POST("file/upload")
    Call<AckDTO<String>> upload(
            @Part("identifier") RequestBody identifier,
            @Part MultipartBody.Part content //TODO
    );

    @FormUrlEncoded
    @POST("user/locate")
    Call<AckDTO> locateUser(
            @Field("username") String username,
            @Field("userId") String userId,
            @Field("lat") Double latitude,
            @Field("long") Double longitude
    );

    @FormUrlEncoded
    @POST("user/update")
    Call<AckDTO<Long>> updateUser(
            @Field("username") String username,
            @Field("fisname") String firstname,
            @Field("lastname") String lastname,
            @Field("picture") String picture,
            @Field("phone") String phone,
            @Field("visible") Boolean visible
    );

//    get("/api/user/neighbors", (req, res) -> {
//        HttpServletRequest raw = req.raw();
//        return TelegramService.nearbyUser(raw.getParameter("username"),
//                StringUtil.toLong(raw.getParameter("userId")));
//    }, transformer);
//

//    get("/api/content/hots", (req, res) -> {
//        HttpServletRequest raw = req.raw();
//        return TelegramService.listHits(StringUtil.toLong(raw.getParameter("date"))
//                , raw.getParameter("type")
//                , Boolean.parseBoolean(raw.getParameter("local"))
//                , StringUtil.toLong(raw.getParameter("from"))
//                , StringUtil.toLong(raw.getParameter("count")));
//    }, transformer);


    @POST("channel/log")
    Call<AckDTO<Boolean>> logChannel(
            @Field("channel") String channel,
            @Field("hits") Long hits
    );

    @FormUrlEncoded
    @POST
    Call<ChannelDTO> getChannel(
            @Field("channel") String channel
    );

    @FormUrlEncoded
    @POST
    Call<AckDTO> updateChannel(
            @Field("data") ChannelDTO channel //TODO
    );

    @FormUrlEncoded
    @POST("content/log")
    Call<AckDTO<Boolean>> contentLog(
            @Field("type") String type,
            @Field("id") Long contentId,
            @Field("channel") String channel,
            @Field("hits") Long hits
    );

    @FormUrlEncoded
    @POST("content/multi-log")
    Call<AckDTO<List<String>>> contentMultiLog(
            @Field("logs") List<ContentLogDTO> logs
    );

    @FormUrlEncoded
    @POST("content/audio/update")
    Call<AckDTO<List<Boolean>>> updateAudioContent(
            @Field("data") List<AudioDTO> data
    );

    @FormUrlEncoded
    @POST("content/video/update")
    Call<AckDTO<List<Boolean>>> updateVideoContent(
            @Field("data") List<VideoDTO> data
    );

    @FormUrlEncoded
    @POST("content/content/update")
    Call<AckDTO<List<Boolean>>> updateContent(
            @Field("data") List<BaseContentDTO> data
    );

    @FormUrlEncoded
    @POST
    Call<AckDTO<Boolean>> advStatus(
            @Field("channel") String channel
    );

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
