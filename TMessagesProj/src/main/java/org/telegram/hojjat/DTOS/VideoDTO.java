package org.telegram.hojjat.DTOS;

/**
 * Created by hojjatimani on 8/8/2016 AD.
 */
public class VideoDTO extends BaseContentDTO {

    private int duration;
    private int bitrate;

    public VideoDTO() {

    }

    public VideoDTO(ChannelDTO channel, Long contentId, Long timestamp, String text, String thumbnail, String type, int bitrate) {
        setChannel(channel);
        setContentId(contentId);
        setPublishDate(timestamp);
        setBody(text);
        setThumbnail(thumbnail);
        setType(type);
        setBitrate(bitrate);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }
}
