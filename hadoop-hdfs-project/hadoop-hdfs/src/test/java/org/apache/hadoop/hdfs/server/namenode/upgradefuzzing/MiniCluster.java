
package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY;
import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_HTTP_ADDRESS_KEY;
import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_IPC_ADDRESS_KEY;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.Command;

import static org.apache.hadoop.fs.CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY;

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
    static String DFS_NAMENODE_HTTP_ADDRESS_KEY = "dfs.namenode.http-address";

    static final long seed = 0;
    static final short NUM_DATANODES = 1;
    static final int BLOCKSIZE = 1024;
    static final long txid = 1;

    private Options options;
    private CommandLine cmdLine;

    public static final String HDFS_MINIDFS_BASEDIR = "hdfs.minidfs.basedir";

    public static String miniclusterRoot = "/home/yayu/tmp/minicluster/minicluster-0";
    public static final int directoryMaxDepth = 3;
    public static final int suffixBound = 10;
    public static final int localFileLengthLimit = 10240;

    public void startCluster() throws IOException, InterruptedException {
        startCluster(miniclusterRoot);
    }

    public void startCluster(String root) throws IOException, InterruptedException {
        conf = new Configuration();
        // conf.set("hadoop.tmp.dir", "/home/yayu/tmp/hdfs-" + "0");
        FileUtils.deleteDirectory(new File(miniclusterRoot));
        conf.set(HDFS_MINIDFS_BASEDIR, miniclusterRoot);
        conf.set(DFS_DATANODE_ADDRESS_KEY, String.valueOf(dataNodePort));
        conf.set(DFS_DATANODE_IPC_ADDRESS_KEY, dataNodeIPCPort);
        conf.set(DFS_DATANODE_HTTP_ADDRESS_KEY, dataNodeHttpPort);
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(NUM_DATANODES);
        builder.format(true);
        builder.nameNodePort(nameNodePort);
        builder.nameNodeHttpPort(nameNodeHttpPort);
        builder.checkDataNodeAddrConfig(true);
        builder.checkDataNodeHostConfig(true);

        cluster = builder.build();
        cluster.waitActive();
        fsn = cluster.getNamesystem();
        hdfs = cluster.getFileSystem();
        // System.out.println("namenode port: " + cluster.getNameNodePort() + " " + cluster.getHttpUri(0));
        // System.out.println("datanode " + cluster.getDataNodes().get(0).getIpcPort() + " "
        //         + cluster.getDataNodes().get(0).getDataPort() + " " + cluster.getDataNodes().get(0).getRpcPort() + " "
        //         + cluster.getDataNodes().get(0).getDatanodeHostname());

        // Thread.sleep(10000);
        // shutDown();
    }

    public void startAndShutdown() throws Exception {
        conf = new Configuration();
        // conf.set("hadoop.tmp.dir", "/home/yayu/tmp/hdfs-" + "0");
        conf.set(HDFS_MINIDFS_BASEDIR, miniclusterRoot);
        conf.set(DFS_DATANODE_ADDRESS_KEY, String.valueOf(dataNodePort));
        conf.set(DFS_DATANODE_IPC_ADDRESS_KEY, dataNodeIPCPort);
        conf.set(DFS_DATANODE_HTTP_ADDRESS_KEY, dataNodeHttpPort);

        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(NUM_DATANODES);
        builder = builder.format(false);
        builder.nameNodePort(nameNodePort);
        builder.nameNodeHttpPort(nameNodeHttpPort);
        builder.checkDataNodeAddrConfig(true);
        builder.checkDataNodeHostConfig(true);
        cluster = builder.build();
        cluster.waitActive();
        fsn = cluster.getNamesystem();
        hdfs = cluster.getFileSystem();
        shutDown();
    }

    public void startRollingUpgrade(String basePath) throws Exception {
        Configuration conf = new Configuration();
        conf.set(HDFS_MINIDFS_BASEDIR, basePath);
        /* conf.set("hadoop.log.dir", basePath + "/logs"); */
        /* conf.set("hadoop.log.file", "hadoop-yayu-namenode-msi.log"); */
        /* conf.set("yarn.log.dir", basePath + "/logs"); */
        /* conf.set("yarn.log.file", "hadoop-yayu-namenode-msi.log"); */
        System.out.println("PID: " + ManagementFactory.getRuntimeMXBean().getName());
        System.out.println("directory: " + basePath);
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(NUM_DATANODES);
        builder.format(false);
        builder.checkExitOnShutdown(true);
        StartupOption operation = StartupOption.ROLLINGUPGRADE;
        operation.setRollingUpgradeStartupOption("started");
        builder = builder.startupOption(operation);
        // Thread.sleep(8000);
        // try {
        cluster = builder.build();
        cluster.waitActive();
        System.out.println("namenode port: " + cluster.getNameNodePort());
        System.out.println("datanode " + cluster.getDataNodes().get(0).getHttpPort());
        String localhost = "127.0.0.1";
        String namenodePort = localhost + ":" + cluster.getNameNodePort();
        String[] httpURI = cluster.getHttpUri(0).split(":");
        String namenodeHttpPort = localhost + ":" + httpURI[httpURI.length - 1];
        System.out.println("Get namenode Port: " + namenodePort + " " + namenodeHttpPort);
        conf.set(FS_DEFAULT_NAME_KEY, namenodePort);
        conf.set(DFS_NAMENODE_HTTP_ADDRESS_KEY, namenodeHttpPort);
        // String datanodePort = localhost + ":" + cluster.getDataNodes().get(0).getDataPort();
        // String datanodeIpcPort = localhost + ":" + cluster.getDataNodes().get(0).getIpcPort();

        StringBuilder commandLog = new StringBuilder();
        Integer commandIndex = 0;
        FsShell shell = null;
        CommandGenerator fsg = new CommandGenerator();
        for (int i = 0; i < 20; ++i) {
            try {
                Thread thread = new CommandThread(commandLog, fsg, conf, commandIndex);
                Long startTime = System.currentTimeMillis(), endTime;
                thread.start();
                thread.join(600000);
                if (thread.isAlive()) {
                    thread.interrupt();
                    commandLog.append("TIMEOUT");
                }
                endTime = System.currentTimeMillis();
                commandLog.append("\ntime usage: " + Double.toString((endTime - startTime) / 1000.) + "\n");
            } finally {
                if (shell != null) {
                    shell.close();
                }
            }
        }
        System.out.println("commands:\n" + commandLog);
        try {
            assertEquals(1, cluster.getNumNameNodes());
            assertEquals(1, cluster.getDataNodes().size());
        } catch (AssertionError e) {
            shutDown();
            // System.exit(1);
        }
        fsn = cluster.getNamesystem();
        hdfs = cluster.getFileSystem();
        if (cmdLine.hasOption("exit")) {
            // System.exit(0);
        }
        shutDown();
    }

    public void shutDown() {
        if (cluster != null) {
            cluster.shutdown();
            cluster = null;
        }
    }

    public void start(String[] argv) throws Exception {
        options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("a", "alive", false, "keep alive");
        options.addOption("r", "rollingupgrade", false, "Try load fsimage in old foramt");
        options.addOption("t", "test", false, "send fsshell command and test");
        options.addOption("p", "basePath", true, "hadoop base directory");
        options.addOption("c", "copy", false, "load a copied fsimage");
        options.addOption("exit", false, "exit 0 and don't wait shutdown");
        cmdLine = new BasicParser().parse(options, argv);
        if (cmdLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("FuzzingMiniCluster", options);
            return;
        }
        if (cmdLine.hasOption("test")) {
            FuzzingTest fz = new FuzzingTest();
            FuzzingTest.pretest();
            byte[] b = new byte[1024 * 32];
            new Random(System.currentTimeMillis()).nextBytes(b);
            fz.fuzzCommand(new ByteArrayInputStream(b));
            Thread.sleep(2000);
            fz.tearDown();
        } else if (cmdLine.hasOption("alive")) {
            miniclusterRoot = cmdLine.getOptionValue("basePath", miniclusterRoot);
            startCluster(miniclusterRoot);
        } else if (cmdLine.hasOption("rollingupgrade")) {
            String targetDir = cmdLine.getOptionValue("basePath", "/home/yayu/tmp/minicluster/minicluster-0");
            if (cmdLine.hasOption("copy")) {
                UUID uuid = UUID.randomUUID();
                File copyFile = new File("/home/yayu/tmp/copy/" + "minicluster-" + uuid.toString());
                if (!copyFile.getParentFile().exists()) {
                    copyFile.getParentFile().mkdirs();
                }
                System.out.println("target: " + targetDir + "\ncopy: " + copyFile.toString());
                FileUtils.copyDirectory(new File(targetDir), copyFile);
                startRollingUpgrade(copyFile.toString());
                FileUtils.deleteDirectory(copyFile);
            } else {
                startRollingUpgrade(targetDir);
            }
        } else {
            startAndShutdown();
        }
    }

    public void mkdirs(Path dir) throws IOException {
        hdfs.mkdirs(dir);
    }

    public void mkdirs(String dir) throws IllegalArgumentException, IOException {
        hdfs.mkdirs(new Path(dir));
    }

    public static void main(String[] argv) throws Exception {
        MiniCluster minicluster = new MiniCluster();
        minicluster.start(argv);
    }

    private final class CommandThread extends Thread {
        private StringBuilder commandLog;
        private final CommandGenerator fsg;
        private final Configuration conf;
        private Integer commandIndex;

        private CommandThread(StringBuilder commandLog, CommandGenerator fsg, Configuration conf,
                Integer commandIndex) {
            this.commandLog = commandLog;
            this.fsg = fsg;
            this.conf = conf;
            this.commandIndex = commandIndex;
        }

        @Override
        public void run() {
            try {
                Command cmd;
                cmd = fsg.generate();
                String cmdString = String.join(" ", cmd.generate());
                synchronized (commandLog) {
                    commandLog.append("CMD " + Integer.toString(++commandIndex) + ":\n" + cmdString + "\nresult: ");
                }
                int res = cmd.execute(conf);
                synchronized (commandLog) {
                    commandLog.append(Integer.toString(res));
                }
                // System.out.println("CMD " + Integer.toString(commandIndex) + ":\n" + cmdString + "\nresult: "
                //         + Integer.toString(res) + "\n");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static class PrepareLocalSource {
        static String localPrefix = FuzzingTest.localResource;
        static Random rnd = new Random(System.currentTimeMillis());

        public static String generateLocalFile() {
            String filePath = generateLocalDir() + "file" + Integer.toString(rnd.nextInt(suffixBound));
            return filePath;
        }

        public static String generateLocalDir() {
            String dirPath = localPrefix;
            int depth = 0;
            while (rnd.nextBoolean() && ++depth < directoryMaxDepth) {
                dirPath += "dir" + Integer.toString(rnd.nextInt(suffixBound)) + "/";
            }
            return dirPath;
        }

        public static void createLocalFile(String filePath) {
            try {
                File file = new File(filePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
                Integer fileLength = rnd.nextInt(localFileLengthLimit);
                byte[] content = new byte[fileLength];
                new Random().nextBytes(content);
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(content);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public static void generateLocalSnapshot() throws IOException {
            File localSnapshotDir = new File(localPrefix);
            File localSnapshotCopyDir = new File(FuzzingTest.localResourceCopy);
            if (localSnapshotDir.exists()) {
                FileUtils.deleteDirectory(localSnapshotDir);
            }
            if (localSnapshotCopyDir.exists()) {
                FileUtils.deleteDirectory(localSnapshotCopyDir);
            }
            localSnapshotDir.mkdirs();
            Integer fileNum = rnd.nextInt(16);
            for (int i = 0; i < fileNum; ++i) {
                String filePath = generateLocalFile();
                createLocalFile(filePath);
            }
            FileUtils.copyDirectory(localSnapshotDir, localSnapshotCopyDir);
        }

    }
}
