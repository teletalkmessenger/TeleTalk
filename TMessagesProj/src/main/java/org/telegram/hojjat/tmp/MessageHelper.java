package org.telegram.hojjat.tmp;

import android.util.Log;

import org.telegram.hojjat.network.OddgramService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageHelper implements MessageReceiver{
    private static final String TAG = "HOJJAT_MessageHelper";

    private static volatile MessageHelper Instance = null;

    interface OnCompleteDelegate {
        void onComplete(Object... args);
    }

    public static MessageHelper getInstance() {
        MessageHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (MessageHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MessageHelper();
                }
            }
        }
        return localInstance;
    }

    public void getMessage(String channelId, final Integer msgId, final MessageReceiver receiver) {
        TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
        req.username = channelId;
        final int reqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                if (error == null) {
                    TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                    MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, false, true);
                    if (!res.chats.isEmpty()) {
                        TLRPC.Chat chat = res.chats.get(0);
                        TLRPC.InputChannel inputChannel = new TLRPC.TL_inputChannel();
                        inputChannel.channel_id = chat.id;
                        inputChannel.access_hash = chat.access_hash;
                        final TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
                        req.channel = inputChannel;
                        req.id.add(msgId);
                        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                            @Override
                            public void run(TLObject response, TLRPC.TL_error error) {
                                executedRequests++;
                                if (error == null) {
                                    TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
                                    receiver.messageReceived(messagesRes.messages.get(0));
                                }
                            }
                        });
                    } else {
                        executedRequests++;
                    }
                } else {
                    executedRequests++;
                }
            }
        });
    }

    public void loadMoreMessageIfPossible(int offset) {
        OddgramService service = OddgramService.ServiceGenerator.createService(OddgramService.class);
        Call<OddgramService.POJOS.ListHitsResponse> call = service.listHits("video", false, offset, 10);
        call.enqueue(new Callback<OddgramService.POJOS.ListHitsResponse>() {
            @Override
            public void onResponse(Call<OddgramService.POJOS.ListHitsResponse> call, Response<OddgramService.POJOS.ListHitsResponse> response) {
                Log.d(TAG, "onResponse: response=" + response.raw().body());
                if (response.body().entity != null)

                    for (OddgramService.POJOS.TrendItem item : response.body().entity) {
                        getMessage(item.channel, new Integer(item.id), MessageHelper.this);
                    }
                sendMsgsWhenLoadingDone(response.body().entity.size());
            }

            @Override
            public void onFailure(Call<OddgramService.POJOS.ListHitsResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: ");
            }
        });
//        Log.d(TAG, "loadMoreMessageIfPossible: offset=" + offset);
//        try {
//            File dir = new File(ActivityTrends.CHATS_DIR);
//            dir.mkdirs();
//            File file = new File(dir, ActivityTrends.TRENDS_FILE);
//            if (!file.exists()) return;
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            String line = null;
//            int count = 0;
//            while ((line = reader.readLine()) != null) {
//                count++;
//                if (count <= offset)
//                    continue;
//                String[] split = line.split(":::");
//                MessageHelper.getInstance().getMessages(split[0], new Integer(split[1]), this);
//                if (count - offset >= 5)
//                    break;
//            }
//            reader.close();
//            if (count > offset)
//                sendMsgsWhenLoadingDone(count - offset);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    int toBeloaded = 0;
    int executedRequests;
    ArrayList<TLRPC.Message> loadedMsgs = new ArrayList<>();

    @Override
    public void messageReceived(TLRPC.Message msg) {
        loadedMsgs.add(msg);
        sendMessagesIfAllRequestsAreExecuted();
    }

    private void sendMessagesIfAllRequestsAreExecuted() {
        Log.d(TAG, "sendMessagesIfAllRequestsAreExecuted: toBeloaded=" + toBeloaded + " executedRequests=" + executedRequests);
        if (toBeloaded != 0 && executedRequests == toBeloaded) {
            sendMessages();
        }
    }

    private void sendMsgsWhenLoadingDone(int toBeloaded) {
        Log.d(TAG, "sendMsgsWhenLoadingDone: toBeloaded=" + toBeloaded);
        this.toBeloaded = toBeloaded;
        sendMessagesIfAllRequestsAreExecuted();
    }

    private void sendMessages() {
        final ArrayList<TLRPC.Message> messages = new ArrayList<>(loadedMsgs);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.trendsLoaded, messages);
            }
        });
        toBeloaded = 0;
        executedRequests = 0;
        loadedMsgs.clear();
    }

    public void loadMessages(int offset) {
        fetchTrendMessagesDataFromServer("video", false, offset, offset + 10);
    }

    private void loadChannelsTrendsMessages(ChannelTrendMessages channelTrends) {
        resolveChannel(channelTrends.channelUserName, channelResolvedDelegate);
    }

    private void fetchTrendMessagesDataFromServer(String type, boolean local, int offset, int count) {
        server.listHits(type, local, offset, count).enqueue(new Callback<OddgramService.POJOS.ListHitsResponse>() {
            @Override
            public void onResponse(Call<OddgramService.POJOS.ListHitsResponse> call, Response<OddgramService.POJOS.ListHitsResponse> response) {
                Log.d(TAG, "onResponse: response=" + response.raw().body());
                ChannelTrendList channelTrendList = getChannelTrendList(response.body().entity);
                if (channelTrendList != null)
                    for (ChannelTrendMessages channelTrends : channelTrendList.channelTrends) {
                        trendMessageDataFetchedFromServer.onComplete(channelTrends);
                    }
            }

            @Override
            public void onFailure(Call<OddgramService.POJOS.ListHitsResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private void resolveChannel() {

    }

    OddgramService server = OddgramService.ServiceGenerator.createService(OddgramService.class);

    OnCompleteDelegate trendMessageDataFetchedFromServer = new OnCompleteDelegate() {
        @Override
        public void onComplete(Object... args) {
            ChannelTrendMessages channelTrends = (ChannelTrendMessages) args[0];
        }
    };

    OnCompleteDelegate channelResolvedDelegate = new OnCompleteDelegate() {
        @Override
        public void onComplete(Object... args) {

        }
    };

    OnCompleteDelegate trendMessagesLoaded = new OnCompleteDelegate() {
        @Override
        public void onComplete(Object... args) {

        }
    };

    private void resolveChannel(String s, OnCompleteDelegate delegate) {
        //TODO
    }

    private ChannelTrendList getChannelTrendList(List<OddgramService.POJOS.TrendItem> items) {
        if (items == null || items.isEmpty())
            return null;
        ChannelTrendList trends = new ChannelTrendList();
        for (OddgramService.POJOS.TrendItem item : items) {
            trends.addItem(item);
        }
        return trends;
    }

    class ChannelTrendMessages {
        String channelUserName;
        ArrayList<Integer> trendMsgIds = new ArrayList<>();

        public ChannelTrendMessages(String userName) {
            this.channelUserName = userName;
        }
    }

    class ChannelTrendList {
        ArrayList<ChannelTrendMessages> channelTrends = new ArrayList<>();

        public void addItem(OddgramService.POJOS.TrendItem item) {
            ChannelTrendMessages channelTrend = getChannelIfExist(item.channel);
            if (channelTrend == null) {
                channelTrend = new ChannelTrendMessages(item.channel);
                channelTrends.add(channelTrend);
            }
            channelTrend.trendMsgIds.add(new Integer(item.id));
        }

        ChannelTrendMessages getChannelIfExist(String username) {
            for (ChannelTrendMessages channel : channelTrends) {
                if (channel.channelUserName.equals(username))
                    return channel;
            }
            return null;
        }
    }




    class LoadOperation{
        String msgstype;
        int offset;
        int count;
        boolean local;
        ChannelTrendList toBeLoaded = new ChannelTrendList();
        ArrayList<TLRPC.Message> loadedMessages = new ArrayList<>();
    }
}