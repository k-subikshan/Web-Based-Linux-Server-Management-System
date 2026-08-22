package serverviwe.view.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import jakarta.servlet.http.HttpSession;


@Controller
public class Run_command {

    @PostMapping("/run_command")
    public String runCommand(
            @RequestParam String command,
            HttpSession session,Model model) throws Exception {

        String host = session.getAttribute("host").toString();
        String password = session.getAttribute("password").toString();
        String path = session.getAttribute("path").toString();

        JSch jsch = new JSch();

        Session sshSession = jsch.getSession("root", host, 22);

        sshSession.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        sshSession.setConfig(config);
        sshSession.connect();

        String output = executeCommand(
                sshSession,
                "cd " + path + " && " + command
        );
        model.addAttribute("output", output);
        model.addAttribute("command", command);
        model.addAttribute("path", path);

        sshSession.disconnect();

        return "run_command"; // Return the name of the view to display the command output
    }
    public String executeCommand(Session session, String command) throws Exception {

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        InputStream input = channel.getInputStream();
        channel.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        channel.disconnect();

        return output.toString();
    }
}
