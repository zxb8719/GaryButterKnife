package gary.com.garybutterknife.MyBufferKnife;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *View解析类，主要用于解析含有注解的View成员变量
 */
public class ViewBinderParser {
    /**
     * 初始化解析
     * @param object
     */
    public static void  init(Object object )
    {
        ViewBinderParser viewBinderParser = new ViewBinderParser();
        try {
            viewBinderParser.parser(object);
            viewBinderParser.parserMethod(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 解析成员方法
     * @param object
     * @throws Exception
     */

    public void parserMethod(final Object object) throws Exception
    {
        View view = null;
       // Method[] methods = clazz.getDeclaredMethods();
        Method[] methods = object.getClass().getDeclaredMethods();
        for(final Method method : methods)
        {
            if(method.isAnnotationPresent(ViewBinder.class))
            {
                ViewBinder inject = method.getAnnotation(ViewBinder.class);
                int clickID = inject.onClickID();
                if(clickID <= 0 )
                {
                    throw new Exception("id must not be null");
                }
                if(object instanceof View)
                {
                    view = ((View) object).findViewById(clickID);
                }else if(object instanceof Activity)
                {
                    view = ((Activity) object).findViewById(clickID);
                }
               if(view != null)
               {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                method.invoke(object,null);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }catch (NullPointerException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
               }
            }
        }
    }





    /**
     * 开始执行解析方法
     * @param object
     * @throws Exception
     */
    public void parser(Object object) throws  Exception
    {
        View view = null;
        //获取目标对象的字节码
        final Class<?> clazz = object.getClass();
        //获取目标对象定义的成员变量
        //getFields()获得某个类的所有的公共（public）的字段，包括父类。
        //getDeclaredFields()获得某个类的所有申明的字段，即包括public、private和proteced，
        //但是不包括父类的申明字段。
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields)
        {
            //检查object类中的字段是否含有@ViewBinder注解
            if(field.isAnnotationPresent(ViewBinder.class))
            {
                //若存在就获取注解
                ViewBinder inject = field.getAnnotation(ViewBinder.class);
                int id = inject.id();
                if(id <=0)
                {
                    throw  new Exception("id must not be null");
                }
                field.setAccessible(true);
                if(object instanceof  View)
                {
                    view = ((View) object).findViewById(id);
                }else if(object instanceof Activity)
                {
                    view = ((Activity) object).findViewById(id);
                }
                field.set(object,view);

            }
        }
    }

}
