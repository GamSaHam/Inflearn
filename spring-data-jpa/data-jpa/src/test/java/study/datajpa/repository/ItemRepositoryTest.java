package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

@SpringBootTest
class ItemRepositoryTest {
    @Autowired ItemRepository itemRepository;

    @Test
    public void save() throws Exception {
        Item item = new Item("A");
        itemRepository.save(item);

        // select query 한번을 하고 insert 추가한다.
        // null 항목도 저장을 하게된다.
        // 영속성 컨텐스트에서 분리될때 추가하는데 이럴경우는 별로 없다.

        // 문자를 id 로 사용할경우 Persistable<String>를 상속받는다.


        // 스프링 JPA Criteria 사용하기 어렵다.
        // 읽어 낼수 없다.

        // Query By Example 한계가 있어 실무에서 사용하기 어렵다.

    }

}