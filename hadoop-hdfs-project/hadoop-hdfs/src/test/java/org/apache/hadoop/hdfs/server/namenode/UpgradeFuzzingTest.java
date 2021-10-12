package org.apache.hadoop.hdfs.server.namenode;

import static org.junit.Assert.assertTrue;

import org.apache.hadoop.conf.Configuration;
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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
    private static final String HDFS_MINIDFS_BASEDIR = "hdfs.minidfs.basedir";
    UUID uuid;

    class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("enter thread " + Thread.currentThread().getName());
            try {
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                // TODO: handle exception
            }
            System.out.println("thread " + Thread.currentThread().getName() + " done");
        }
    }

    // @Before
    public void setUp() throws IOException {
        conf = new Configuration();
        uuid = UUID.randomUUID();
        testDir = "/tmp/hadoop-yayu" + uuid + "-test/";
        File testDirFile = new File(testDir);
        if (!testDirFile.exists()) {
            testDirFile.mkdir();
        }
        conf.set(HDFS_MINIDFS_BASEDIR, "/tmp/hadoop-yayu" + uuid);
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

    @Fuzz
    public void testFSImage(InputStream input) throws Exception {
        try {
            setUp();
            testSaveLoadImage();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            tearDown();
            // FileUtils.deleteDirectory(new File("/tmp/hadoop-" + uuid));
        }
    }

    private void testSaveLoadImage() throws Exception {
        int s = 0;
        // make changes to the namesystem
        hdfs.mkdirs(dir);
        SnapshotTestHelper.createSnapshot(hdfs, dir, "s" + ++s);
        Path sub1 = new Path(dir, "sub1");
        hdfs.mkdirs(sub1);
        hdfs.setPermission(sub1, new FsPermission((short) 0777));
        Path sub11 = new Path(sub1, "sub11");
        hdfs.mkdirs(sub11);
        checkImage(s);

        hdfs.createSnapshot(dir, "s" + ++s);
        Path sub1file1 = new Path(sub1, "sub1file1");
        Path sub1file2 = new Path(sub1, "sub1file2");
        DFSTestUtil.createFile(hdfs, sub1file1, BLOCKSIZE, (short) 1, seed);
        DFSTestUtil.createFile(hdfs, sub1file2, BLOCKSIZE, (short) 1, seed);
        checkImage(s);

        hdfs.createSnapshot(dir, "s" + ++s);
        Path sub2 = new Path(dir, "sub2");
        Path sub2file1 = new Path(sub2, "sub2file1");
        Path sub2file2 = new Path(sub2, "sub2file2");
        DFSTestUtil.createFile(hdfs, sub2file1, BLOCKSIZE, (short) 1, seed);
        DFSTestUtil.createFile(hdfs, sub2file2, BLOCKSIZE, (short) 1, seed);
        checkImage(s);

        hdfs.createSnapshot(dir, "s" + ++s);
        hdfs.setReplication(sub1file1, (short) 1);
        hdfs.delete(sub1file2, true);
        hdfs.setOwner(sub2, "dr.who", "unknown");
        hdfs.delete(sub2file1, true);
        checkImage(s);

        hdfs.createSnapshot(dir, "s" + ++s);
        Path sub1_sub2file2 = new Path(sub1, "sub2file2");
        hdfs.rename(sub2file2, sub1_sub2file2);

        hdfs.rename(sub1file1, sub2file1);
        checkImage(s);

        hdfs.rename(sub2file1, sub2file2);
        checkImage(s);
    }

    void fuzzFSImage() {
        try {
            NameNode.format(conf);
            File file = new File("/tmp/hadoop-yayu/dfs/name/current/fsimage_0000000000000000000");
            FileOutputStream fileStream = new FileOutputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            dataInputStream.readFully(bytes);
            dataInputStream.close();
            fileStream.write(bytes);
            fileStream.close();
            // DefaultMetricsSystem.initialize("NameNode");
            NameNode.initMetrics(conf, NamenodeRole.NAMENODE);
            FSNamesystem.loadFromDisk(conf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private File dumpTree2File(String fileSuffix) throws IOException {
        File file = getDumpTreeFile(testDir, fileSuffix);
        SnapshotTestHelper.dumpTree2File(fsn.getFSDirectory(), file);
        return file;
    }

    private File getDumpTreeFile(String dir, String suffix) {
        return new File(dir, String.format("dumpTree_%s", suffix));
    }

    /**
     * Create a temp fsimage file for testing.
     *
     * @param dir       The directory where the fsimage file resides
     * @param imageTxId The transaction id of the fsimage
     * @return The file of the image file
     */
    private File getImageFile(String dir, long imageTxId) {
        return new File(dir, String.format("%s_%019d", NameNodeFile.IMAGE, imageTxId));
    }

    /** Save the fsimage to a temp file */
    private File saveFSImageToTempFile() throws IOException {
        SaveNamespaceContext context = new SaveNamespaceContext(fsn, txid, new Canceler());
        FSImageFormatProtobuf.Saver saver = new FSImageFormatProtobuf.Saver(context, conf);
        FSImageCompression compression = FSImageCompression.createCompression(conf);
        File imageFile = getImageFile(testDir, txid);
        fsn.readLock();
        try {
            saver.save(imageFile, compression);
        } finally {
            fsn.readUnlock();
        }
        return imageFile;
    }

    /** Load the fsimage from a temp file */
    private void loadFSImageFromTempFile(File imageFile) throws IOException {
        FSImageFormat.LoaderDelegator loader = FSImageFormat.newLoader(conf, fsn);
        fsn.writeLock();
        fsn.getFSDirectory().writeLock();
        try {
            loader.load(imageFile, false);
            fsn.getFSDirectory().updateCountForQuota();
        } finally {
            fsn.getFSDirectory().writeUnlock();
            fsn.writeUnlock();
        }
    }

    void checkImage(int s) throws IOException {
        final String name = "s" + s;

        // dump the fsdir tree
        File fsnBefore = dumpTree2File(name + "_before");

        // save the namesystem to a temp file
        File imageFile = saveFSImageToTempFile();

        long numSdirBefore = fsn.getNumSnapshottableDirs();
        long numSnapshotBefore = fsn.getNumSnapshots();
        SnapshottableDirectoryStatus[] dirBefore = hdfs.getSnapshottableDirListing();

        // shutdown the cluster
        cluster.shutdown();

        // dump the fsdir tree
        File fsnBetween = dumpTree2File(name + "_between");
        SnapshotTestHelper.compareDumpedTreeInFile(fsnBefore, fsnBetween, true);

        // restart the cluster, and format the cluster
        cluster = new MiniDFSCluster.Builder(conf).format(true).numDataNodes(NUM_DATANODES).build();
        cluster.waitActive();
        fsn = cluster.getNamesystem();
        hdfs = cluster.getFileSystem();

        // load the namesystem from the temp file
        loadFSImageFromTempFile(imageFile);

        // dump the fsdir tree again
        File fsnAfter = dumpTree2File(name + "_after");

        // compare two dumped tree
        SnapshotTestHelper.compareDumpedTreeInFile(fsnBefore, fsnAfter, true);

        long numSdirAfter = fsn.getNumSnapshottableDirs();
        long numSnapshotAfter = fsn.getNumSnapshots();
        SnapshottableDirectoryStatus[] dirAfter = hdfs.getSnapshottableDirListing();

        Assert.assertEquals(numSdirBefore, numSdirAfter);
        Assert.assertEquals(numSnapshotBefore, numSnapshotAfter);
        Assert.assertEquals(dirBefore.length, dirAfter.length);
        List<String> pathListBefore = new ArrayList<String>();
        for (SnapshottableDirectoryStatus sBefore : dirBefore) {
            pathListBefore.add(sBefore.getFullPath().toString());
        }
        for (SnapshottableDirectoryStatus sAfter : dirAfter) {
            Assert.assertTrue(pathListBefore.contains(sAfter.getFullPath().toString()));
        }
    }

    // @Fuzz
    // public void testLoadFromDisk(@From(UpgradeFuzzingGenerator.class)
    // HadoopStartupWrapper startupWrapper) {
    // try {
    // DefaultMetricsSystem.initialize("NameNode");
    // NameNode.initMetrics(conf, NamenodeRole.NAMENODE);
    // FSNamesystem.loadFromDisk(conf);
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // // try {
    // // FSImage fsImage = startupWrapper.fsImage;
    // // StartupOption startOpt = startupWrapper.startOpt;
    // // FSNamesystem fsn = startupWrapper.fsn;
    // // MetaRecoveryContext recovery = startOpt.createRecoveryContext();
    // // fsImage.recoverTransitionRead(startOpt, fsn, recovery);
    // // } catch (IOException e) {
    // // // TODO Auto-generated catch block
    // // }
    // }
}
