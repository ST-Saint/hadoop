package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import static org.junit.Assert.assertTrue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshottableDirectoryStatus;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NamenodeRole;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.FsShellGenerator;
import org.apache.hadoop.hdfs.server.namenode.FSImage;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.INode;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.NNStorage.NameNodeFile;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.hdfs.util.Canceler;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.ToolRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.event.Level;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;

@RunWith(JQF.class)
public class UpgradeFuzzingTest {
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


    UpgradeFuzzingTest(){
        conf = new Configuration();
        uuid = UUID.randomUUID();
        conf.set("hadoop.tmp.dir", "/home/yayu/tmp/hdfs-" + uuid.toString());
        conf.set("fs.defaultFS", "hdfs://localhost:" + new Random().nextInt(1024) + 10000);
        conf.set("dfs.replication", "1");
    }

    private static final String HDFS_MINIDFS_BASEDIR = "hdfs.minidfs.basedir";

    public void pretest() throws Exception {
        NameNode namenode = NameNode.createNameNode(new String[0], conf);
        DataNode datanode = DataNode.createDataNode(new String[0], conf);
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
        conf = new Configuration();
        shell.setConf(conf);
        FsShellGenerator fsg = new FsShellGenerator(new Random());
        for (int i = 0; i < 1000; ++i) {
            String[] cmd = fsg.generate();
            FileWriter fw = new FileWriter("upgradefuzz.log", true);
            fw.write(String.join(" ", cmd) + "\n");
            fw.close();
        }
        int res;
        try {
            // res = ToolRunner.run(shell, cmd);
        } finally {
            shell.close();
        }
    }

    @Fuzz
    public void fuzzCommand() throws Exception {
        FsShell shell = new FsShell();
        conf = new Configuration();
        shell.setConf(conf);
        FsShellGenerator fsg = new FsShellGenerator(new Random());
        String[] cmd = fsg.generate();
        int res;
        try {
            // res = ToolRunner.run(shell, cmd);
        } finally {
            shell.close();
        }
    }
}
