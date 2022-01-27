package hello.core.singleton;

public class StatefulService {


    // private int price; // 상태를 유지하는 필드 싱글톤으로 사용되어지면 안되기 떄문에 사용X

    public int order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);

        return price;
    }

//    public int getPrice() {
//        return price;
//    }
}
