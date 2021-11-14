
package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.BasicParser;

import static org.apache.hadoop.hdfs.DFSConfigKeys.*;

public class MiniCluster {
    Configuration conf;
    MiniDFSCluster cluster;
    FSNamesystem fsn;
    DistributedFileSystem hdfs;
    private int nameNodePort = 10240;
    private int nameNodeHttpPort = 10241;
    private String dataNodePort = "127.0.0.1:10242";
    private String dataNodeIPCPort = "127.0.0.1:10243";
    private String dataNodeHttpPort = "127.0.0.1:10244";

    static final long seed = 0;
    static final short NUM_DATANODES = 1;
    static final int BLOCKSIZE = 1024;
    static final long txid = 1;

    private Options options;
    private CommandLine cmdLine;

    public static final String HDFS_MINIDFS_BASEDIR = "hdfs.minidfs.basedir";

    public void startCluster() throws IOException, InterruptedException {
        conf = new Configuration();
        // conf.set("hadoop.tmp.dir", "/home/yayu/tmp/hdfs-" + "0");
        conf.set(HDFS_MINIDFS_BASEDIR, "/home/yayu/tmp/minicluster-0");
        conf.set(DFS_DATANODE_ADDRESS_KEY, String.valueOf(dataNodePort));
        conf.set(DFS_DATANODE_IPC_ADDRESS_KEY, dataNodeIPCPort);
        conf.set(DFS_DATANODE_HTTP_ADDRESS_KEY, dataNodeHttpPort);
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(NUM_DATANODES);
        builder.nameNodePort(nameNodePort);
        builder.nameNodeHttpPort(nameNodeHttpPort);
        builder.checkDataNodeAddrConfig(true);
        builder.checkDataNodeHostConfig(true);

        cluster = builder.build();
        cluster.waitActive();
        fsn = cluster.getNamesystem();
        hdfs = cluster.getFileSystem();
        // cluster.getnamenode
    }

    public void startAndShutdown() throws Exception {
        conf = new Configuration();
        // conf.set("hadoop.tmp.dir", "/home/yayu/tmp/hdfs-" + "0");
        conf.set(HDFS_MINIDFS_BASEDIR, "/home/yayu/tmp/minicluster-0");
        conf.set(DFS_DATANODE_ADDRESS_KEY, String.valueOf(dataNodePort));
        conf.set(DFS_DATANODE_IPC_ADDRESS_KEY, dataNodeIPCPort);
        conf.set(DFS_DATANODE_HTTP_ADDRESS_KEY, dataNodeHttpPort);

        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(NUM_DATANODES);
        builder.nameNodePort(nameNodePort);
        builder.nameNodeHttpPort(nameNodeHttpPort);
        builder.checkDataNodeAddrConfig(true);
        builder.checkDataNodeHostConfig(true);
        cluster = builder.build();
        cluster.waitActive();
        if (cluster != null) {
            cluster.shutdown();
        }
    }

    public void start(String[] argv) throws Exception {
        options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("a", "alive", false, "keep alive");
        cmdLine = new BasicParser().parse(options, argv);
        if (cmdLine.hasOption("alive")) {
            startCluster();
        } else {
            startAndShutdown();
        }
    }

    public static void main(String[] argv) throws Exception {
        MiniCluster minicluster = new MiniCluster();
        minicluster.start(argv);
    }

}
