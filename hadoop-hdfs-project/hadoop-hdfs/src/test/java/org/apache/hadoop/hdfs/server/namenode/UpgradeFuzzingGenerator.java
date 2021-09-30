package org.apache.hadoop.hdfs.server.namenode;

import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.namenode.UpgradeFuzzingTest.HadoopStartupWrapper;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;

import static org.apache.hadoop.hdfs.DFSConfigKeys.*;
import static org.apache.hadoop.hdfs.server.common.Util.fileAsURI;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NamenodeRole;

public class UpgradeFuzzingGenerator extends Generator<HadoopStartupWrapper> {
    public UpgradeFuzzingGenerator() {
        super(HadoopStartupWrapper.class); // Register the type of objects that we can create
    }

    private Configuration getConf() throws IOException {
        String baseDir = MiniDFSCluster.getBaseDirectory();

        String nameDirs = fileAsURI(new File(baseDir, "name1")) + "," + fileAsURI(new File(baseDir, "name2"));

        Configuration conf = new HdfsConfiguration();
        FileSystem.setDefaultUri(conf, "hdfs://localhost:0");
        conf.set(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "0.0.0.0:0");
        conf.set(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY, nameDirs);
        conf.set(DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_KEY, nameDirs);
        conf.set(DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, "0.0.0.0:0");
        conf.setBoolean(DFSConfigKeys.DFS_PERMISSIONS_ENABLED_KEY, false);
        return conf;
    }

    private static void checkConfiguration(Configuration conf) throws IOException {

        final Collection<URI> namespaceDirs = FSNamesystem.getNamespaceDirs(conf);
        final Collection<URI> editsDirs = FSNamesystem.getNamespaceEditsDirs(conf);
        final Collection<URI> requiredEditsDirs = FSNamesystem.getRequiredNamespaceEditsDirs(conf);
        final Collection<URI> sharedEditsDirs = FSNamesystem.getSharedEditsDirs(conf);

        for (URI u : requiredEditsDirs) {
            if (u.toString().compareTo(DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_DEFAULT) == 0) {
                continue;
            }

            // Each required directory must also be in editsDirs or in
            // sharedEditsDirs.
            if (!editsDirs.contains(u) && !sharedEditsDirs.contains(u)) {
                throw new IllegalArgumentException("Required edits directory " + u + " not found: "
                        + DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_KEY + "=" + editsDirs + "; "
                        + DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_REQUIRED_KEY + "=" + requiredEditsDirs + "; "
                        + DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY + "=" + sharedEditsDirs);
            }
        }
    }

    @Override
    public HadoopStartupWrapper generate(SourceOfRandomness random, GenerationStatus __ignore__) {
        Configuration conf = new HdfsConfiguration();
        File nameDir = new File(MiniDFSCluster.getBaseDirectory(), "name");
        conf.set(DFS_NAMENODE_NAME_DIR_KEY, nameDir.getAbsolutePath());
        // StartupOption startOpt1 =
        // StartupOption.values()[random.nextInt(StartupOption.values().length)];
        StartupOption startOpt = StartupOption.FORMAT;
        // NameNode.initMetrics(conf, NamenodeRole.NAMENODE);
        conf.set(DFS_NAMENODE_STARTUP_KEY, startOpt.name());
        try {
            DFSTestUtil.formatNameNode(conf);
            // DefaultMetricsSystem.initialize("NameNode");
            FSNamesystem fsn = FSNamesystem.loadFromDisk(conf);
            checkConfiguration(conf);
            FSImage fsImage = null;
            fsImage = new FSImage(conf, FSNamesystem.getNamespaceDirs(conf), FSNamesystem.getNamespaceEditsDirs(conf));
            return new HadoopStartupWrapper(fsImage, fsn, startOpt);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
