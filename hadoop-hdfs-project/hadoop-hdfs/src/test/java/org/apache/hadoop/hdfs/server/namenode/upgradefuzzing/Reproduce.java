package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

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
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSCluster.Builder;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.Command;
import static org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.FuzzingUtils.*;

public class Reproduce {

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

    public void loadCommands(String id) throws IOException {
        commands = new ArrayList<>();
        File logFile = new File(logPath.toString(), "upgradefuzz-" + id + ".log");
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        String cmd, result;
        while ((cmd = reader.readLine()) != null) {
            result = reader.readLine();
            commands.add(new Command(cmd.split(" ")));
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
                System.out.println("min: " + min);
                System.out.println("max: " + max);
                System.out.println(clusterID + " in range: " + (index >= min && index <= max));
            }
        }
        File targetResrc = new File(resourcePath, "localsrc");
        if (targetResrc.exists()) {
            FileUtils.deleteDirectory(targetResrc);
        }
        FileUtils.copyDirectory(backupResrc, targetResrc);
    }

    public void replayCommands() throws Exception {
        cluster = builder.build();
        cluster.waitActive();
        fsn = cluster.getNamesystem();
        hdfs = cluster.getFileSystem();
        FsShell shell = new FsShell();
        shell.setConf(conf);
        for (int i = 0; i < commands.size(); ++i) {
            Command cmd = commands.get(i);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        int res = cmd.execute(conf);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            thread.join(10000);
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        cluster.shutdown();

    }

    private void loadOnNewVersion() throws IOException {
        String targetDfsDir = "/home/yayu/tmp/minicluster/minicluster-0";
        Integer exitCode = systemExecute("timeout " + Integer.toString(20) + " ./fuzz_reload.sh " + targetDfsDir + " > "
                + targetDfsDir + "/log.txt 2>&1", new File(hadoopNewVerPath));
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

        conf = new Configuration();
        builder = new MiniDFSCluster.Builder(conf).numDataNodes(1);
        StartupOption operation = StartupOption.ROLLINGUPGRADE;
        operation.setRollingUpgradeStartupOption("started");
        builder = builder.startupOption(operation);
        try {
            replayCommands();
            loadOnNewVersion();
        } catch (Exception e) {
            System.out.println("tostring:\n" + e.toString() + "\nmessage:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] argv) throws Exception {
        new Reproduce().reproduce(argv);
    }
}
