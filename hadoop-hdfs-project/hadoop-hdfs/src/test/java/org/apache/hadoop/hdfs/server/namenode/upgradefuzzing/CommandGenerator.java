package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.Command;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.CommandsEnum;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSAdminCommands.allowSnapshotCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSAdminCommands.disallowSnapshotCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSAdminCommands.rollEditsCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSAdminCommands.safemodeCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSAdminCommands.saveNamespaceCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.appendToFileCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.copyFromLocalCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.copyToLocalCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.cpCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.createSnapshotCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.deleteSnapshotCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.getCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.getmergeCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.mkdirCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.moveFromLocalCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.mvCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.putCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.rmCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.rmdirCommand;

public class CommandGenerator {
    RandomSource rnd;

    CommandGenerator(InputStream is) throws IOException {
        this.rnd = new RandomSource(is);
    }

    public Command generate() throws Exception {
        CommandsEnum cmdEnum = CommandsEnum.values()[rnd.nextInt(CommandsEnum.values().length)];
        Command cmd = null;
        switch (cmdEnum) {
        case safemode: {
            cmd = new safemodeCommand(rnd);
            break;
        }
        case rollEdits: {
            cmd = new rollEditsCommand(rnd);
            break;
        }
        case saveNamespace: {
            cmd = new saveNamespaceCommand(rnd);
            break;
        }
        case allowSnapshot: {
            cmd = new allowSnapshotCommand(rnd);
            break;
        }
        case disallowSnapshot: {
            cmd = new disallowSnapshotCommand(rnd);
            break;
        }
        case get: {
            cmd = new getCommand(rnd);
            break;
        }
        case put: {
            cmd = new putCommand(rnd);
            break;
        }
        case mkdir: {
            cmd = new mkdirCommand(rnd);
            break;
        }
        case rm: {
            cmd = new rmCommand(rnd);
            break;
        }
        case cp: {
            cmd = new cpCommand(rnd);
            break;
        }
        case mv: {
            cmd = new mvCommand(rnd);
            break;
        }
        case getmerge: {
            cmd = new getmergeCommand(rnd);
            break;
        }
        case appendToFile: {
            cmd = new appendToFileCommand(rnd);
            break;
        }
        case moveFromLocal: {
            cmd = new moveFromLocalCommand(rnd);
            break;
        }
        // case moveToLocal: {
        // cmd = new moveToLocalCommand(rnd);
        // break;
        // }
        case copyFromLocal: {
            cmd = new copyFromLocalCommand(rnd);
            break;
        }
        case copyToLocal: {
            cmd = new copyToLocalCommand(rnd);
            break;
        }
        case rmdir: {
            cmd = new rmdirCommand(rnd);
            break;
        }
        case createSnapshot: {
            cmd = new createSnapshotCommand(rnd);
            break;
        }
        case deleteSnapshot: {
            cmd = new deleteSnapshotCommand(rnd);
            break;
        }
        default: {
            throw new Exception("unsupported command");
        }
        }
        return cmd;
    }
}
