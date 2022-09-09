package com.jpasjop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;

@Entity
@DiscriminatorColumn(name = "B")
@Getter
@Setter
public class Book extends Item {

    private String author;
    private String isbn;


}
