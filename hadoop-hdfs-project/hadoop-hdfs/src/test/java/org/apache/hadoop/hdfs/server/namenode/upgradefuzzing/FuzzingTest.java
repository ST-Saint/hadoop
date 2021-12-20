package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.namenode.FSImage;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.Command;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.MiniCluster.PrepareLocalSource;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JQF.class)
public class FuzzingTest {
    public static class HadoopStartupWrapper {
        public StartupOption startOpt;
        private FSNamesystem fsn;
        public FSImage fsImage;

        HadoopStartupWrapper(FSImage fsImage, FSNamesystem fsn, StartupOption startOpt) {
            this.fsImage = fsImage;
            this.fsn = fsn;
            this.startOpt = startOpt;
        }
    }

    private String testDir;

    static {
    }
    static final long seed = 0;
    static final short NUM_DATANODES = 1;
    static final int BLOCKSIZE = 1024;
    static final long txid = 1;
    private final Path dir = new Path("/TestSnapshot");
    static MiniCluster minicluster;
    static String hadoopNewVerPath = "/home/yayu/Project/Upgrade-Fuzzing/hadoop/branch-3.3.0";
    static String localResource = "/home/yayu/tmp/localresource/localsrc/";
    static String localResourceCopy = "/home/yayu/tmp/localresource/localsrc.cpy/";
    static String localResourceRepro = "/home/yayu/tmp/localresource/localsrc[%s-%s]/";
    static File logPath = new File("/home/yayu/Project/Upgrade-Fuzzing/hadoop/branch-2.10.1/fuzz-results/logs");
    static File resourcePath = new File("/home/yayu/tmp/localresource");

    static Configuration conf;
    static MiniDFSCluster cluster;
    static FSNamesystem fsn;
    static DistributedFileSystem hdfs;
    static UUID uuid;
    private static String curTimestamp;
    private static String preTimestamp;

    private static Path workdir = new Path("/workdir");

    static {
        conf = new Configuration();
        uuid = UUID.randomUUID();
        conf.set("hadoop.tmp.dir", "/home/yayu/tmp/minicluster/minicluster-" + "0");
        conf.set("hadoop.home.dir", "/home/yayu/tmp/minicluster/minicluster-" + "0");
        conf.set("hadoop.log.dir", "/home/yayu/tmp/minicluster/minicluster-" + "0" + "/logs");
        conf.set("yarn.log.dir", "/home/yayu/tmp/minicluster/minicluster-" + "0" + "/logs");
        conf.set("fs.defaultFS", "hdfs://localhost:10240");
        conf.set("dfs.namenode.http-address", "127.0.0.1:10241");
        conf.set("dfs.datanode.address", "127.0.0.1:10242");
        conf.set("dfs.datanode.http.address", "127.0.0.1:10244");
        conf.set("dfs.datanode.ipc.address", "127.0.0.1:10243");
        conf.set("dfs.replication", "1");
    }

    public FuzzingTest() {

    }

    private static final String HDFS_MINIDFS_BASEDIR = "hdfs.minidfs.basedir";

    public static int formatNamenode(Configuration conf) throws IOException {
        String[] cmd = new String[] { "bin/hdfs", "-format", "-force", "-nonInteractive" };
        Process process = Runtime.getRuntime().exec(cmd, null);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = "", string;
        while ((string = reader.readLine()) != null) {
            result += string + "\n";
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
        }
        reader.close();
        return process.exitValue();
    }

    public static void pretest() throws Exception {
        System.out.println("pretest: start minicluster");
        minicluster = new MiniCluster();
        minicluster.startCluster(conf);
        minicluster.mkdirs(workdir);
        PrepareLocalSource.generateLocalSnapshot();
        preTimestamp = null;
    }

    // @Before
    public void setUp() throws Exception {
        minicluster = new MiniCluster();
        minicluster.startCluster();
    }

    // @After
    public static void tearDown() throws Exception {
        if (minicluster != null) {
            System.out.println("teardown: shutdown minicluster");
            minicluster.shutDown();
            System.out.println("teardown: copy local resources");
            FileUtils.moveDirectory(new File(localResourceCopy),
                    new File(String.format(localResourceRepro, preTimestamp, curTimestamp)));
        }
        commandLog = "";
        commandIndex = 0;
        fuzzingIndex = 0;
    }

    @Test
    public void testStartupTime() throws Exception {
        setUp();
        tearDown();
    }

