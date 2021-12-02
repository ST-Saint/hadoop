package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hdfs.server.namenode.INode;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.slf4j.LoggerFactory;

public class Monitor {
    static long timestamp;
    static int loadTimeout = 20;
    private static String hadoopNewVerPath = FuzzingTest.hadoopNewVerPath;
    static File workDir = new File("/home/yayu/tmp/minicluster");
    static File failureDir = new File("/home/yayu/tmp/failure/");
    static Integer count = 0;
    static ForkJoinPool pool = new ForkJoinPool(1);

    static{
        SnapshotTestHelper.disableLogs();
        GenericTestUtils.disableLog(LoggerFactory.getLogger(NameNode.class));
        System.setProperty("hadoop.log.dir", "/home/yayu/tmp/logs");
        System.setProperty("hadoop.log.file", "hadoop-yayu-namenode-msi.log");
        System.setProperty("yarn.log.dir", "/home/yayu/tmp/logs");
        System.setProperty("yarn.log.file", "hadoop-yayu-namenode-msi.log");
        // GenericTestUtils.setLogLevel(INode.LOG, Level.OFF);
    }

    public static void loadFSImage(File dir) throws InterruptedException {
        Thread loadThread = new Thread() {
            @Override
            public void run() {
                Integer exitCode = 0;
                Boolean ok = true;
                UUID uuid = UUID.randomUUID();
                File copyFile = new File("/home/yayu/tmp/copy/" + "minicluster-" + uuid.toString());
                if (!copyFile.getParentFile().exists()) {
                    copyFile.getParentFile().mkdirs();
                }
                System.out.println("target: " + dir + "\ncopy: " + copyFile.toString());
                try {
                    FileUtils.copyDirectory(dir, copyFile);
                } catch (IOException e) {
                    System.err.println("copy error");
                    e.printStackTrace();
                }
                MiniCluster minicluster = new MiniCluster();
                try {
                    minicluster.startRollingUpgrade(copyFile.toString());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    ok = false;
                    System.err.println("tostring:\n" + e.toString() + "\nmessage:\n" + e.getMessage());
                    System.err.println("Load " + dir + " exit : " + Integer.toString(exitCode));
                    e.printStackTrace();
                }
                if (ok) {
                    try {
                        FileUtils.deleteDirectory(dir);
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

            void backupDFS(File dir) {
                try {
                    System.out.println("target dir: " + new File(failureDir, dir.getName()));
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
                    long sufixTime = Long.parseLong(sufix);
                    if (timestamp < sufixTime) {
                        findAny = true;
                        // System.out.println("current time: " + timestamp + "\n" + "sufix time: " +
                        // sufixTime);
                        loadFSImage(f);
                    }
                }
                break;
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
                System.out.println("empty");
                empty = true;
                Thread.sleep(10000);
            } else {
                findAny = false;
                empty = false;
                Thread.sleep(5000);
            }
            break;
        }
    }
}
