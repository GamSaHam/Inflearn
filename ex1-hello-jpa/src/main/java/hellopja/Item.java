package hellopja;

import jdk.jfr.Description;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 기본
@Inheritance(strategy = InheritanceType.JOINED)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn // DTYPE 항목을 추가해준다. 있는게 좋다. 디비상에서 확인해야할대 필요
// 단일 테이블 전략에서는 DTYPE 항목을 추가해준다.
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;


}
