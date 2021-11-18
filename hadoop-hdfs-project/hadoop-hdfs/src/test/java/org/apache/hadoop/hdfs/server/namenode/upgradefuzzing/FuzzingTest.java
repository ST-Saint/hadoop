package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshottableDirectoryStatus;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NamenodeRole;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.namenode.FSImage;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.INode;
import org.apache.hadoop.hdfs.server.namenode.NNStorage.NameNodeFile;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.CommandGenerator;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.Command;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.NoSystemExit.ExitException;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.hdfs.util.Canceler;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.ToolRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.event.Level;

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
    private static String hadoopNewVPath = "/home/yayu/Project/Upgrade-Fuzzing/hadoop/branch-3.3.1";
    static MiniCluster minicluster;

    Configuration conf;
    MiniDFSCluster cluster;
    FSNamesystem fsn;
    DistributedFileSystem hdfs;
    UUID uuid;

    public FuzzingTest() {
        conf = new Configuration();
        uuid = UUID.randomUUID();
        conf.set("hadoop.tmp.dir", "/home/yayu/tmp/minicluster-" + "0");
        conf.set("hadoop.home.dir", "/home/yayu/tmp/minicluster-" + "0");
        conf.set("hadoop.log.dir", "/home/yayu/tmp/minicluster-" + "0" + "/logs");
        conf.set("yarn.log.dir", "/home/yayu/tmp/minicluster-" + "0" + "/logs");
        conf.set("fs.defaultFS", "hdfs://localhost:10240");
        conf.set("dfs.namenode.http-address", "127.0.0.1:10241");
        conf.set("dfs.datanode.address", "127.0.0.1:10242");
        conf.set("dfs.datanode.http.address", "127.0.0.1:10244");
        conf.set("dfs.datanode.ipc.address", "127.0.0.1:10243");
        conf.set("dfs.replication", "1");
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
        minicluster = new MiniCluster();
        minicluster.startCluster();
    }

    // @Before
    public void setUp() throws Exception {
        minicluster = new MiniCluster();
        minicluster.startCluster();
    }

    // @After
    public void tearDown() throws Exception {
        minicluster.shutDown();
    }

    @Test
    public void testStartupTime() throws Exception {
        setUp();
        tearDown();
    }

    @Fuzz
    public void fuzzCommand(InputStream input) throws Exception {
        FsShell shell = null;
        CommandGenerator fsg = new CommandGenerator(input);
        Command cmd;
        for (int i = 0; i < 20; ++i) {
            try {
                cmd = fsg.generate();
                int res = cmd.execute(conf);
                assertEquals("exit code : " + Integer.toString(res), 0, res);
                Thread.sleep(50);
            } finally {
                if (shell != null) {
                    shell.close();
                }
            }
        }

        // Thread loadFSiamge = new Thread() {
        //     @Override
        //     public void run() {
                Integer exitCode;
                try {
                    exitCode = systemExecute("./fuzz_minicluster_load.sh", new File(hadoopNewVPath));
                    assertTrue(exitCode.toString(), exitCode.equals(0));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    assertTrue(false);
                }
        //     }
        // };
        // loadFSiamge.start();
    }

    @Test
    public void testCommand() throws Exception {
        FsShell shell = new FsShell();
        // Configuration conf = new Configuration();
        // conf.setQuietMode(false);
        shell.setConf(conf);
        int res = 0;
        try {
            res = ToolRunner.run(conf, shell, new String[] { "-mkdir", "-p", "/user/yayu/" });
            res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] { "-allowSnapshot", "/user/yayu" });
            res = ToolRunner.run(conf, shell, new String[] { "-createSnapshot", "/user/yayu/", "s0" });
            // res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] { "-safemode", "enter" });
            // res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] { "-saveNamespace" });
            // res = ToolRunner.run(conf, new DFSAdmin(conf), new String[] { "-safemode", "leave" });
        } finally {
            shell.close();
        }
        System.out.println("exit code: " + Integer.toString(res));
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

    public static Integer systemExecute(String cmd, File path) throws IOException {
        FileWriter fw = new FileWriter("upgradefuzz.log", true);
        fw.write("exec: " + cmd + "\n");
        fw.write(path.toString() + "\n");
        fw.flush();
        Process process = Runtime.getRuntime().exec(cmd, null, path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = "", string;
        while ((string = reader.readLine()) != null) {
            fw.write(string + "\n");
            fw.flush();
            result += string + "\n";
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
        }
        fw.close();
        reader.close();
        return process.exitValue();
    }

    public static void main(String[] argv) throws Exception {
        FuzzingTest fz = new FuzzingTest();
        byte[] b = new byte[1024 * 32];
        new Random(System.currentTimeMillis()).nextBytes(b);
        // fz.fuzzCommand(new ByteArrayInputStream(b));
        fz.testCommand();
    }
}
