package com.liux.android.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public abstract class Shell {

    public abstract int execResultCodeBy(String cmd);

    public abstract String execResultString(String cmd);

    public abstract int execResultCodeBySu(String cmd);

    public abstract String execResultStringBySu(String cmd);

    public static Shell SU_ROOT = new Shell() {
        @Override
        public int execResultCodeBy(String cmd) {
            try {
                return createProcess(cmd).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        public String execResultString(String cmd) {
            try {
                return readProcess(
                        createProcess(cmd)
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public int execResultCodeBySu(String cmd) {
            try {
                return createProcess("su", "root", cmd).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        public String execResultStringBySu(String cmd) {
            try {
                return readProcess(
                        createProcess("su", "root", cmd)
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Process createProcess(String... cmds) throws IOException {
            return Runtime.getRuntime().exec(cmds);
        }
    };

    public static Shell SU_C = new Shell() {
        @Override
        public int execResultCodeBy(String cmd) {
            try {
                return createProcess(cmd).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        public String execResultString(String cmd) {
            try {
                return readProcess(
                        createProcess(cmd)
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public int execResultCodeBySu(String cmd) {
            try {
                return createProcess("su", "-c", cmd).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        public String execResultStringBySu(String cmd) {
            try {
                return readProcess(
                        createProcess("su", "-c", cmd)
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Process createProcess(String... cmds) throws IOException {
            return Runtime.getRuntime().exec(cmds);
        }
    };

    public static Shell SU_EXEC = new Shell() {
        @Override
        public int execResultCodeBy(String cmd) {
            try {
                return exec(cmd).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        public String execResultString(String cmd) {
            try {
                return readProcess(
                        exec(cmd)
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public int execResultCodeBySu(String cmd) {
            try {
                return execBuSu(cmd).waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        public String execResultStringBySu(String cmd) {
            try {
                return readProcess(
                        execBuSu(cmd)
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Process exec(String cmd) throws IOException {
            return Runtime.getRuntime().exec(cmd);
        }

        private Process execBuSu(String cmd) throws IOException {
            Process process = Runtime.getRuntime().exec("su");

            OutputStream outputStream = process.getOutputStream();
            outputStream.write(cmd.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write("exit".getBytes());
            outputStream.write("\n".getBytes());
            outputStream.flush();

            return process;
        }
    };

    public static Shell DEFAULT = SU_ROOT;

    public static String readProcess(Process process) throws IOException, InterruptedException {
        String result = null;

        InputStream inputStream = process.getInputStream();
        process.waitFor();

        int length = inputStream.available();
        if (length > 0) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            if (bytes[length - 1] == '\n') bytes = Arrays.copyOf(bytes, length - 1);
            result = new String(bytes);
        }
        inputStream.close();

        return result;
    }
}
