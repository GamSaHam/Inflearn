package hellopja;


import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class RelationMappingTest {


    @Test
    void manyToOne() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUserName("member1");
            member.setTeam(team);

            em.persist(member);

//            em.flush();
//            em.clear();


            Member findMember = em.find(Member.class, member.getId());

            Team findTeam = findMember.getTeam();

            System.out.println("findTeam.name = " + findTeam.getName());

            System.out.println("===================");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

}
