package com.dp.plat.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.CollationKey;
import java.text.Collator;

public class CommonComparator {
	  /***
     * @param
     * 比较对象的属性用String[]的形式传过来
     * 比较的对象一定要符合javaBean，即要有Set,Get方法
     * */
    String[] fields_user = null;
   
   
    public String[] getFields_user() {
        return fields_user;
    }
    public void setFields_user(String[] fields_user) {
        this.fields_user = fields_user;
    }
    /**
     * 定义排序规则
     * 如果按照不止一个属性进行排序
     * 这按照属性的顺序进行排序,类是sql order by
     * 即只要比较出同位置的属性就停止
     * */
    public int compare(Object obj1, Object obj2)
    {
        //没有属性，则不排序
        if(fields_user == null || fields_user.length<=0)
        {
            return 2;//不比较
        }
        for(int i=0;i<fields_user.length;i++)
        {
            //return compareField(obj1,obj2,fields_user[i]);
            if(compareField(obj1,obj2,fields_user[i])>0)
            {//如果obj1的属性值大于obj2的属性值，则返回正数
                return 1;
             }else if(compareField(obj1,obj2,fields_user[i])<0){
                 return -1;
             }
        }
        return 0;
    }
    /**
     * @param fieldName
     * 根据属性名排序
     * */
    private static int compareField(Object o1,Object o2,String fieldName)
    {
            try
            {
                Object value1 = getFieldValueByName(fieldName,o1);
                Object value2 = getFieldValueByName(fieldName,o2);
              
                //--字符串比较
                if(value1 instanceof String)
                {
                  String v1 = getFieldValueByName(fieldName,o1).toString();
                  String v2 = getFieldValueByName(fieldName,o2).toString();
                    Collator myCollator = Collator.getInstance();
                    CollationKey[] keys = new CollationKey[5];
                    keys[0] = myCollator.getCollationKey(v1);
                    keys[1] = myCollator.getCollationKey(v2);
                    return (keys[0].compareTo(keys[1]));
                }
                //--非比较属性不比较
                else if("java.lang.Boolean".equals(value1.getClass().getName()) || "java.lang.Byte".equals(value1.getClass().getName()))
                {
                    return 0;
                }else
                {
                    BigDecimal b1 = new BigDecimal(value1.toString());
                    BigDecimal b2 = new BigDecimal(value2.toString());
                    return b1.compareTo(b2);
                }
            } catch (Exception e)
            {
                System.out.println("-----------------------------------------------------------------------------");
                System.out.println("---------对象的该属性不存在或者不允许在此安全级别上反射该属性，详情请查阅JAVA DOC--------");
                System.out.println("-----------------------------------------------------------------------------");
                e.printStackTrace();
            }
            //小于
            return -1;
    }
    /**
     * @param
     * fieldName 属性名
     * obj 对象
     * 反射获得该属性的值
     * */
    private static Object getFieldValueByName(String fieldName,Object obj)
    {
        try
        {
            String Letter = fieldName.substring(0,1).toUpperCase();
            String methodStr = "get"+Letter+fieldName.substring(1);
            Method method = obj.getClass().getMethod(methodStr, new Class[]{});
   
            Object value = method.invoke(obj, new Object[]{});
            
            return value;
        }catch(Exception e)
        {
            System.out.println("------------------------------------------------------");
            System.out.println("---------该"+fieldName+"属性不存在----------------------");
            System.out.println("------------------------------------------------------");
            return null;
        }
    }
}
