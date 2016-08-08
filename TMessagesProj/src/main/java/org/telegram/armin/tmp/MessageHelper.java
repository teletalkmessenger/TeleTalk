package org.telegram.armin.tmp;

import android.util.Log;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vandermonde on 8/7/16.
 */
public class MessageHelper {
    private static final String TAG = "ARMIN_MessageHelper";

    private static volatile MessageHelper Instance = null;


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

    public void getMessages(HashMap<Integer, Integer> message_ids){
        TLRPC.TL_messages_getMessages req = new TLRPC.TL_messages_getMessages();
        req.id = new ArrayList<>();
        req.id.addAll(message_ids.values());
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(TLObject response, TLRPC.TL_error error) {
                if (error == null) {
                    TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
                    for (int a = 0; a < messagesRes.messages.size(); a++) {
                        TLRPC.Message message = messagesRes.messages.get(a);
                        Log.d("ARMIN", message.message);
                    }
                } else {
                    Log.d("ARMIN", error.text);
                }
            }
        });
    }

    public void getChannelMessages(HashMap<Integer, Number[]> info){
        for (Number[] numbers : info.values()) {
            TLRPC.InputChannel inputChannel = new TLRPC.TL_inputChannel();
            inputChannel.channel_id = (int) numbers[1];
            inputChannel.access_hash = (long) numbers[2];
            final TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
            req.channel = inputChannel;
            req.id.add((int) numbers[0]);
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                @Override
                public void run(TLObject response, TLRPC.TL_error error) {
                    if (error == null) {
                        TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
                        for (int a = 0; a < messagesRes.messages.size(); a++) {
                            TLRPC.Message message = messagesRes.messages.get(a);
                            Log.d("ARMIN", "channel: " + message.message);
                        }
                    } else {
                        Log.d("ARMIN", "channel: "+error.text);
                    }
                }
            });
        }
    }
}
