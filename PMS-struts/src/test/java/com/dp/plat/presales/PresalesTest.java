package com.dp.plat.presales;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PresalesTest {

    @Test
    public void matchOfficeCode() {
        String officeCode = "162001";
        String areaPowers = "162002,162001,1620011,1162001,162002";
        assertEquals(true, areaPowers.matches(".*\\b" + officeCode + "\\b.*"));
        areaPowers = "162002,1620011,1162001,162002";
        assertEquals(false, areaPowers.matches(".*\\b" + officeCode + "\\b.*"));
        areaPowers = "1620011,1162001,162002";
        assertEquals(false, areaPowers.matches(".*\\b" + officeCode + "\\b.*"));
        areaPowers = "162002,1620011,1162001,162001";
        assertEquals(true, areaPowers.matches(".*\\b" + officeCode + "\\b.*"));
        areaPowers = "162001,1620011,1162001,162002";
        assertEquals(true, areaPowers.matches(".*\\b" + officeCode + "\\b.*"));
        areaPowers = "162001";
        assertEquals(true, areaPowers.matches(".*\\b" + officeCode + "\\b.*"));
        officeCode = null;
        areaPowers = "162001";
        assertEquals(false, areaPowers.matches(".*\\b" + officeCode + "\\b.*"));
    }
}
