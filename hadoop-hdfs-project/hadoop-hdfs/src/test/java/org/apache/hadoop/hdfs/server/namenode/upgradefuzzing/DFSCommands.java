package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.DFSCommand;

public class DFSCommands {

    public enum DFSCommandEnum {
        appendToFile,
        // cat,
        // checksum,
        // chgrp,
        // chmod,
        // chown,
        copyFromLocal, copyToLocal,
        // count,
        cp,
        // createSnapshot,
        // deleteSnapshot,
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
    }

    public static class getmergeCommand extends DFSCommand {
        public getmergeCommand(RandomSource rand) {
            super(rand);
            cmd = "-getmerge";
            options = new String[] { "-nl", "-skip-empty-file" };
        }

        @Override
        public void generateInternal() {
            add(generateHdfsPath());
            add(generateLocalFile());
        }
    }

    public static class copyToLocalCommand extends DFSCommand {
        copyToLocalCommand(RandomSource rand) {
            super(rand);
            cmd = "-copyToLocal";
            options = new String[] { "-f", "-p", "-ignoreCrc", "-crc" };
        }

        @Override
        public void generateInternal() {
            add(generateHdfsPath());
            add(generateLocalPath());
        }
    }

    public static class moveToLocalCommand extends DFSCommand {
        moveToLocalCommand(RandomSource rand) {
            super(rand);
            cmd = "-moveToLocal";
            options = new String[0];
        }

        @Override
        public void generateInternal() {
            add(generateHdfsPath());
            add(generateLocalPath());
        }
    }

    public static class copyFromLocalCommand extends DFSCommand {
        copyFromLocalCommand(RandomSource rand) {
            super(rand);
            cmd = "-copyFromLocal";
            options = new String[] { "-f", "-p", "-l", "-d", "-t", "4" };
        }

        @Override
        public void generateInternal() {
            add(generateLocalPath());
            add(generateHdfsPath());
        }
    }

    public static class moveFromLocalCommand extends DFSCommand {
        moveFromLocalCommand(RandomSource rand) {
            super(rand);
            cmd = "-moveFromLocal";
            options = new String[] { "-f", "-p", "-l", "-d" };
        }

        @Override
        public void generateInternal() {
            add(generateLocalPath());
            add(generateHdfsPath());
        }
    }

    public static class appendToFileCommand extends DFSCommand {
        appendToFileCommand(RandomSource rand) {
            super(rand);
            cmd = "-appendToFile";
            options = new String[0];
        }

        @Override
        public void generateInternal() {
            add(generateLocalFile());
            add(generateHdfsFile());
        }
    }

    public static class cpCommand extends DFSCommand {
        cpCommand(RandomSource rand) {
            super(rand);
            cmd = "-cp";
            options = new String[] { "-f", "-p", "-ptopax", "-d" };
        }

        @Override
        public void generateInternal() {
            add(generateHdfsPath());
            add(generateHdfsPath());
        }
    }

    public static class putCommand extends DFSCommand {
        putCommand(RandomSource rand) {
            super(rand);
            cmd = "-put";
            options = new String[] { "-f", "-p", "-l", "-d" };
        }

        @Override
        public void generateInternal() {
            add(generateLocalPath());
            add(generateHdfsPath());
        }
    }

    public static class mkdirCommand extends DFSCommand {
        mkdirCommand(RandomSource rand) {
            super(rand);
            cmd = "-mkdir";
            options = new String[] { "-p" };
        }

        @Override
        public void generateInternal() {
            add(generateHdfsDir());
        }
    }

    public static class getCommand extends DFSCommand {
        getCommand(RandomSource rand) {
            super(rand);
            cmd = "-get";
            options = new String[] { "-f", "-p", "-ignoreCrc", "-crc" };
        }

        @Override
        public void generateInternal() {
            add(generateHdfsPath());
            add(generateLocalPath());
        }
    }

    public static class mvCommand extends DFSCommand {
        mvCommand(RandomSource rand) {
            super(rand);
            cmd = "-mv";
            options = new String[0];
        }

        @Override
        public void generateInternal() {
            add(generateHdfsPath());
            add(generateHdfsPath());
        }
    }

    public static class rmCommand extends DFSCommand {
        rmCommand(RandomSource rand) {
            super(rand);
            cmd = "-rm";
            options = new String[] { "-f", "-r", "-R", "-skipTrash", "-safely" };
        }

        @Override
        public void generateInternal() {
            add(generateHdfsPath());
        }
    }

    public static class rmdirCommand extends DFSCommand {
        rmdirCommand(RandomSource rand) {
            super(rand);
            cmd = "-rmdir";
            options = new String[] { "--ignore-fail-on-non-empty" };
        }

        @Override
        public void generateInternal() {
            add(generateHdfsPath());
        }
    }

}
