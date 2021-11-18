
package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY;
import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_HTTP_ADDRESS_KEY;
import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_IPC_ADDRESS_KEY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;

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
        // Thread.sleep(10000);
        // shutDown();
    }

    public void startAndShutdown() throws Exception{
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
        shutDown();
    }

    public void startRollingUpgrade(String basePath){
        conf = new Configuration();
        if( basePath==null ){
            conf.set(HDFS_MINIDFS_BASEDIR, "/home/yayu/tmp/minicluster-0");
        }else{
            conf.set(HDFS_MINIDFS_BASEDIR, basePath);
        }

        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(NUM_DATANODES);
        builder = builder.format(false);
        StartupOption operation = StartupOption.ROLLINGUPGRADE;
        operation.setRollingUpgradeStartupOption("started");
        builder = builder.startupOption(operation);
        try {
          cluster = builder.build();
          cluster.waitActive();
          // System.exit(0);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        shutDown();
    }

    public void shutDown(){
        if( cluster!=null ){
            cluster.shutdown();
            cluster=null;
        }
    }

    public void start(String[] argv) throws Exception{
        options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("a", "alive", false, "keep alive");
        options.addOption("r", "rollingupgrade", false, "Try load fsimage in old foramt");
        options.addOption("t", "test", false, "send fsshell command and test");
        cmdLine = new BasicParser().parse(options, argv);
        if( cmdLine.hasOption("test")  ){
            FuzzingTest fz = new FuzzingTest();
            FuzzingTest.pretest();
            byte[] b = new byte[1024*32];
            new Random(System.currentTimeMillis()).nextBytes(b);
            fz.fuzzCommand(new ByteArrayInputStream(b));
            Thread.sleep(2000);
            fz.tearDown();
        }
        else if(cmdLine.hasOption("alive")){
            startCluster();
        }else if( cmdLine.hasOption("rollingupgrade") ){
            startRollingUpgrade(null);
        }
        else{
            startAndShutdown();
        }
    }

    public static void main(String[] argv) throws Exception {
        MiniCluster minicluster = new MiniCluster();
        minicluster.start(argv);
    }
}
