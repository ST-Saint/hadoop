package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.Command;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.Commands.DFSCommand;
import org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.DFSCommands.*;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.util.ToolRunner;

public class CommandGenerator {
    RandomSource rnd;

    CommandGenerator(InputStream is) throws IOException {
        this.rnd = new RandomSource(is);
    }

    public Command generate() throws Exception {
        DFSCommandEnum cmdEnum = DFSCommandEnum.values()[rnd.nextInt(DFSCommandEnum.values().length)];
        Command cmd = null;
        switch (cmdEnum) {
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
        default: {
            throw new Exception("unsupported command");
        }
        }
        return cmd;
    }
}
