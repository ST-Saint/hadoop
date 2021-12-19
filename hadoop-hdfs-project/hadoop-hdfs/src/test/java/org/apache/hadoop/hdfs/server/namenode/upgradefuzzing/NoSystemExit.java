package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.security.Permission;

public class NoSystemExit {
    protected static class ExitException extends SecurityException {
        public final int status;

        public ExitException(int status) {
            super("There is no escape!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            // allow anything.
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            // allow anything.
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    public static void set(){
        System.setSecurityManager(new NoExitSecurityManager());
    }

    public static void down(){
        System.setSecurityManager(null);
    }
}
