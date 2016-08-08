package org.telegram.hojjat.DTOS;

/**
 * Created by hojjatimani on 8/8/2016 AD.
 */
public class ChannelDTO {

    private Long id;
    private String name;
    private String desc;
    private String picture;
    private Long hits;
    private Boolean adField;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }

    public void setAdField(Boolean adField) {
        this.adField = adField;
    }
}
