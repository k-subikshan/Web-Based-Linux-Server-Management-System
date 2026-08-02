package serverviwe.view.service;

import org.springframework.stereotype.Service;
import serverviwe.view.reposiotory.ProjectDeployedRepository;

@Service
public class ProjectDeployedService {
ProjectDeployedRepository projectDeployedRepository;
public ProjectDeployedService(ProjectDeployedRepository projectDeployedRepository) {
    this.projectDeployedRepository = projectDeployedRepository;}
    public String getProjectStatusById(Long id) {
        return projectDeployedRepository.findById(id)
                .map(projectDeployed -> projectDeployed.getStatus().name())
                .orElse("Project not found");
    }
    public String getProjectNameById(Long id) {
        return projectDeployedRepository.findById(id)
                .map(projectDeployed -> projectDeployed.getProjectName())
                .orElse("Project not found");
    }
    public String getServerIpById(Long id) {
        return projectDeployedRepository.findById(id)
                .map(projectDeployed -> projectDeployed.getServerInfo().getServerIp())
                .orElse("Project not found");}
    
}
