package com.dp.plat.type;

import java.awt.Color;

import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

public abstract class ChartUtil
{
    /**
     * 18
     * <table><tr>
     * <td style="background:#6666ff;">#6666ff</td>
     * <td style="background:#99cc32;">#99cc32</td>
     * <td style="background:#bed5f7;">#bed5f7</td>
     * <td style="background:#a68df8;">#a68df8</td>
     * <td style="background:#ffcc66;">#ffcc66</td>
     * <td style="background:#f08080;">#f08080</td>
     * </tr><tr>
     * <td style="background:#ee0000;">#ee0000</td>
     * <td style="background:#92c322;">#92c322</td>
     * <td style="background:#ffcc00;">#ffcc00</td>
     * <td style="background:#9034bf;">#9034bf</td>
     * <td style="background:#8bcfa1;">#8bcfa1</td>
     * <td style="background:#996600;">#996600</td>
     * </tr><tr>
     * <td style="background:#fe78d1;">#fe78d1</td>
     * <td style="background:#e7f2bc;">#e7f2bc</td>
     * <td style="background:#c49cab;">#c49cab</td>
     * <td style="background:#734fe7;">#734fe7</td>
     * <td style="background:#33ffff;">#33ffff</td>
     * <td style="background:#dce9fc;">#dce9fc</td>
     * </tr></table>
     */
    public static final Color[] COLORGROUP_1 = 
        new Color[]
        {
        new Color(0x6666ff), new Color(0x99cc32), new Color(0xbed5f7),
        new Color(0xa68df8), new Color(0xffcc66), new Color(0xf08080),
        
        new Color(0xee0000), new Color(0x92c322), new Color(0xffcc00),
        new Color(0x9034bf), new Color(0x8bcfa1), new Color(0x996600),

        new Color(0xfe78d1), new Color(0xe7f2bc), new Color(0xc49cab),
        new Color(0x734fe7), new Color(0x33ffff), new Color(0xdce9fc),
        };
    
    public static final Color[] COLORGROUP_SITE = 
        new Color[]
        {
        new Color(0x70ff70), new Color(0x7070ff), new Color(0xff7070),
        new Color(0xffff70), 
        };
    
    /**
     * 6
     * <table><tr>
     * <td style="background:#ffd700;">#ffd700</td>
     * <td style="background:#f000ff;">#f000ff</td>
     * <td style="background:#007fff;">#007fff</td>
     * <td style="background:#ff0000;">#ff0000</td>
     * <td style="background:#22e0be;">#22e0be</td>
     * <td style="background:#ff8c00;">#ff8c00</td>
     * </tr></table>
     */
    public static final Color[] COLORGROUP_2 = 
        new Color[]
        {
        new Color(0xffd700), new Color(0xf000ff), new Color(0x007fff),
        new Color(0xff0000), new Color(0x22e0be), new Color(0xff8c00),
        };
    
    /**
     * 6
     * <table><tr>
     * <td style="background:#ffd700;">#ffd700(检测流量)</td>
     * <td style="background:#0000ff;">#0000ff(正常流量)</td>
     * <td style="background:#ff0000;">#ff0000(攻击流量)</td>
     * </tr></table>
     */
    public static final Color[] COLORGROUP_NTC_FLUX = 
        new Color[]
        {
        new Color(0xffd700), new Color(0x000ff), new Color(0xff0000)
        };
    
    /**
     * <table><tr>
     * <td style="background:#ff0000;">#ff0000</td>
     * </tr></table>
     */
    public static final Color COLOR_RED = Color.RED;
    
    /**
     * <table><tr>
     * <td style="background:#00ff00;">#00ff00</td>
     * </tr></table>
     */
    public static final Color COLOR_GREEN = Color.GREEN;
    
    /**
     * <table><tr>
     * <td style="background:#0000ff;">#0000ff</td>
     * </tr></table>
     */
    public static final Color COLOR_BLUE = Color.BLUE;

    /**
     * <table><tr>
     * <td style="background:#000000;color:#ffffff">#000000</td>
     * </tr></table>
     */
    public static final Color COLOR_BLACK = Color.BLACK;

    /**
     * <table><tr>
     * <td style="background:grey;">grey</td>
     * </tr></table>
     */
    public static final Color COLOR_GREY = Color.GRAY;
    
    /**
     * <table><tr>
     * <td style="background:#00DEFF;">#00DEFF</td>
     * </tr></table>
     */
    public static final Color COLOR_LBLUE = new Color(0x00DEFF);

    /**
     * <table><tr>
     * <td style="background:#FF0000;">#FF0000</td>
     * </tr></table>
     */
    public static final Color COLOR_SERVERITY_CRITICAL = new Color(0xFF0000);
    /**
     * <table><tr>
     * <td style="background:#6699FF;">#6699FF</td>
     * </tr></table>
     */
    public static final Color COLOR_SERVERITY_MAJOR = new Color(0x6699FF);
    /**
     * <table><tr>
     * <td style="background:#FFFF99;">#FFFF99</td>
     * </tr></table>
     */
    //public static final Color COLOR_SERVERITY_MINOR = new Color(0xFFFF99);
    public static final Color COLOR_SERVERITY_MINOR = new Color(0xE8D40F);
    
    /**
     * <table><tr>
     * <td style="background:#FF9900;">#FF9900</td>
     * </tr></table>
     */
    public static final Color COLOR_SERVERITY_WARNING = new Color(0xFF9900);

    public static void setXYItemRendererColor(XYItemRenderer render, int size, Color[] colorgroup)
    {
        if(null == colorgroup)
        {
            return;
        }
        
        for(int i=0;i<size && i<colorgroup.length; i++)
        {
            render.setSeriesPaint(i, colorgroup[i]);
        }
    }
    
    @SuppressWarnings("deprecation")
	public static void setPiePlotColor(PiePlot plot, int size, Color[] colorgroup)
    {
        if(null == colorgroup)
        {
            return;
        }
        
        for(int i=0;i<size && i<colorgroup.length; i++)
        {
            plot.setSectionPaint(i, colorgroup[i]);
        }
    }
}
