package jpabook.jpashop.domain;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Movie extends Item{
    private String director;
    private String actor;
}
