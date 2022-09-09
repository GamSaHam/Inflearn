package com.jpasjop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

// 내장형 타입은 값 타입이므로 Setter 항목을 private로 설정하여 불변하게 설정을 한다.
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipCode;

    protected Address() {
    }

    public Address(String city, String street, String zipCode) {
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }
}
