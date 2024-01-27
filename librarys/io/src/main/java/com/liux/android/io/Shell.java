package com.liux.android.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public abstract class Shell {

    public abstract int execResultCode(String cmd);

    public abstract String execResultString(String cmd);

    public static Shell DEFAULT = new Shell() {
        @Override
        public int execResultCode(String cmd) {
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

        private Process createProcess(String cmd) throws IOException {
            return Runtime.getRuntime().exec(cmd);
        }
    };

    public static Shell SU_ROOT = new Shell() {
        @Override
        public int execResultCode(String cmd) {
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

        private Process createProcess(String cmd) throws IOException {
            return Runtime.getRuntime().exec(new String[]{"su", "root", cmd});
        }
    };

    public static Shell SU_C = new Shell() {
        @Override
        public int execResultCode(String cmd) {
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

        private Process createProcess(String cmd) throws IOException {
            return Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
        }
    };

    public static Shell SU_EXEC = new Shell() {
        @Override
        public int execResultCode(String cmd) {
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

        private Process createProcess(String cmd) throws IOException {
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
