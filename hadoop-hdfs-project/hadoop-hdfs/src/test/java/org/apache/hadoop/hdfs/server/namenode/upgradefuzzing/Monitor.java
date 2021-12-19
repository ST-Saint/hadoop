package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import static org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.FuzzingUtils.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
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

    static Pattern exceptionPattern = Pattern.compile("(?m)^.*?Exception[^\\n]*((\\R^\\s*at .*)+)");
    static Set<String> exceptionSet = new HashSet<>();

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

    static Boolean fail = null;

    static Boolean loadAndTest(final File dir, final File logFile) throws Exception {
        fail = true;
        Thread loadThread = new Thread() {
            @Override
            public void run() {
                Integer exitCode = 0;
                // Boolean ok = true, duplicate = false;
                // System.out.println("target: " + dir + "\ncopy: " + copyDir.toString());
                try {
                    exitCode = systemExecute("./fuzz.sh MiniCluster -r -c -p " + dir + " > " + logFile + " 2>&1",
                            new File(hadoopRoot));
                    FileInputStream fis = new FileInputStream(logFile);
                    byte[] logBytes = new byte[(int) logFile.length()];
                    fis.read(logBytes);
                    fis.close();
                    String logContent = new String(logBytes, "UTF-8");
                    Matcher m = exceptionPattern.matcher(logContent);
                    // duplicate = checkDuplication(m);
                    if (exitCode != 0) {
                        fail = false;
                    }
                } catch (IOException e) {
                    fail = false;
                    e.printStackTrace();
                }
                synchronized (count) {
                    count += 1;
                    if (count % 10 == 0) {
                        System.out.println("load " + count + " in total");
                    }
                }
            }

        };
        loadThread.start();
        loadThread.join();
        return fail;
    }

    public static void loadFSImage(File dir) throws Exception {
        UUID uuid = UUID.randomUUID();
        File copyDir = new File("/home/yayu/tmp/copy/" + "minicluster-" + uuid.toString());
        if (!copyDir.getParentFile().exists()) {
            copyDir.getParentFile().mkdirs();
        }
        try {
            FileUtils.copyDirectory(dir, copyDir);
        } catch (IOException e) {
            System.err.println("copy error");
            e.printStackTrace();
        }

        Boolean loadFailure = false;
        for (Integer epoch = 0; epoch < 2; ++epoch) {
            File logFile = new File(dir, "log" + Integer.toString(epoch) + ".txt");
            loadFailure = loadFailure | loadAndTest(copyDir, logFile);
        }
        try {
            if (loadFailure) {
                backupDFS(dir);
            } else {
                // FileUtils.deleteDirectory(dir);
            }
            FileUtils.deleteDirectory(copyDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void backupDFS(File dir) {
        try {
            System.out.println("fail to load target dir: " + new File(failureDir, dir.getName()) + "\n");
            FileUtils.moveDirectory(dir, new File(failureDir, dir.getName()));
        } catch (IOException e) {
            System.out.println("Failed in renaming " + dir);
        }
    }

    static Boolean checkDuplication(Matcher m) {
        String key = "";
        while (m.find()) {
            key += m.group(1);
        }
        if (exceptionSet.contains(key)) {
            return true;
        } else {
            exceptionSet.add(key);
            return false;
        }

    }

    static void mockMain() throws Exception {
        File logFile = new File("/home/yayu/tmp/failure/minicluster-1638797619085/log.txt");
        FileInputStream fis = new FileInputStream(logFile);
        byte[] logBytes = new byte[(int) logFile.length()];
        fis.read(logBytes);
        fis.close();
        String logContent = new String(logBytes, "UTF-8");
        Matcher m = exceptionPattern.matcher(logContent);
        // System.out.println(StringEscapeUtils.escapeJava(logContent));
        while (m.find()) {
            int cnt = m.groupCount();
            for (int i = 1; i < cnt; ++i) {
                System.out.println("get excepiton " + i + "\n" + m.group(i));
            }
        }
        System.exit(0);
    }

    public static void main(String[] argv) throws Exception {
        timestamp = 0;
        System.out.println("create dirs " + failureDir);
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
