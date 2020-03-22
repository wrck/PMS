package com.dp.plat.type;

import java.io.*;

public class FileSuffixFilter implements FilenameFilter
{
	String filter;

	public FileSuffixFilter(String filterStr)
	{
		this.filter = filterStr;
	}

	public boolean accept(File dir, String name)
	{
		String f = new File(name).getName();
		return f.endsWith(filter);
	}
}
