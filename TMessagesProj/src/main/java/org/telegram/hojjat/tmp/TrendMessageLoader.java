package org.telegram.hojjat.tmp;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.telegram.hojjat.network.OddgramService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Response;

public class TrendMessageLoader extends Handler {
    private static final String TAG = "TrendMessageLoader";
    private OddgramService oddgramServer = OddgramService.ServiceGenerator.createService(OddgramService.class);
    private Map<String, TLRPC.InputChannel> channelsChached = new ConcurrentHashMap<>();
    private Map<String, String> currentlyLoadingChannels = new ConcurrentHashMap<>();
    private int offset;
    private boolean isLoading;
    private int loaded;

    public static final int itemsPerLoad = 10;
    public static final int loadTimeout = 10 * 1000;

    private static volatile TrendMessageLoader Instance = null;

    public static TrendMessageLoader getInstance() {
        TrendMessageLoader localInstance = Instance;
        if (localInstance == null) {
            synchronized (MessageHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new TrendMessageLoader();
                }
            }
        }
        return localInstance;
    }

    public void loadMore() {
        Log.i(TAG, "loadMore: isLoading=" + isLoading);
        if (isLoading)
            return;
        isLoading = true;
        loaded = 0;
        OddgramServerFetchOperation operation = new OddgramServerFetchOperation();
        operation.msgstype = "video";
        operation.local = false;
        operation.offset = offset;
        operation.count = itemsPerLoad;
        offset = offset + itemsPerLoad;
        enqueue(operation);
        enqueueDelayed(new LoadTimeOutOperation(), loadTimeout);
    }

    private void enqueueDelayed(Operation operation, long delay) {
        Message message = obtainMessage();
        message.obj = operation;
        sendMessageDelayed(message, delay);
    }

    private void enqueue(Operation operation) {
        enqueueDelayed(operation, 0);
    }

    @Override
    public void handleMessage(Message msg) {
        Operation obj = (Operation) msg.obj;
        if (obj instanceof OddgramServerFetchOperation) {
            OddgramServerFetchOperation operation = (OddgramServerFetchOperation) obj;
            if (operation.result == null)
                fetchFromOddgramServer(operation);
            else
                processResultFromOddgramServer(operation.result);
        } else if (obj instanceof TelegramServerFetchOperation) {
            TelegramServerFetchOperation operation = (TelegramServerFetchOperation) obj;
            if (operation.resolvedChannel == null) {
                resolveChannel(operation);
            } else if (operation.loadedMessages == null) {
                fetchMessages(operation);
            } else {
                processLoadedMessages(operation);
            }
        } else if (obj instanceof LoadTimeOutOperation) {
            isLoading = false;
        }
    }

    public void resetLoading() {
        offset = 0;
    }

    private class Operation {

    }

    private class OddgramServerFetchOperation extends Operation {
        String msgstype;
        int offset;
        int count;
        boolean local;
        OddgramService.POJOS.ListHitsResponse result;
    }

    private class TelegramServerFetchOperation extends Operation {
        String channelUserName;
        ArrayList<Integer> msgIds = new ArrayList<>();
        TLRPC.InputChannel resolvedChannel;
        ArrayList<TLRPC.Message> loadedMessages;
    }

    private class LoadTimeOutOperation extends Operation {

    }

    private void fetchFromOddgramServer(final OddgramServerFetchOperation operation) {
        oddgramServer.listHits(operation.msgstype, operation.local, operation.offset, operation.count)
                .enqueue(new retrofit2.Callback<OddgramService.POJOS.ListHitsResponse>() {
                    @Override
                    public void onResponse(Call<OddgramService.POJOS.ListHitsResponse> call, Response<OddgramService.POJOS.ListHitsResponse> response) {
                        Log.d(TAG, "onResponse: response=" + response.raw().body());
                        operation.result = response.body();
                        enqueue(operation);
                    }

                    @Override
                    public void onFailure(Call<OddgramService.POJOS.ListHitsResponse> call, Throwable t) {
                        t.printStackTrace();
                        Log.d(TAG, "onFailure: ");
                    }
                });
    }

    private void processResultFromOddgramServer(OddgramService.POJOS.ListHitsResponse result) {
        if (result.errorCode != 0)
            return;
        List<OddgramService.POJOS.TrendItem> items = result.entity;
        if (items == null || items.isEmpty())
            return;
        List<TelegramServerFetchOperation> operations = new ArrayList<>();
        boolean found;
        for (OddgramService.POJOS.TrendItem item : items) {
            found = false;
            for (TelegramServerFetchOperation operation : operations) {
                if (operation.channelUserName.equals(item.channel)) {
                    operation.msgIds.add(item.id);
                    found = true;
                    break;
                }
            }
            if (!found) {
                TelegramServerFetchOperation newOp = new TelegramServerFetchOperation();
                newOp.channelUserName = item.channel;
                newOp.msgIds.add(item.id);
                operations.add(newOp);
            }
        }
        for (TelegramServerFetchOperation operation : operations) {
            enqueue(operation);
        }
    }

    private void resolveChannel(TelegramServerFetchOperation operation) {
        if (channelsChached.containsKey(operation.channelUserName)) {
            Log.d(TAG, "resolveChannel: " + operation.channelUserName);
            operation.resolvedChannel = channelsChached.get(operation.channelUserName);
            enqueue(operation);
        } else if (currentlyLoadingChannels.containsKey(operation.channelUserName)) {
            enqueueDelayed(operation, 100);
        } else {
            currentlyLoadingChannels.put(operation.channelUserName, "");
            File file = getChannelFile(operation.channelUserName);
            if (file.exists()) {
                resolveChannelFromFile(operation, file);
            } else {
                resolveChannelFromServer(operation);
            }
        }
    }

    private void resolveChannelFromFile(final TelegramServerFetchOperation operation, final File file) {
        Log.d(TAG, "resolveChannelFromFile: " + operation.channelUserName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                    TLRPC.TL_channel channel = (TLRPC.TL_channel) ois.readObject();
                    ois.close();
                    TLRPC.InputChannel resolvedChannel = new TLRPC.TL_inputChannel();
                    resolvedChannel.channel_id = channel.id;
                    resolvedChannel.access_hash = channel.access_hash;
                    operation.resolvedChannel = resolvedChannel;
                    enqueue(operation);
                    channelsChached.put(operation.channelUserName, resolvedChannel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentlyLoadingChannels.remove(operation.channelUserName);
            }
        }).start();
    }

    private File getChannelFile(String channelUsername) {
        String path = Environment.getExternalStorageDirectory() + "/Telegram" + "/.Trends" + "/.Channels";
        new File(path).mkdirs();
        return new File(path, channelUsername);
    }

    private void saveChannelToFile(final TLRPC.TL_channel channel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = getChannelFile(channel.username);
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                    oos.writeObject(channel);
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void resolveChannelFromServer(final TelegramServerFetchOperation operation) {
        Log.d(TAG, "resolveChannelFromServer: " + operation.channelUserName);
        final TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
        req.username = operation.channelUserName;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                if (error == null) {
                    TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                    if (!res.chats.isEmpty()) {
                        TLRPC.TL_channel channel = (TLRPC.TL_channel) res.chats.get(0);
                        TLRPC.InputChannel resolvedChannel = new TLRPC.TL_inputChannel();
                        resolvedChannel.channel_id = channel.id;
                        resolvedChannel.access_hash = channel.access_hash;
                        operation.resolvedChannel = resolvedChannel;
                        enqueue(operation);
                        channelsChached.put(operation.channelUserName, resolvedChannel);
                        saveChannelToFile(channel);
                    }
                }
                currentlyLoadingChannels.remove(operation.channelUserName);
            }
        });
    }

    private void fetchMessages(final TelegramServerFetchOperation operation) {
        final TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
        req.channel = operation.resolvedChannel;
        req.id.addAll(operation.msgIds);
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(TLObject response, TLRPC.TL_error error) {
                if (error == null) {
                    TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
                    operation.loadedMessages = messagesRes.messages;
                    enqueue(operation);
                }
            }
        });
    }

    private void processLoadedMessages(final TelegramServerFetchOperation operation) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.trendsLoaded, operation.loadedMessages);
                loaded += operation.msgIds.size();
                if (loaded >= itemsPerLoad)
                    isLoading = false;
            }
        });
    }
}
