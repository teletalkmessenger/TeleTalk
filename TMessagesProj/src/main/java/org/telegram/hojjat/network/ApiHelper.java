package org.telegram.hojjat.network;

import org.telegram.hojjat.DTOS.AckDTO;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hojjatimani on 8/8/2016 AD.
 */
public class ApiHelper implements NotificationCenter.NotificationCenterDelegate {
    private static ApiHelper instance;
    private final OddgramService api;
    private List<Integer> channelsToBeUploaded = new ArrayList<>();

    public static ApiHelper getInstance() {
        ApiHelper localInstance = instance;
        if (localInstance == null) {
            synchronized (ApiHelper.class) {
                localInstance = instance;
                if (localInstance == null) {
                    localInstance = instance = new ApiHelper();
                }
            }
        }
        return localInstance;
    }

    public ApiHelper() {
        api = OddgramService.ServiceGenerator.createService(OddgramService.class);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoCantLoad);
    }

    public void logChannel(final int channelId, final String channelUsername, Long hits) {
        Call<AckDTO<Boolean>> call = api.logChannel(channelUsername, hits);
        call.enqueue(new Callback<AckDTO<Boolean>>() {
            @Override
            public void onResponse(Call<AckDTO<Boolean>> call, Response<AckDTO<Boolean>> response) {
                AckDTO<Boolean> result = response.body();
                if (result.getErrorCode() == 0) {
                    if (result.getEntity()) {
                        //should upload channel data
                        channelsToBeUploaded.add(channelId);
                        MessagesController.getInstance().loadFullChat(channelId, 0, true);
                    }
                }
            }

            @Override
            public void onFailure(Call<AckDTO<Boolean>> call, Throwable t) {

            }
        });
    }

    private void uploadChannelData(TLRPC.ChatFull channel) {
        TLRPC.Chat chat = MessagesController.getInstance().getChat(channel.id);
        TLRPC.FileLocation photo_big = chat.photo.photo_big;
        ImageLoader.getInstance().
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (channelsToBeUploaded.contains(chatFull.id)) {
                channelsToBeUploaded.remove(id);
                uploadChannelData(chatFull);
            }
        } else if (id == NotificationCenter.chatInfoCantLoad) {
            int chatId = (Integer) args[0];
            channelsToBeUploaded.remove(chatId);
        }
    }
}
