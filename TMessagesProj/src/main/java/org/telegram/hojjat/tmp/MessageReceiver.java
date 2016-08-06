package org.telegram.hojjat.tmp;

import org.telegram.tgnet.TLRPC;

/**
 * Created by hojjatimani on 7/19/2016 AD.
 */
public interface MessageReceiver {
        void messageReceived(TLRPC.Message msg);
}
