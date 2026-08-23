package serverviwe.view.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceMonitor {

    private final SSHService ssh;

    public ServiceMonitor(SSHService ssh) {
        this.ssh = ssh;
    }

    public Map<String, String> getServices(
            String host,
            String password) throws Exception {

        Map<String, String> result =
                new HashMap<>();

        result.put(
                "nginx",
                ssh.execute(
                        host,
                        password,
                        "systemctl is-active nginx"
                )
        );

        result.put(
                "gunicorn",
                ssh.execute(
                        host,
                        password,
                        "systemctl is-active gunicorn"
                )
        );

        result.put(
                "springboot",
                ssh.execute(
                        host,
                        password,
                        "systemctl is-active springboot"
                )
        );

        result.put(
                "mysql",
                ssh.execute(
                        host,
                        password,
                        "systemctl is-active mysql"
                )
        );

        return result;
    }
}