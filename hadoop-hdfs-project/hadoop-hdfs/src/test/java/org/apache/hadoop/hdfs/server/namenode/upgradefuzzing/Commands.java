package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.util.ToolRunner;

public class Commands {

    static Integer directoryMaxDepth = 3;
    static Integer suffixBound = 10;
    static String localPrefix = FuzzingTest.localResource;
    static int localFileLengthLimit = 1024;

    public enum CommandsEnum {
        appendToFile,
        // cat,
        // checksum,
        // chgrp,
        // chmod,
        // chown,
        copyFromLocal, copyToLocal,
        // count,
        cp, createSnapshot, deleteSnapshot,
        // df,
        // du,
        // expunge,
        // find,
        get,
        // getfacl,
        // getfattr,
        getmerge,
        // head,
        // help,
        // ls,
        mkdir, moveFromLocal,
        // moveToLocal,
        mv, put,
        // renameSnapshot,
        rm, rmdir,
        // setfacl,
        // setfattr,
        // setrep,
        // stat,
        // tail,
        // test,
        // text,
        // touch,
        // touchz,
        // truncate,
        // usage,
        //
        // report,
        safemode, saveNamespace, rollEdits,
        // restoreFailedStorage,
        // refreshNodes,
        // setQuota,
        // clrQuota,
        // setSpaceQuota,
        // clrSpaceQuota,
        // finalizeUpgrade,
        // rollingUpgrade,
        // upgrade,
        // refreshServiceAcl,
        // refreshUserToGroupsMappings,
        // refreshSuperUserGroupsConfiguration,
        // refreshCallQueue,
        // refresh,
        // reconfig,
        // printTopology,
        // refreshNamenodes,
        // getVolumeReport,
        // deleteBlockPool,
        // setBalancerBandwidth,
        // getBalancerBandwidth,
        // fetchImage,
        allowSnapshot, disallowSnapshot,
        // shutdownDatanode,
        // evictWriters,
        // getDatanodeInfo,
        // metasave,
        // triggerBlockReport,
        // listOpenFiles,
    }

    public static abstract class DFSAdminCommand extends Command {

        DFSAdminCommand(RandomSource rand) {
            super(rand);
        }

        public Integer execute() throws Exception {
            return execute(new HdfsConfiguration());
        }

        public Integer execute(Configuration conf) throws Exception {
            String[] argv = generate();
            // FileWriter fw = new FileWriter("upgradefuzz.log", true);
            // fw.write(this.toString() + " result: " );
            // fw.flush();
            int res = ToolRunner.run(new DFSAdmin(conf), argv);
            // fw.write(Integer.toString(res) + "\n");
            // fw.close();
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
            String[] argv = generate();
            // FileWriter fw = new FileWriter("upgradefuzz.log", true);
            // fw.write(this.toString() + " result: " );
            // fw.flush();
            FsShell shell = new FsShell();
            shell.setConf(conf);
            int res = ToolRunner.run(shell, argv);
            // fw.write(Integer.toString(res) + "\n");
            // fw.close();
            return res;
        }
    }

    public static abstract class Command {
        RandomSource rnd;
        List<String> commands = new ArrayList<>();
        String cmd;
        String[] options;

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

        public void generateInternal() {
        }

        public abstract Integer execute() throws Exception;

        public abstract Integer execute(Configuration conf) throws Exception;

        @Override
        public String toString() {
            return String.join(" ", commands);
        }

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
            int depth = 0;
            while (rnd.nextBoolean() && ++depth < directoryMaxDepth) {
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
            // try {
            // File file = new File(filePath);
            // file.getParentFile().mkdirs();
            // file.createNewFile();
            // Integer fileLength = rnd.nextInt(localFileLengthLimit);
            // byte[] content = new byte[fileLength];
            // new Random().nextBytes(content);
            // FileOutputStream fos = new FileOutputStream(filePath);
            // fos.write(content);
            // fos.close();
            // } catch (IOException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            return filePath;
        }

        public String generateLocalDir() {
            String dirPath = localPrefix;
            int depth = 0;
            while (rnd.nextBoolean() && ++depth < directoryMaxDepth) {
                dirPath += "dir" + Integer.toString(rnd.nextInt(suffixBound)) + "/";
            }
            // File dirFile = new File(dirPath);
            // if (!dirFile.exists()) {
            // dirFile.mkdirs();
            // }
            return dirPath;
        }

        public void createLocalFile(String filePath) {
            try {
                File file = new File(filePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
                // Integer fileLength = rnd.nextInt(localFileLengthLimit);
                // byte[] content = new byte[fileLength];
                // new Random().nextBytes(content);
                byte[] content = rnd.nextBytes(localFileLengthLimit);
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(content);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static class PrepareLocalSource {
        static String localPrefix = FuzzingTest.localResource;
        static Random rnd = new Random();

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
            for (int i = 0; i < 256; ++i) {
                String filePath = generateLocalFile();
                createLocalFile(filePath);
            }
            FileUtils.copyDirectory(localSnapshotDir, localSnapshotCopyDir);
        }

    }
}
