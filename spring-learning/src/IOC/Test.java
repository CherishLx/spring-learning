package IOC;

public class Test {

    public static void main(String[] args) throws Exception {
        String location=SimpleIOC.class.getClassLoader().getResource("IOC/bean.xml").getFile();
        SimpleIOC IOC=new SimpleIOC(location);
        TV tv=(TV) IOC.getBean("tv");
        System.out.println("tv: "+tv.getLength());
        System.out.println("screen: "+tv.getScreen().getColor());
    }
}
