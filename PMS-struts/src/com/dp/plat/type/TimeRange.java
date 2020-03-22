package com.dp.plat.type;


public class TimeRange implements UserParamInterface
{
    public static final char TIME_2 = '2';
    
    public static final String DEFAULT = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";

    char[] timelist = new char[336];

    public TimeRange()
    {
        for (int i = 0; i < DEFAULT.length(); i++)
        {
            timelist[i] = DEFAULT.charAt(i);
        }
    }

    public String formatTo()
    {
        StringBuffer sb = new StringBuffer();

        for (char ch : timelist)
        {
            sb.append(ch);
        }
        return sb.toString();
    }

    public boolean parseFrom(String input)
    {
        if (input.length() != 336)
        {
            return false;
        }

        char[] chars = input.toCharArray();

        for (int i = 0; i < 336; i++)
        {
            timelist[i] = chars[i];
        }

        return true;
    }

    @Override
    public String toString()
    {
        return this.formatTo();
    }
    
    public boolean test(int pos, char value)
    {
        return value == timelist[pos]; 
    }
}
