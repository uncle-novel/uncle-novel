package unclezs;

/**
 * @author zhanghongguo@sensorsdata.cn
 * @since 2021/03/19 11:58
 */
public class PluginTest {
    public static void main(String[] args) {
        System.out.println(PluginTest.class.getClassLoader());
    }

    public void hello() {
        System.out.println("我是");
    }
}
