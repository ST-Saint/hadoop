package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.util.ToolRunner;

public class Commands {

    public static abstract class DFSAdminCommand extends Command {

        DFSAdminCommand(RandomSource rand) {
            super(rand);
        }

        public Integer execute() throws Exception {
            return execute(new HdfsConfiguration());
        }

        public Integer execute(Configuration conf) throws Exception {
            String[] argv = commands.toArray(new String[0]);
            int res = ToolRunner.run(new DFSAdmin(conf), argv);
            return res;
        }
    }

    public static abstract class DFSCommand extends Command {
        DFSCommand(RandomSource rand) {
            super(rand);
        }

        public Integer execute() throws Exception {
            return execute(new Configuration());
        }

        public Integer execute(Configuration conf) throws Exception {
            FsShell shell = new FsShell();
            shell.setConf(conf);
            String[] argv = commands.toArray(new String[0]);
            FileWriter fw = new FileWriter("upgradefuzz.log", true);
            fw.write(String.join(" ", cmd) + "\n");
            fw.close();
            int res = ToolRunner.run(shell, argv);
            return res;
        }
    }

    public static abstract class Command {
        RandomSource rnd;
        List<String> commands = new ArrayList<>();
        String cmd;
        String[] options;
        Integer suffixBound = 10;
        String localPrefix = "/home/yayu/tmp/localsrc/";
        int localFileLengthLimit = 1024;

        public Command(RandomSource rand) {
            rnd = rand;
        }

        public String[] generate() {
            generateOptions();
            generateInternal();
            return commands.toArray(new String[0]);
        }

        public void generateOptions() {
            commands.add(cmd);
            generateFlags();
        }

        public void generateFlags() {
            for (int i = 0; i < options.length; i++) {
                if (rnd.nextInt(options.length + 1) == 0) {
                    commands.add(options[i]);
                }
            }
        }

        public void add(String str) {
            commands.add(str);
        }

        public abstract void generateInternal();

        public abstract Integer execute() throws Exception;

        public abstract Integer execute(Configuration conf) throws Exception;

        public String generateHdfsPath() {
            if (rnd.nextBoolean()) {
                return generateHdfsFile();
            } else {
                return generateHdfsDir();
            }
        }

        public String generateHdfsFile() {
            String filePath = generateHdfsDir() + "file" + Integer.toString(rnd.nextInt(suffixBound));
            return filePath;
        }

        public String generateHdfsDir() {
            String dirPath = "/";
            while (rnd.nextBoolean()) {
                dirPath += "dir" + Integer.toString(rnd.nextInt(suffixBound)) + "/";
            }
            return dirPath;
        }

        public String generateLocalPath() {
            if (rnd.nextBoolean()) {
                return generateLocalFile();
            } else {
                return generateLocalDir();
            }
        }

        public String generateLocalFile() {
            String filePath = generateLocalDir() + "file" + Integer.toString(rnd.nextInt(suffixBound));
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
            return filePath;
        }

        public String generateLocalDir() {
            String dirPath = localPrefix;
            while (rnd.nextBoolean()) {
                dirPath += "dir" + Integer.toString(rnd.nextInt(suffixBound)) + "/";
            }
            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            return dirPath;
        }

    }
}
