package com.whereisdarran.setusbdefault.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class RootUtil {

    public static String executeAsRoot(String command) {
        try {
            Process proc = Runtime.getRuntime().exec( "su" );
            DataOutputStream os = new DataOutputStream(proc.getOutputStream());

            try {
                os.writeBytes(command + "\n");
                os.writeBytes("exit\n");
                os.flush();
            } finally {
                if (os != null) {
                    os.close();
                }
            }

            proc.waitFor();

            StringBuffer output = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            String result = output.toString();

            return result;
        } catch (final InterruptedException exc) {
            return "";
        } catch (final IOException exc) {
            return "";
        }
    }
}
