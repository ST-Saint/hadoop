package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.io.FileUtils;

public class Monitor {
    static long timestamp;
    static int loadTimeout = 20;
    private static String hadoopNewVPath = "/home/yayu/Project/Upgrade-Fuzzing/hadoop/branch-3.3.0";
    static File workDir = new File("/home/yayu/tmp/");
    static File failureDir = new File("/home/yayu/tmp/failure/");
    static Integer count = 0;
    static ForkJoinPool pool = new ForkJoinPool(12);

    public static void loadFSImage(String dir) {
        Thread loadThread = new Thread() {
            @Override
            public void run() {
                Integer exitCode = 0;
                Boolean ok = false;
                try {
                    exitCode = systemExecute("timeout " + Integer.toString(loadTimeout) + " ./fuzz_reload.sh " + dir
                            + " > " + dir + "/log.txt 2>&1", new File(hadoopNewVPath));
                    if (exitCode == 0) {
                        ok = true;
                    } else {
                        System.out.println("Load " + dir + " exit : " + Integer.toString(exitCode));
                    }
                } catch (IOException e) {
                    System.out.println("Load " + dir + " exit : " + Integer.toString(exitCode));
                    e.printStackTrace();
                }
                if (ok) {
                    try {
                        FileUtils.deleteDirectory(new File(dir));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    backupDFS(dir);
                }
                synchronized (count) {
                    count += 1;
                    if (count % 10 == 0) {
                        System.out.println("load " + count + " in total");
                    }
                }
            }

            void backupDFS(String dir) {
                try {
                    systemExecute("mv " + dir + " " + failureDir, workDir);
                } catch (IOException e) {
                    System.out.println("Failed in renaming " + dir);
                }
            }
        };
        pool.submit(loadThread);
    }

    public static void main(String[] argv) throws Exception {
        timestamp = 0;
        System.out.println("create dirs " + failureDir + " " + failureDir.exists());
        if (!failureDir.exists()) {
            failureDir.mkdirs();
        }
        Boolean empty = false;
        Boolean findAny = false;
        while (true) {
            File[] files = workDir.listFiles();
            for (File f : files) {
                if (f.isDirectory() && f.getName().startsWith("minicluster-")) {
                    String[] fn = f.toString().split("-");
                    String sufix = fn[1];
                    long sufixTime = Long.parseLong(sufix);
                    if (timestamp < sufixTime) {
                        findAny = true;
                        // System.out.println("current time: " + timestamp + "\n" + "sufix time: " +
                        // sufixTime);
                        loadFSImage(f.toString());
                    }
                }
            }
            for (File f : files) {
                if (f.isDirectory() && f.getName().startsWith("minicluster-")) {
                    String[] fn = f.toString().split("-");
                    String sufix = fn[1];
                    long sufixTime = Long.parseLong(sufix);
                    timestamp = Math.max(timestamp, sufixTime);
                }
            }
            if (findAny == false) {
                if (!empty) {
                    System.out.println("empty");
                    empty = true;
                }
                Thread.sleep(10000);
            } else {
                findAny = false;
                empty = false;
                Thread.sleep(5000);
            }
        }
    }

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
