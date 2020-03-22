package com.dp.plat.util;

public class ConvertUtil
{
	public static double KByteToMbps(long kbyte, long interval)
	{
		return (double)kbyte*1024.0*8/interval/1000/1000;
	}
	public static double MbpsToKByte(double mbps, long interval)
	{
		return (double)mbps*1000*1000*interval/8/1024.0;
	}
	
	public static double pTopps(long p, long interval)
	{
		return (double)p/interval;
	}
}
