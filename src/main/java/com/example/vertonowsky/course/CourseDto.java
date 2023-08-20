package com.example.vertonowsky.course;

import com.example.vertonowsky.advantage.AdvantageDto;
import com.example.vertonowsky.chapter.ChapterDto;
import com.example.vertonowsky.payment.PaymentHistoryDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDto {

    private List<AdvantageDto> advantages;
    private OffsetDateTime buyDate;
    private String categoryType;
    private List<ChapterDto> chapters;
    private Integer courseId;
    private String description;
    private OffsetDateTime expiryDate;
    private Integer finishedTopics;
    private String name;
    private List<PaymentHistoryDto> paymentHistories;
    private Integer percentage;
    private Double price;
    private Double pricePromotion;


    public static CourseDto of(Integer courseId, String name, CategoryType categoryType) {
        CourseDto dto = new CourseDto();
        dto.courseId = courseId;
        dto.name = name;
        dto.setCategoryType(categoryType);
        return dto;
    }

    public List<AdvantageDto> getAdvantages() {
        return advantages;
    }

    public void setAdvantages(List<AdvantageDto> advantages) {
        this.advantages = advantages;
    }

    public OffsetDateTime getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(OffsetDateTime buyDate) {
        this.buyDate = buyDate;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        String s = String.valueOf(categoryType).toLowerCase();
        this.categoryType = StringUtils.capitalize(s);
    }

    public List<ChapterDto> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterDto> chapters) {
        this.chapters = chapters;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(OffsetDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getFinishedTopics() {
        return finishedTopics;
    }

    public void setFinishedTopics(Integer finishedTopics) {
        this.finishedTopics = finishedTopics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PaymentHistoryDto> getPaymentHistories() {
        return paymentHistories;
    }

    public void setPaymentHistories(List<PaymentHistoryDto> paymentHistories) {
        this.paymentHistories = paymentHistories;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public String capitalizeCategory() {
        String s = String.valueOf(categoryType).toLowerCase();
        return StringUtils.capitalize(s);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPricePromotion() {
        return pricePromotion;
    }

    public void setPricePromotion(Double pricePromotion) {
        this.pricePromotion = pricePromotion;
    }

    public String getPriceAsString() {
        if (price == null) return null;
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(price);
    }

    public String getPricePromotionAsString() {
        if (pricePromotion == null) return null;
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(pricePromotion);
    }

}
