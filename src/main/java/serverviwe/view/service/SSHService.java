package serverviwe.view.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

@Service
public class SSHService {

    public String execute(
            String host,
            String password,
            String command) throws Exception {

        JSch jsch = new JSch();

        Session session =
                jsch.getSession("root", host, 22);

        session.setPassword(password);

        Properties config = new Properties();
        config.put(
                "StrictHostKeyChecking",
                "no"
        );

        session.setConfig(config);

        session.connect();

        ChannelExec channel =
                (ChannelExec) session.openChannel("exec");

        channel.setCommand(command);

        InputStream input =
                channel.getInputStream();

        channel.connect();

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(input)
                );

        StringBuilder output =
                new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {

            output.append(line)
                  .append("\n");
        }

        channel.disconnect();
        session.disconnect();

        return output.toString();
    }
}