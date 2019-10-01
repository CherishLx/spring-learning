package AOP;

public class AOPTest {
    public static void main(String[] args) {
        MethodInvocation beforeTask=()->System.out.println("before...");
        TransferServiceImpl transferServiceimpl=new TransferServiceImpl();

        //1.创建一个Advice
        Advice beforeAdvice=new BeforeAdvice(new TransferServiceImpl(), beforeTask);

        //2.获取代理对象，调用方法
        TransferService transferServiceProxy=(TransferService) SimpleAOP.getProxy(transferServiceimpl,beforeAdvice);
        transferServiceProxy.transfer();
    }
}
