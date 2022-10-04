package hellopja;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Period {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 이러한 함수를 만들수 있다.
//    public boolean isWork() {
//        return true
//    }

}
