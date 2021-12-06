package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import static org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.FuzzingUtils.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hdfs.server.namenode.INode;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.LoggerFactory;

public class Monitor {
    static long timestamp;
    static int loadTimeout = 20;
    private static String hadoopNewVerPath = FuzzingTest.hadoopNewVerPath;
    static File workDir = new File("/home/yayu/tmp/minicluster");
    static File failureDir = new File("/home/yayu/tmp/failure/");
    static Integer count = 0;
    static ForkJoinPool pool = new ForkJoinPool(4);
    static String hadoopRoot = "/home/yayu/Project/Upgrade-Fuzzing/hadoop/branch-3.3.0";

    static Pattern exceptionPattern = Pattern.compile("(?m)^.*?Exception.*(?:\\R+^\\s*at .*)+");

    static {
        // SnapshotTestHelper.disableLogs();
        // GenericTestUtils.disableLog(LoggerFactory.getLogger(NameNode.class));
        // LogManager.getRootLogger().setLevel(Level.OFF);
        // System.setProperty("hadoop.log.dir", "/home/yayu/tmp/logs");
        // System.setProperty("hadoop.log.file", "hadoop-yayu-namenode-msi.log");
        // System.setProperty("yarn.log.dir", "/home/yayu/tmp/logs");
        // System.setProperty("yarn.log.file", "hadoop-yayu-namenode-msi.log");
        // GenericTestUtils.setLogLevel(INode.LOG, Level.OFF);
    }

    public static void loadFSImage(File dir) throws InterruptedException {
        Thread loadThread = new Thread() {
            @Override
            public void run() {
                Integer exitCode = 0;
                Boolean ok = true;
                UUID uuid = UUID.randomUUID();
                File copyDir = new File("/home/yayu/tmp/copy/" + "minicluster-" + uuid.toString());
                if (!copyDir.getParentFile().exists()) {
                    copyDir.getParentFile().mkdirs();
                }
                // System.out.println("target: " + dir + "\ncopy: " + copyDir.toString());
                try {
                    FileUtils.copyDirectory(dir, copyDir);
                } catch (IOException e) {
                    System.err.println("copy error");
                    e.printStackTrace();
                }
                File logFile = new File(dir, "log.txt");
                try {
                    exitCode = systemExecute("./fuzz.sh MiniCluster -r -c -p " + copyDir + " > " + logFile + " 2>&1",
                            new File(hadoopRoot));
                    FileInputStream fis = new FileInputStream(logFile);
                    byte[] logBytes = new byte[(int) logFile.length()];
                    fis.read(logBytes);
                    fis.close();
                    String logContent = new String(logBytes, "UTF-8");
                    Matcher m = exceptionPattern.matcher(logContent);
                    if (m.find()) {
                        int cnt = m.groupCount();
                        for (int i = 1; i < cnt; ++i) {
                            System.out.println("get excepiton " + i + " " + m.group(i));
                        }
                    }
                    if (exitCode != 0) {
                        ok = false;
                    }
                } catch (IOException e) {
                    ok = false;
                    e.printStackTrace();
                }
                // MiniCluster minicluster = new MiniCluster();
                // try {
                // minicluster.startRollingUpgrade(copyFile.toString());
                // } catch (Exception e) {
                // ok = false;
                // System.err.println("tostring:\n" + e.toString() + "\nmessage:\n" +
                // e.getMessage());
                // System.err.println("Load " + dir + " exit : " + Integer.toString(exitCode));
                // e.printStackTrace();
                // }
                try {
                    if (ok) {
                        FileUtils.deleteDirectory(dir);
                    } else {
                        backupDFS(dir);
                    }
                    FileUtils.deleteDirectory(copyDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (count) {
                    count += 1;
                    if (count % 10 == 0) {
                        System.out.println("load " + count + " in total");
                    }
                }
            }

            void backupDFS(File dir) {
                try {
                    System.out.println("fail to load target dir: " + new File(failureDir, dir.getName()) + "\n");
                    FileUtils.moveDirectory(dir, new File(failureDir, dir.getName()));
                } catch (IOException e) {
                    System.out.println("Failed in renaming " + dir);
                }
            }
        };
        loadThread.start();
        loadThread.join();
        // pool.submit(loadThread);
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
                    try {
                        long sufixTime = Long.parseLong(sufix);
                        if (timestamp < sufixTime) {
                            findAny = true;
                            // System.out.println("current time: " + timestamp + "\n" + "sufix time: " +
                            // sufixTime);
                            loadFSImage(f);
                        }
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
            for (File f : files) {
                if (f.isDirectory() && f.getName().startsWith("minicluster-")) {
                    String[] fn = f.toString().split("-");
                    String sufix = fn[1];
                    try {
                        long sufixTime = Long.parseLong(sufix);
                        timestamp = Math.max(timestamp, sufixTime);
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
            if (findAny == false) {
                System.out.println("empty");
                empty = true;
                Thread.sleep(10000);
            } else {
                findAny = false;
                empty = false;
                Thread.sleep(5000);
            }
        }
    }
}
