package com.dp.plat.type;

import java.io.*;

public class CommonFileFilter implements FilenameFilter
{
    String filter;

    public CommonFileFilter(String filterStr)
    {
        this.filter = filterStr;
    }

    public boolean accept(File dir, String name)
    {
        String f = new File(name).getName();
        return f.endsWith(filter);
    }
}
