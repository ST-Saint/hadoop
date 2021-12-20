package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FuzzingUtils {

    public static Integer systemExecute(String cmd, File path) throws IOException {
        // FileWriter fw = new FileWriter("upgradefuzz.log", true);
        // fw.write("exec: " + cmd + "\n");
        // fw.write(path.toString() + "\n");
        // fw.close();
        Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", cmd }, null, path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = "", string;
        while ((string = reader.readLine()) != null) {
            // fw.write(string + "\n");
            // fw.flush();
            result += string + "\n";
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
        }
        // fw.close();
        reader.close();
        // System.out.println("exec: " + cmd + " result: " + process.exitValue());
        return process.exitValue();
    }
}
