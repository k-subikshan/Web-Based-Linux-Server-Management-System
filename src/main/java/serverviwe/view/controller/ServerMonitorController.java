package serverviwe.view.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import serverviwe.view.service.ServerMonitorService;
import serverviwe.view.service.ServiceMonitor;

@Controller
public class ServerMonitorController {

    private final ServerMonitorService monitor;
    private final ServiceMonitor serverMonitor;

    public ServerMonitorController(
            ServerMonitorService monitor,
            ServiceMonitor serverMonitor) {

        this.monitor = monitor;
        this.serverMonitor = serverMonitor;
    }

    @GetMapping("/server-monitor")
    public String monitor(
            HttpSession session,
            Model model) throws Exception {

        String host =
                session.getAttribute("host").toString();

        String password =
                session.getAttribute("password").toString();

        /*
         * Server statistics
         */
        var stats =
                monitor.getServerStats(
                        host,
                        password
                );

        /*
         * Service status
         */
        var services =
                serverMonitor.getServices(
                        host,
                        password
                );

        model.addAllAttributes(services);

        /*
         * Server information
         */
        model.addAttribute(
                "host",
                host
        );

        model.addAttribute(
                "port",
                session.getAttribute("port")
        );

        model.addAttribute(
                "path",
                session.getAttribute("path")
        );

        /*
         * Server monitor
         */
        model.addAttribute(
                "cpu",
                stats.get("cpu")
        );

        model.addAttribute(
                "ram",
                stats.get("ram")
        );

        model.addAttribute(
                "disk",
                stats.get("disk")
        );

        model.addAttribute(
                "network",
                stats.get("network")
        );

        model.addAttribute(
                "status",
                stats.get("status")
        );

        return "server-monitor";
    }
}