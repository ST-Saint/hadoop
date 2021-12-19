package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.DFSAdminCommand;

public class DFSAdminCommands {

    public static class saveNamespaceCommand extends DFSAdminCommand {
        saveNamespaceCommand(RandomSource rand) {
            super(rand);
            cmd = "-saveNamespace";
            options = new String[0];
        }
    }

    public static class rollEditsCommand extends DFSAdminCommand {
        rollEditsCommand(RandomSource rand) {
            super(rand);
            cmd = "-rollEdits";
            options = new String[0];
        }
    }

    public static class safemodeCommand extends DFSAdminCommand {
        safemodeCommand(RandomSource rand) {
            super(rand);
            cmd = "-safemode";
            options = new String[] { "enter", "leave", "get", /*"wait",*/ "forceExit" };
        }

        @Override
        public void generateFlags() {
            int idx = rnd.nextInt(options.length);
            commands.add(options[idx]);
        }
    }

    public static class allowSnapshotCommand extends DFSAdminCommand {
        allowSnapshotCommand(RandomSource rand) {
            super(rand);
            cmd = "-allowSnapshot";
            options = new String[0];
        }

        @Override
        public void generateInternal() {
            add(generateHdfsDir());
        }
    }

    public static class disallowSnapshotCommand extends DFSAdminCommand {
        disallowSnapshotCommand(RandomSource rand) {
            super(rand);
            cmd = "-disallowSnapshot";
            options = new String[0];
        }

        @Override
        public void generateInternal() {
            add(generateHdfsDir());
        }
    }
}
