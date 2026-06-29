package com.dp.plat.context;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.dp.plat.util.StringEscUtil;

public class VContext
{
	private static VelocityEngine ve = null;
	
	private static void initEngine()
	{
		if(null == ve)
		{
			try
			{
				ve = new VelocityEngine();
				InputStream is = new FileInputStream(System.getProperty("webapp.rootpath")+
						"/WEB-INF/velocity.properties");
				Properties pro = new Properties();
				pro.load(is);
				is.close();
				
				ve.init(pro);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
    public static String getVM(String temple, Object... maps)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for(int i=0;i<=(maps.length/2-1);i++)
        {
            map.put((String)maps[i*2], maps[i*2+1]);
        }
        return getVM(temple, map);
    }
    public static String getVM(String temple, Map<String, Object> map)
    {
        StringWriter sw = new StringWriter();
        getVM(sw, temple, map);
        return sw.toString();
    }

    public static void getVM(Writer w, String temple, Object... maps)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for(int i=0;i<=(maps.length/2-1);i++)
        {
            map.put((String)maps[i*2], maps[i*2+1]);
        }
        getVM(w, temple, map);
    }
    
    public static void getVM(Writer w, String temple, Map<String, Object> map)
    {
    	initEngine();
    	
        try
        {
        	
            Template t = ve.getTemplate(temple);
            VelocityContext vctx = new VelocityContext();
            vctx.put("StringUtil", new StringEscUtil());

            for(Object obj : map.keySet())
            {
                vctx.put(obj.toString(), map.get(obj));
            }
            
            t.merge(vctx, w);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
