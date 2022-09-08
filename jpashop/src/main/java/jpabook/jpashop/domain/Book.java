package jpabook.jpashop.domain;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Book extends Item{

    private String author;
    private String isbn;
}
