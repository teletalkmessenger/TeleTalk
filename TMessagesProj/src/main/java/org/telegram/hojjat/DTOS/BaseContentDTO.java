package org.telegram.hojjat.DTOS;

/**
 * Created by hojjatimani on 8/8/2016 AD.
 */
public class BaseContentDTO {

    private ChannelDTO channel;
    private long contentId;
    private String type;
    private String body;
    private long publishDate;
    private String thumbnail;
    private long hits;

    public BaseContentDTO() {
    }

    public BaseContentDTO(ChannelDTO channel, Long contentId, String type, String body, Long publishDate, String thumbnail) {
        setChannel(channel);
        setContentId(contentId);
        setType(type);
        setBody(body);
        setPublishDate(publishDate);
        setThumbnail(thumbnail);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ChannelDTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelDTO channel) {
        this.channel = channel;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }
}
