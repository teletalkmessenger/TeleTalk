package org.telegram.hojjat.DTOS;

/**
 * Created by hojjatimani on 8/8/2016 AD.
 */
public class ContentLogDTO {

    private String channel;
    private long id;
    private String type;
    private long hits;
    private long latitude;
    private long longitude;

    public ContentLogDTO(String channel, long id, long hits) {
        this.channel = channel;
        this.id = id;
        this.hits = hits;
    }

    public ContentLogDTO(String type, String channel, long id, long hits) {
        this.type = type;
        this.channel = channel;
        this.id = id;
        this.hits = hits;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}
