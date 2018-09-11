package com.bibmovel.client.utils;

import android.graphics.Color;

/**
 * Created by vinibrenobr11 on 08/09/18 at 13:51
 */
public class BookColorHelper {


    public static int getColorByString(String coverColor) {

        if (coverColor.equalsIgnoreCase("blue"))
            return Color.BLUE;

        if (coverColor.equalsIgnoreCase("yellow"))
            return Color.YELLOW;

        if (coverColor.equalsIgnoreCase("green"))
            return Color.GREEN;

        if (coverColor.equalsIgnoreCase("red"))
            return Color.RED;

        return Color.WHITE;
    }
}
