package com.example.vertonowsky.profile;

public class RecentProgressDto {

    private Integer count;
    private RecentProgressType recentProgressType;
    private Integer percentage;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public RecentProgressType getRecentProgressType() {
        return recentProgressType;
    }

    public void setRecentProgressType(RecentProgressType recentProgressType) {
        this.recentProgressType = recentProgressType;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

}