    public static String commandLog = "";
    public static Integer commandIndex = 0;
    public static Integer fuzzingIndex = 0;

    @Fuzz
    public void fuzzCommand(InputStream input) throws Exception {
        curTimestamp = Long.toUnsignedString(System.currentTimeMillis());
        System.out.println("get preTime stamp: " + preTimestamp + " + " + curTimestamp);
        if (preTimestamp == null) {
            preTimestamp = curTimestamp;
        }
        minicluster.mkdirs(new Path(workdir, "subdir" + Integer.toString(++fuzzingIndex)));
        FsShell shell = null;
        final CommandGenerator fsg = new CommandGenerator(input);
        // commandLog = "";
        // ExecutorService executor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 20; ++i) {
            try {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Command cmd;
                            cmd = fsg.generate();
                            String cmdString = String.join(" ", cmd.generate());
                            commandLog += "CMD " + Integer.toString(++commandIndex) + ":\n" + cmdString + "\nresult: ";
                            int res = cmd.execute(conf);
                            commandLog += Integer.toString(res);
                            System.out.println("CMD " + Integer.toString(commandIndex) + ":\n" + cmdString
                                    + "\nresult: " + Integer.toString(res));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                Long startTime = System.currentTimeMillis(), endTime;
                thread.start();
                thread.join(60000);
                if (thread.isAlive()) {
                    thread.interrupt();
                    commandLog += "TIMEOUT";
                }
                endTime = System.currentTimeMillis();
                commandLog += "\ntime usage: " + Double.toString((endTime - startTime) / 1000.) + "\n";
            } finally {
                if (shell != null) {
                    shell.close();
                }
            }
        }
        System.out.println("cursor usage: " + fsg.rnd.cursor);
        File logFile = new File(logPath, "upgradefuzz-" + curTimestamp + ".log");
        logFile.getParentFile().mkdirs();
        FileWriter fw = new FileWriter(logFile, true);
        fw.write(commandLog);
        fw.close();
        File dfsFile = new File("/home/yayu/tmp/minicluster/minicluster-0");
        File dfsCopyFile = new File("/home/yayu/tmp/minicluster/minicluster-" + curTimestamp);
        FileUtils.copyDirectory(dfsFile, dfsCopyFile);
    }

    @Test
    public void testCommand() throws Exception {
        FsShell shell = new FsShell();
        // Configuration conf = new Configuration();
        // conf.setQuietMode(false);
        shell.setConf(conf);
        // int res = 0;
        try {
            // res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] {
            // "-allowSnapshot", "/user/yayu" });
            // res = ToolRunner.run(conf, shell, new String[] { "-createSnapshot",
            // "/user/yayu/", "s0" });
            // res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] {
            // "-safemode", "enter" });

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        // int res = cmd.execute(conf);
                        int res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] { "-safemode", "get" });
                        System.out.println("exit code: " + Integer.toString(res));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            thread.join(1000);
            if (thread.isAlive()) {
                thread.interrupt();
            }
            // thread.wait(1000);
            // res = ToolRunner.run(conf, shell, new String[] { "-mkdir", "-p",
            // "/user/yayu/" });
            // res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] { "-mkdir",
            // "-p",
            // "/user/yayu/" });
            // res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] {
            // "-saveNamespace" });
            // res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] {
            // "-safemode", "leave" });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shell.close();
        }
        // FsShell shell = new FsShell();
        // conf.setQuietMode(false);
        // shell.setConf(conf);
        // File seedFile = new
        // File("/home/yayu/Project/Upgrade-Fuzzing/hadoop-3.1.3/fuzz-seeds/seed");
        // InputStream is = new FileInputStream(seedFile);
        // CommandGenerator fsg = new CommandGenerator(is);
        // int res;
        // for (int i = 0; i < 1; ++i) {
        // Command cmd = fsg.generate();
        // res = cmd.execute();
        // }
        // byte[] b = new byte[32768];
        // new Random().nextBytes(b);
        // Files.write(seedFile.toPath(), b);
    }

    public static void createSeed() throws IOException {
        File seedFile = new File("/home/yayu/Project/Upgrade-Fuzzing/hadoop/branch-3.1.3/fuzz-seeds/seed");
        byte[] b = new byte[512];
        new Random(System.currentTimeMillis()).nextBytes(b);
        Files.write(seedFile.toPath(), b);
    }

    public static void main(String[] argv) throws Exception {
        FuzzingTest fz = new FuzzingTest();
        createSeed();
        // fz.testCommand();
    }
}
