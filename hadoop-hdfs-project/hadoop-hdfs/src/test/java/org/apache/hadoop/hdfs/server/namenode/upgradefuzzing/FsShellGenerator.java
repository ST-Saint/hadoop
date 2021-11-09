package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FsShellGenerator {
    Random rnd;

    FsShellGenerator(Random rand) {
        this.rnd = rand;
    }

    public String[] generate() throws Exception {
        dfsCommand cmdEnum = dfsCommand.values()[rnd.nextInt(dfsCommand.values().length)];
        Command cmd = null;
        switch (cmdEnum) {
        case get: {
            cmd = new getCommand();
            break;
        }
        case put: {
            cmd = new putCommand();
            break;
        }
        case mkdir: {
            cmd = new mkdirCommand();
            break;
        }
        case rm: {
            cmd = new rmCommand();
            break;
        }
        case mv: {
            cmd = new mvCommand();
            break;
        }
        default: {
            throw new Exception("unsupported command");
        }
        }
        return cmd.generate();
    }

    public enum dfsCommand {
        // appendToFile,
        // cat,
        // checksum,
        // chgrp,
        // chmod,
        // chown,
        // copyFromLocal,
        // copyToLocal,
        // count,
        // cp,
        // createSnapshot,
        // deleteSnapshot,
        // df,
        // du,
        // expunge,
        // find,
        get,
        // getfacl,
        // getfattr,
        // getmerge,
        // head,
        // help,
        // ls,
        mkdir,
        // moveFromLocal,
        // moveToLocal,
        mv, put,
        // renameSnapshot,
        rm,
        // rmdir,
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
    }

    public abstract class Command {
        List<String> commands = new ArrayList<>();
        String cmd;
        String[] options;
        Integer suffixBound = 10;
        String localPrefix = "/home/yayu/tmp/localsrc/";
        int localFileLengthLimit = 1024;

        public String[] generate() {
            generateOptions();
            generateInternal();
            return commands.toArray(new String[0]);
        }

        public void generateOptions() {
            commands.add(cmd);
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

        public String generatePath() {
            if (rnd.nextBoolean()) {
                return generateDstFile();
            } else {
                return generateDstDir();
            }
        }

        public String generateDstFile() {
            String filePath = generateDstDir() + "file" + Integer.toString(rnd.nextInt(suffixBound));
            return filePath;
        }

        public String generateDstDir() {
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

    public class putCommand extends Command {
        putCommand() {
            cmd = "-put";
            options = new String[] { "-f", "-p", "-l", "-d" };
        }

        @Override
        public void generateInternal() {
            add(generateLocalPath());
            add(generatePath());
        }
    }

    public class mkdirCommand extends Command {
        mkdirCommand() {
            cmd = "-mkdir";
            options = new String[] { "-p" };
        }

        @Override
        public void generateInternal() {
            add(generateDstDir());
        }
    }

    public class getCommand extends Command {
        getCommand() {
            cmd = "-get";
            options = new String[] { "-f", "-p", "-ignoreCrc", "-crc" };
        }

        @Override
        public void generateInternal() {
            add(generatePath());
            add(generateLocalPath());
        }
    }

    public class mvCommand extends Command {
        mvCommand() {
            cmd = "-mv";
            options = new String[0];
        }

        @Override
        public void generateInternal() {
            add(generatePath());
            add(generatePath());
        }
    }

    public class rmCommand extends Command {
        rmCommand() {
            cmd = "-rm";
            options = new String[] { "-f", "-r", "-R", "-skipTrash", "-safety" };
        }

        @Override
        public void generateInternal() {
            add(generatePath());
        }
    }

}
