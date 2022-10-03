package study.datajpa.repository;

public interface NestedClosedProjects {

    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

}
