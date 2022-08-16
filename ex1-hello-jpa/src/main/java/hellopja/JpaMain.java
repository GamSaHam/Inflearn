package hellopja;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // 회원등록
//            Member member = new Member();
//            member.setId(2L);
//            member.setName("김길동");
//            em.persist(member);

            // 회원 조회
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.name = " + findMember.getName());

            // 회원 수정
//            Member findMember = em.find(Member.class, 1L);
//            findMember.setName("HelloJPA"); // commit 하기 직전에 update 쿼리 발생


            // dialect 항목을 바꾸어도 sql을 만들어짐
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5)
                    .setMaxResults(10)
                    .getResultList();

            for (Member member : result) {
                System.out.println("member.name = " + member.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }


    }

}
