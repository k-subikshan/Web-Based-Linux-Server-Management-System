package serverviwe.view.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class ServerMonitorService {

    public Map<String, String> getServerStats(
            String host,
            String password) throws Exception {

        Map<String, String> stats = new HashMap<>();

        JSch jsch = new JSch();

        Session session =
                jsch.getSession(
                        "root",
                        host,
                        22
                );

        session.setPassword(password);

        Properties config = new Properties();

        config.put(
                "StrictHostKeyChecking",
                "no"
        );

        session.setConfig(config);

        session.connect();


        /*
         * CPU
         */

        String cpuOutput = executeCommand(
                session,
                "top -bn1 | grep 'Cpu(s)'"
        );

        String cpu = parseCpu(cpuOutput);


        /*
         * RAM
         */

        String ramOutput = executeCommand(
                session,
                "free"
        );

        String ram = parseRam(ramOutput);


        /*
         * DISK
         */

        String diskOutput = executeCommand(
                session,
                "df -P / | tail -1"
        );

        String disk = parseDisk(diskOutput);


        /*
         * NETWORK
         */

        String networkOutput = executeCommand(
                session,
                "cat /proc/net/dev"
        );

        String network = parseNetwork(
                networkOutput
        );


        /*
         * Status
         */

        stats.put(
                "cpu",
                cpu
        );

        stats.put(
                "ram",
                ram
        );

        stats.put(
                "disk",
                disk
        );

        stats.put(
                "network",
                network
        );

        stats.put(
                "status",
                "connected"
        );


        session.disconnect();

        return stats;
    }


    /*
     * ============================
     * CPU
     * ============================
     */

    private String parseCpu(String output) {

        try {

            /*
             * Example:
             *
             * %Cpu(s): 0.0 us, 4.8 sy,
             * 0.0 ni, 95.2 id, ...
             */

            String idlePart =
                    output
                            .split(",")[3]
                            .trim();

            double idle =
                    Double.parseDouble(
                            idlePart
                                    .replace(
                                            "id",
                                            ""
                                    )
                                    .trim()
                    );

            double usage =
                    100 - idle;

            return String.format(
                    "%.1f",
                    usage
            );

        } catch (Exception e) {

            return "0";
        }
    }


    /*
     * ============================
     * RAM
     * ============================
     */

    private String parseRam(String output) {

        try {

            /*
             * free output:
             *
             *               total
             * used   free
             *
             * Mem:
             *  ...   ...   ...
             */

            String[] lines =
                    output.trim().split("\\R");

            String[] values =
                    lines[1]
                            .trim()
                            .split("\\s+");

            long total =
                    Long.parseLong(
                            values[1]
                    );

            long used =
                    Long.parseLong(
                            values[2]
                    );

            double percentage =
                    ((double) used / total) * 100;

            return String.format(
                    "%.1f",
                    percentage
            );

        } catch (Exception e) {

            return "0";
        }
    }


    /*
     * ============================
     * DISK
     * ============================
     */

    private String parseDisk(String output) {

        try {

            /*
             * Example:
             *
             * /dev/sda2  50G  12G  36G  24% /
             */

            String[] values =
                    output.trim()
                            .split("\\s+");

            String percentage =
                    values[4];

            return percentage.replace(
                    "%",
                    ""
            );

        } catch (Exception e) {

            return "0";
        }
    }


    /*
     * ============================
     * NETWORK
     * ============================
     */

    private String parseNetwork(String output) {

        try {

            long rx = 0;
            long tx = 0;

            String[] lines =
                    output.split("\\R");

            for (String line : lines) {

                line = line.trim();

                if (!line.contains(":")) {
                    continue;
                }

                String[] parts =
                        line.split(":");

                if (parts.length != 2) {
                    continue;
                }

                String interfaceName =
                        parts[0].trim();

                /*
                 * Ignore loopback
                 */

                if (interfaceName.equals("lo")) {
                    continue;
                }

                String[] values =
                        parts[1]
                                .trim()
                                .split("\\s+");

                if (values.length < 9) {
                    continue;
                }

                rx += Long.parseLong(values[0]);

                tx += Long.parseLong(values[8]);
            }

            /*
             * Convert bytes to MB
             */

            double totalMB =
                    (rx + tx)
                            / 1024.0
                            / 1024.0;

            return String.format(
                    "%.2f",
                    totalMB
            );

        } catch (Exception e) {

            return "0";
        }
    }


    /*
     * ============================
     * SSH COMMAND
     * ============================
     */

    private String executeCommand(
            Session session,
            String command) throws Exception {

        ChannelExec channel =
                (ChannelExec)
                        session.openChannel("exec");

        channel.setCommand(command);

        InputStream input =
                channel.getInputStream();

        channel.connect();

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                input
                        )
                );

        StringBuilder output =
                new StringBuilder();

        String line;

        while (
                (line = reader.readLine())
                        != null
        ) {

            output
                    .append(line)
                    .append("\n");
        }

        channel.disconnect();

        return output.toString();
    }
}