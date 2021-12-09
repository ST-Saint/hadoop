package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY;
import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_HTTP_ADDRESS_KEY;
import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_DATANODE_IPC_ADDRESS_KEY;
import static org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.FuzzingUtils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSCluster.Builder;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.Command;
import org.apache.hadoop.test.GenericTestUtils;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

public class Reproduce {

    public static final String HDFS_MINIDFS_BASEDIR = "hdfs.minidfs.basedir";
    public static final String miniclusterRoot = "/home/yayu/tmp/minicluster/minicluster-0";
    public static final String miniclusterRepro = "/home/yayu/tmp/minicluster/minicluster-repro";
    private int nameNodePort = 10240;
    private int nameNodeHttpPort = 10241;
    private String dataNodePort = "127.0.0.1:10242";
    private String dataNodeIPCPort = "127.0.0.1:10243";
    private String dataNodeHttpPort = "127.0.0.1:10244";
    private static Path workdir = new Path("/workdir");

    private static Options options;
    private static CommandLine cmdLine;
    private static MiniDFSCluster cluster;
    private static FSNamesystem fsn;
    private static DistributedFileSystem hdfs;
    private static File logPath = FuzzingTest.logPath;
    private static File resourcePath = FuzzingTest.resourcePath;
    private List<Command> commands;
    private Configuration conf;
    private Builder builder;
    private static String hadoopNewVerPath = FuzzingTest.hadoopNewVerPath;

    static {
        // SnapshotTestHelper.disableLogs();
        // GenericTestUtils.disableLog(LoggerFactory.getLogger(NameNode.class));
        LogManager.getRootLogger().setLevel(Level.OFF);
    }

    public void loadCommands(String id) throws IOException {
        commands = new ArrayList<>();
        File logFile = new File(logPath.toString(), "upgradefuzz-" + id + ".log");
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        String index, cmd, result, time;
        while ((index = reader.readLine()) != null) {
            cmd = reader.readLine();
            result = reader.readLine();
            time = reader.readLine();
            // if (result.equals("result: 0")) {
            commands.add(Command.parseCommand(cmd.split(" ")));
            // }
        }
        String currentLine = reader.readLine();
        reader.close();
    }

    private void loadLocalResource(String clusterID) throws IOException {
        File backupResrc = null;
        Pattern p = Pattern.compile("localsrc\\[(\\d+)-(\\d+)\\]");
        Long index = Long.valueOf(clusterID);
        for (File f : resourcePath.listFiles()) {
            Matcher m = p.matcher(f.getName());
            if (m.find()) {
                Long min = Long.valueOf(m.group(1)), max = Long.valueOf(m.group(2));
                // System.out.println(clusterID + " in range: " + (index >= min && index <=
                // max));
                if ((index >= min && index <= max)) {
                    System.out.println(f.getAbsolutePath());
                    backupResrc = f;
                    break;
                }
            }
        }
        if (backupResrc == null) {
            new IllegalArgumentException().printStackTrace();
            System.exit(0);
        }
        File targetResrc = new File(resourcePath, "localsrc");
        if (targetResrc.exists()) {
            FileUtils.deleteDirectory(targetResrc);
        }
        FileUtils.copyDirectory(backupResrc, targetResrc);
    }

    static String commandLog = "";
    static Integer commandIndex = 0;

    public void replayCommands() throws Exception {
        Integer fuzzingCount = 1, cmdCount = 0;
        for (int i = 0; i < commands.size(); ++i) {

            if (cmdCount.equals(0)) {
                hdfs.mkdirs(new Path(workdir, "subdir" + Integer.toString(++fuzzingCount)));
            }
            cmdCount = (cmdCount + 1) % 20;

            final Command cmd = commands.get(i);
            Thread thread = new Thread() {
                Command _cmd = cmd;

                @Override
                public void run() {
                    try {
                        String cmdString = _cmd.toString();
                        commandLog += "CMD " + Integer.toString(++commandIndex) + ":\n" + cmdString + "\nresult: ";
                        int res = _cmd.execute(conf);
                        commandLog += Integer.toString(res);
                        System.out.println(_cmd.toString() + "\n" + res);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            Long startTime = System.currentTimeMillis(), endTime;
            thread.start();
            thread.join(20000);
            if (thread.isAlive()) {
                thread.interrupt();
                commandLog += "TIMEOUT";
            }
            endTime = System.currentTimeMillis();
            commandLog += "\ntime usage: " + Double.toString((endTime - startTime) / 1000.) + "\n";
            Thread.sleep(1000);
        }
        System.out.println(commandLog);
        Thread.sleep(1000);
        FileUtils.deleteDirectory(new File(miniclusterRepro));
        FileUtils.copyDirectory(new File(miniclusterRoot), new File(miniclusterRepro));
        Thread.sleep(1000);
        cluster.shutdown();
    }

    private void loadOnNewVersion() throws IOException {
        System.out.println("\n----------------------------\n\nload on new version");
        // String targetDfsDir = "/home/yayu/tmp/minicluster/minicluster-0";
        // Integer exitCode = systemExecute("timeout " + Integer.toString(20) + "
        // ./fuzz_reload.sh " + targetDfsDir + " > "
        // + targetDfsDir + "/log.txt 2>&1", new File(hadoopNewVerPath));
    }

    private void setConfiguration() {
        conf = new Configuration();
        conf.set(HDFS_MINIDFS_BASEDIR, miniclusterRoot);
        conf.set(DFS_DATANODE_ADDRESS_KEY, String.valueOf(dataNodePort));
        conf.set(DFS_DATANODE_IPC_ADDRESS_KEY, dataNodeIPCPort);
        conf.set(DFS_DATANODE_HTTP_ADDRESS_KEY, dataNodeHttpPort);
    }

    private void startup() throws IOException {
        FileUtils.deleteDirectory(new File(miniclusterRoot));
        setConfiguration();
        builder = new MiniDFSCluster.Builder(conf).numDataNodes(1);
        builder.nameNodePort(nameNodePort);
        builder.nameNodeHttpPort(nameNodeHttpPort);
        builder.checkDataNodeAddrConfig(true);
        builder.checkDataNodeHostConfig(true);
        StartupOption operation = StartupOption.ROLLINGUPGRADE;
        operation.setRollingUpgradeStartupOption("started");
        builder.startupOption(operation);
        cluster = builder.build();
        cluster.waitActive();
        fsn = cluster.getNamesystem();
        hdfs = cluster.getFileSystem();
        hdfs.mkdirs(new Path("/workdir"));
    }

    private void teardown() {
        if (cluster != null) {
            cluster.shutdown();
        }
    }

    public void reproduce(String[] argv) throws Exception {
        options = new Options();
        options.addOption("h", "help", false, "print this message");
        Option idOption = OptionBuilder.isRequired().hasArg(true).create("id");
        options.addOption(idOption);
        // options.addOption("id", true, "minicluster uuid");
        cmdLine = new BasicParser().parse(options, argv);
        if (cmdLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("FuzzingMiniCluster", options);
            return;
        }
        String clusterID = cmdLine.getOptionValue("id");

        loadCommands(clusterID);
        loadLocalResource(clusterID);
        try {
            startup();
            replayCommands();
            loadOnNewVersion();
        } catch (Exception e) {
            System.out.println("tostring:\n" + e.toString() + "\nmessage:\n" + e.getMessage());
            e.printStackTrace();
            teardown();
        }
    }

    public static void main(String[] argv) throws Exception {
        new Reproduce().reproduce(argv);
    }
}
