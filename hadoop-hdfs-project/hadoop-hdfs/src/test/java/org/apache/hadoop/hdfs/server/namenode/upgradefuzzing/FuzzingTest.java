package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import static org.junit.Assert.assertTrue;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import java.io.BufferedReader;
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
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.FsShellGenerator;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.NoSystemExit.ExitException;
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
        SnapshotTestHelper.disableLogs();
        GenericTestUtils.setLogLevel(INode.LOG, Level.TRACE);
    }
    static final long seed = 0;
    static final short NUM_DATANODES = 1;
    static final int BLOCKSIZE = 1024;
    static final long txid = 1;
    private final Path dir = new Path("/TestSnapshot");

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
        Configuration hdfsClusterConf = new HdfsConfiguration();
        hdfsClusterConf.set("hadoop.tmp.dir", "/home/yayu/tmp/hdfs-" + "0");
        hdfsClusterConf.set("hadoop.home.dir", "/home/yayu/tmp/hdfs-" + "0");
        hdfsClusterConf.set("hadoop.log.dir", "/home/yayu/tmp/hdfs-" + "0" + "/logs");
        hdfsClusterConf.set("yarn.log.dir", "/home/yayu/tmp/hdfs-" + "0" + "/logs");
        hdfsClusterConf.set("fs.defaultFS", "hdfs://localhost:16024");
        // hdfsClusterConf.set("fs.defaultFS", "hdfs://localhost:" + (new
        // Random().nextInt(1024) + 10000));
        hdfsClusterConf.set("dfs.namenode.name.dir", "/home/yayu/tmp/hdfs" + "0");
        hdfsClusterConf.set("dfs.datanode.data.dir", "/home/yayu/tmp/hdfs" + "0");
        hdfsClusterConf.set("dfs.replication", "1");
        hdfsClusterConf.set("dfs.namenode.http-address", "0.0.0.0:9870");
        hdfsClusterConf.set("dfs.namenode.https-address", "0.0.0.0:9871");
        hdfsClusterConf.set("dfs.namenode.secondary.http-address", "0.0.0.0:9868");
        hdfsClusterConf.set("dfs.namenode.secondary.https-address", "0.0.0.0:9869");
        hdfsClusterConf.set("dfs.datanode.address", "0.0.0.0:9866");
        hdfsClusterConf.set("dfs.datanode.http.address", "0.0.0.0:9864");
        hdfsClusterConf.set("dfs.datanode.https.address", "0.0.0.0:9865");
        hdfsClusterConf.set("dfs.datanode.ipc.address", "0.0.0.0:9867");

        // hdfsClusterConf.set("dfs.datanode.failed.volumes.tolerated", "0");
        // hdfsClusterConf.set("dfs.namenode.resource.checked.volumes.minimum", "1");

        // System.out.println(hdfsClusterConf.get("dfs.namenode.http-address"));
        // System.out.println(hdfsClusterConf.get("dfs.namenode.https-address"));
        // System.out.println(hdfsClusterConf.get("dfs.namenode.secondary.http-address"));
        // System.out.println(hdfsClusterConf.get("dfs.namenode.secondary.https-address"));
        // System.out.println(hdfsClusterConf.get("dfs.datanode.address"));
        // System.out.println(hdfsClusterConf.get("dfs.datanode.http.address"));
        // System.out.println(hdfsClusterConf.get("dfs.datanode.https.address"));
        // System.out.println(hdfsClusterConf.get("dfs.datanode.https.address"));
        // System.out.println(hdfsClusterConf.get("dfs.datanode.ipc.address"));

        // format namenode

        // Thread formatThread = new Thread() {
        // @Override
        // public void run() {
        // NoSystemExit.set();
        // try {
        // System.out.println(hdfsClusterConf.get("hadoop.tmp.dir"));
        // NameNode formatNamenode = NameNode.createNameNode(new String[] { "-format",
        // "-force", "-nonInteractive" },
        // hdfsClusterConf);
        // } catch (ExitException e) {
        // // System.out.println(e.status);
        // // e.printStackTrace();
        // if (e.status != 0) {
        // NoSystemExit.down();
        // e.printStackTrace();
        // System.exit(e.status);
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // } finally {
        // NoSystemExit.down();
        // }
        // }
        // };
        // formatThread.start();
        // formatThread.join();
        System.out.println("port: " + hdfsClusterConf.get("fs.defaultFS"));
        NameNode namenode = null;
        // try {
        namenode = NameNode.createNameNode(new String[0], hdfsClusterConf);
        // } catch (MetricsException e) {
        // if (namenode == null) {
        // System.out.println("NO!!!!!");
        // }
        // }
        // DataNode datanode = DataNode.createDataNode(new String[0], hdfsClusterConf);
        // namenode.join();
    }

    // @Before
    public void setUp() throws IOException {
        // uuid = UUID.randomUUID();
        String uuid = "uuid";
        testDir = "/home/yayu/tmp/hadoop-yayu-" + uuid + "-test/";
        File testDirFile = new File(testDir);
        if (!testDirFile.exists()) {
            testDirFile.mkdir();
        }
        conf.set(HDFS_MINIDFS_BASEDIR, "/home/yayu/tmp/hadoop-yayu-" + uuid);
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(NUM_DATANODES).build();
        cluster.waitActive();
        fsn = cluster.getNamesystem();
        hdfs = cluster.getFileSystem();
    }

    // @After
    public void tearDown() throws Exception {
        if (cluster != null) {
            cluster.shutdown();
            cluster = null;
        }
    }

    @Test
    public void testStartupTime() throws Exception {
        setUp();
        tearDown();
    }

    @Test
    public void testCommand() throws Exception {
        FsShell shell = new FsShell();
        shell.setConf(conf);
        File seedFile = new File("/home/yayu/Project/Upgrade-Fuzzing/hadoop-3.3.1/fuzz-seeds/seed");
        InputStream is = new FileInputStream(seedFile);
        FsShellGenerator fsg = new FsShellGenerator(is);
        String[] cmd = null;
        for (int i = 0; i < 1; ++i) {
            cmd = fsg.generate();
            FileWriter fw = new FileWriter("upgradefuzz.log", true);
            fw.write(String.join(" ", cmd) + "\n");
            fw.close();
        }
        cmd = new String[] { "-get", "-crc", "/file4", "/home/yayu/tmp/localsrc/file3" };
        int res;
        try {
            res = ToolRunner.run(shell, cmd);
        } finally {
            shell.close();
        }
        byte[] b = new byte[32768];
        new Random().nextBytes(b);
        Files.write(seedFile.toPath(), b);
    }

    @Fuzz
    public void fuzzCommand(InputStream input) throws Exception {
        FsShell shell = new FsShell();
        // FileWriter fw = new FileWriter("upgradefuzz.log", true);
        // fw.write(conf.get("hadoop.log.dir") + "\n");
        // fw.close();
        // shell.setConf(conf);
        FsShellGenerator fsg = new FsShellGenerator(input);
        String[] cmd = fsg.generate();
        int res;
        try {
            // res = ToolRunner.run(shell, cmd);
        } finally {
            shell.close();
        }
    }

    public static void main(String[] argv) throws Exception {
        FuzzingTest test = new FuzzingTest();
        test.testCommand();
    }
}
