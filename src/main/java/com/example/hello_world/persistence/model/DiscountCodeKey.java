package com.example.hello_world.persistence.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DiscountCodeKey implements Serializable {

    @Column(name = "code_id")
    private Integer codeId;

    @Column(name = "user_id")
    private Integer userId;


    public DiscountCodeKey() {

    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        DiscountCodeKey comparing = (DiscountCodeKey) o;
        return (this.codeId.equals(comparing.codeId) && this.userId.equals(comparing.userId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeId, userId);
    }

}
