package IOC;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SimpleIOC {

    private Map<String,Object> beanMap=new HashMap<>();

    public SimpleIOC(String location) throws Exception {
        loadBeans(location);
    }

    public Object getBean(String id){
        Object bean=beanMap.get(id);
        if(bean==null){
            throw new IllegalArgumentException("no such bean...");
        }
        return bean;
    }

    public void registryBean(String id,Object obj){
        beanMap.put(id,obj);
    }

    public void loadBeans(String location) throws Exception {
        InputStream inputStream=new FileInputStream(location);
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document doc=builder.parse(inputStream);
        Element root=doc.getDocumentElement();
        NodeList childNodes=root.getChildNodes();

        //解析每个bean
        for(int i=0;i<childNodes.getLength();i++){
            Node node=childNodes.item(i);
            if(node instanceof Element){
                Element ele=(Element) node;
                String id=ele.getAttribute("id");
                String className=ele.getAttribute("class");

                Class beanClass=null;
                try {
                    beanClass=Class.forName(className);
                }catch (Exception e){
                    return;
                }

                Object bean=beanClass.newInstance();
                NodeList propertyNodes=ele.getElementsByTagName("property");
                for(int j=0;j<propertyNodes.getLength();j++){
                    Node propertyNode=propertyNodes.item(j);
                    if(propertyNode instanceof Element){
                        Element propertyElement=(Element) propertyNode;
                        String name=propertyElement.getAttribute("name");
                        String value=propertyElement.getAttribute("value");

                        //将name域设置成可访问
                        Field declaredField=beanClass.getDeclaredField(name);
                        declaredField.setAccessible(true);

                        if(value!=null && value.length()>0){
                            //利用反射注入属性
                            declaredField.set(bean,value);
                        }else{
                            String ref=propertyElement.getAttribute("ref");
                            if(ref==null || ref.length()==0){
                                throw new IllegalArgumentException("no such bean...");
                            }
                            declaredField.set(bean,getBean(ref));
                        }

                        //注册到beanMap，IOC容器中
                        registryBean(id,bean);
                    }
                }
            }
        }
    }
}
