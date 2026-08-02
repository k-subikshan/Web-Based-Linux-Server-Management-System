package serverviwe.view.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import jakarta.servlet.http.HttpSession;
import serverviwe.view.layer.vpsloginform;




@Controller
public class serverlogin {
    @GetMapping("/connecttovps")
    public String connectvps() {
        return "vpslogin";
    }
    @PostMapping("/connect")
    public String postMethodName(@ModelAttribute vpsloginform entity,Model model,HttpSession session1) throws Exception {
        JSch jsch = new JSch();

Session session = jsch.getSession("root", entity.getHost(), 22);
session.setPassword(entity.getPassword());

Properties config = new Properties();
config.put("StrictHostKeyChecking", "no");
session.setConfig(config);

session.connect();

System.out.println("------------------------------------------------------------Connected!------------------------------------------------");
String output = executeCommand(
    session,
    "find /"+entity.getPath()+" -maxdepth 1 -mindepth 1 -type d -printf '%f\\n'"
);

List<String> folders = Arrays.asList(output.split("\\R"));
model.addAttribute("folders", folders);
model.addAttribute("path", entity.getPath());
model.addAttribute("host", entity.getHost());
model.addAttribute("port", entity.getPort());
model.addAttribute("password", entity.getPassword());
model.addAttribute("sessionid",session);
model.addAttribute("fullpath", entity.getPath());
session1.setAttribute("host", entity.getHost());
session1.setAttribute("port", entity.getPort());
session1.setAttribute("password", entity.getPassword());
session1.setAttribute("fullpath", entity.getPath());
session1.setAttribute("sessionid",session);
session1.setAttribute("fullpath", entity.getPath());
session1.setAttribute("currentpath", entity.getPath());
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
