package jpabook.jpashop.domain;

import javax.persistence.*;

@Entity
public class ItemCategory extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "ITEM_CATEGORY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

}
