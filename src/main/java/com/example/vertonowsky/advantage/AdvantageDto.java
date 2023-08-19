package com.example.vertonowsky.advantage;

public class AdvantageDto {

    private Integer id;
    private Integer index;
    private String title;
    private boolean premium;
    private AdvantageType advantageType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public AdvantageType getAdvantageType() {
        return advantageType;
    }

    public void setAdvantageType(AdvantageType advantageType) {
        this.advantageType = advantageType;
    }

}
