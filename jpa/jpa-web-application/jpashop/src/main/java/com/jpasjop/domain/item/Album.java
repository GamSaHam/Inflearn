package com.jpasjop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;

@Entity
@DiscriminatorColumn(name = "A")
@Getter
@Setter
public class Album extends Item {

    private String artist;
    private String etc;
}
