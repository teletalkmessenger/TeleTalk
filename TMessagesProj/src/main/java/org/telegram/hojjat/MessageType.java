package org.telegram.hojjat;

import org.telegram.messenger.MessageObject;

/**
 * Created by hojjatimani on 8/1/2016 AD.
 */
public enum MessageType {
    video, image, gif, text, music, sticker, voice, unknown;

    public static MessageType getMessageType(MessageObject msg) {
        if (msg.isVideo()) {
            return video;
        } else if (msg.isGif()) {
            return gif;
        } else if (msg.isMusic()) {
            return music;
        } else if (msg.isSticker()) {
            return sticker;
        } else if (msg.isVoice()) {
            return voice;
        } else {
            return text;
        }
    }
}
