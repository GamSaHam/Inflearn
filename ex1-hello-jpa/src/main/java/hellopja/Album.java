package hellopja;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Album") // 이름을 변경해줄 수 있다.
public class Album extends Item{
    private String artist;
}
