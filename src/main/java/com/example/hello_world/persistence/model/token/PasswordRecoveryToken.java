package com.example.hello_world.persistence.model.token;


import com.example.hello_world.RecoverPasswordStage;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "password_recovery_tokens")
public class PasswordRecoveryToken extends Token {


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RecoverPasswordStage recoverPasswordStage = RecoverPasswordStage.FRESH;



    public RecoverPasswordStage getRecoverPasswordStage() {
        return recoverPasswordStage;
    }

    public void setRecoverPasswordStage(RecoverPasswordStage recoverPasswordStage) {
        this.recoverPasswordStage = recoverPasswordStage;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordRecoveryToken token1)) return false;
        return valid == token1.valid && id.equals(token1.id) && user.equals(token1.user) && token.equals(token1.token) && creationDate.equals(token1.creationDate) && expiryDate.equals(token1.expiryDate) && recoverPasswordStage.equals(token1.recoverPasswordStage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, token, creationDate, expiryDate, valid, recoverPasswordStage);
    }
}
