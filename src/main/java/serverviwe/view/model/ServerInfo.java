package serverviwe.view.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "server_info")
public class ServerInfo {
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Id
private Long id;
private String serverIp;
private String serverName;
}
