package com.example.vertonowsky.discount.model;


import com.example.vertonowsky.discount.DiscountType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "discount_codes")
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id", nullable = false)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DiscountType type;

    @Column(nullable = false, precision = 2)
    private double value;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;



    @OneToMany(mappedBy = "discountCode")
    @JsonIgnore
    private Set<DiscountCodeUsed> codes;


    public DiscountCode() {

    }

    public DiscountCode(String name, String title, DiscountType type, double value, Date expiryDate) {
        this.name = name;
        this.title = title;
        this.type = type;
        this.value = value;
        this.expiryDate = expiryDate;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DiscountType getType() {
        return type;
    }

    public void setType(DiscountType type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Set<DiscountCodeUsed> getCodes() {
        return codes;
    }

    public void setCodes(Set<DiscountCodeUsed> codes) {
        this.codes = codes;
    }




    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        DiscountCode comparing = (DiscountCode) o;
        return (this.id.equals(comparing.id) &&
                this.name.equals(comparing.name) &&
                this.title.equals(comparing.title) &&
                this.type.equals(comparing.type) &&
                this.value == comparing.value &&
                this.expiryDate.equals(comparing.expiryDate));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, title, type, value, expiryDate);
    }


}
