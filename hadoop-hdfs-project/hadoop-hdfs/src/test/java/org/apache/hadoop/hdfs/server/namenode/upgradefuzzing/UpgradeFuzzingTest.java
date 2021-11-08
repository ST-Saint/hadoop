package org.apache.hadoop.hdfs.server.namenode;

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

    Configuration conf = new Configuration();
    MiniDFSCluster cluster;
    FSNamesystem fsn;
    DistributedFileSystem hdfs;
    private static final String HDFS_MINIDFS_BASEDIR = "hdfs.minidfs.basedir";
    // UUID uuid;
    //

    public void pretest() throws EXception{
        // NameNode namenode = NameNode.createNameNode(new String[], conf);
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
    public void testCommand() {
        FsShell shell = new FsShell();
        conf = new Configuration();
        shell.setConf(conf);
        String cmd = generateFuzzingCommand();
        int res;
        try {
            res = ToolRunner.run(shell, cmd);
        } finally {
            shell.close();
        }
        System.exit(res);
    }

    private String generateFuzzingCommand() {
        Random rand = new Random();
        rand.randInt();
        return null;
    }
}
