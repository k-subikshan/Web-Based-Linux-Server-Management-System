package serverviwe.view.service;

import org.springframework.stereotype.Service;

@Service
public class LogAnalyzerService {

    private final SSHService ssh;

    public LogAnalyzerService(SSHService ssh) {
        this.ssh = ssh;
    }

    public String nginxLogs(
            String host,
            String password) throws Exception {

        return ssh.execute(
                host,
                password,
                "tail -n 100 /var/log/nginx/error.log"
        );
    }

    public String gunicornLogs(
            String host,
            String password) throws Exception {

        return ssh.execute(
                host,
                password,
                "journalctl -u gunicorn -n 100 --no-pager"
        );
    }

    public String springBootLogs(
            String host,
            String password) throws Exception {

        return ssh.execute(
                host,
                password,
                "journalctl -u springboot -n 100 --no-pager"
        );
    }

    public String mysqlLogs(
            String host,
            String password) throws Exception {

        return ssh.execute(
                host,
                password,
                "journalctl -u mysql -n 100 --no-pager"
        );
    }
}
