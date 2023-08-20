package com.example.vertonowsky.token.model;


import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * This class inherits from Token class. It means it is a full-featured Entity class parsed by JPA.
 *
 * Verification tokens doesn't require any additional fields, that's why this class is empty.
 */

@Entity
@Table(name = "verification_token")
public class VerificationToken extends Token {


}
