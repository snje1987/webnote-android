package org.snje.webnote;

/**
 * Created by snje1987 on 17-7-13.
 */

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DataService {

    public static final String cfgFile = "cfg.txt";

    public static boolean saveUserInfo(Context context, String url, String password) {
        File file = new File(context.getFilesDir(), cfgFile);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            String info = url + "\n" + password;
            fos.write(info.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static String[] getSavedUserInfo(Context context) {
        File file = new File(context.getFilesDir(), cfgFile);
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String[] res = new String[2];
            res[0] = br.readLine();
            res[1] = br.readLine();
            return res;
        } catch (Exception e) {
            return null;
        }

    }
}
