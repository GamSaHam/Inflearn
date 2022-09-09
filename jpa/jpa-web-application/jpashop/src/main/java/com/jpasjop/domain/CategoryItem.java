package com.jpasjop.domain;

import com.jpasjop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class CategoryItem {

    @Id
    @GeneratedValue
    @Column(name = "category_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categories;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;





}
