package serverviwe.view.controller;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Controller
public class internal_path {

    @GetMapping("/internal/path")
    public String openFolder( @RequestParam String path,
                             HttpSession session,
                             Model model) throws Exception {

            JSch jsch = new JSch();

    Session sshSession = jsch.getSession(
            "root",
            session.getAttribute("host").toString(),
            22);

    sshSession.setPassword(session.getAttribute("password").toString());

    Properties config = new Properties();
    config.put("StrictHostKeyChecking", "no");
    sshSession.setConfig(config);

    sshSession.connect();

    String output = executeCommand(
            sshSession,
            "find /" + path + " -maxdepth 1 -mindepth 1 -printf '%f\\n'"
    );

    List<String> folders = Arrays.asList(output.split("\\R"));
    session.setAttribute("path", path);
    model.addAttribute("folders", folders);
    model.addAttribute("path", path);
    model.addAttribute("host", session.getAttribute("host"));


    return "pathfolder";
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