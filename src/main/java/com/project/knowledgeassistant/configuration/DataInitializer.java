package com.project.knowledgeassistant.configuration;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.knowledgeassistant.entities.Role;
import com.project.knowledgeassistant.entities.User;
import com.project.knowledgeassistant.enums.AccountStatus;
import com.project.knowledgeassistant.repositories.RoleRepository;
import com.project.knowledgeassistant.repositories.UserRepository;

//@Data
//@AllArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

	private UserRepository userRepo;
	
	private RoleRepository roleRepo;
	
	private BCryptPasswordEncoder bCrypt;
	
	
	
	public DataInitializer(UserRepository userRepo, RoleRepository roleRepo, BCryptPasswordEncoder bCrypt) {
		super();
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.bCrypt = bCrypt;
	}



	@Override
	public void run(String... args) throws Exception {
		

		Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "Full Administrator with all permissions") ;
		Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "Department Manager can approve employees");
		Role employeeRole = createRoleIfNotFound("ROLE_EMPLOYEE", "Standard Employee user");
		Role publicRole = createRoleIfNotFound("ROLE_PUBLIC","Public limited user");
		
		if(!userRepo.findByEmail("admin@algoworks.com").isPresent()) {
			User admin = new User();
			admin.setUsername("ADMIN");
			admin.setEmail("admin@algoworks.com");
			admin.setPassword(bCrypt.encode("Admin@123"));
			admin.setAccountStatus(AccountStatus.ACTIVE);
			admin.setApprovedBy("SYSTEM");
			admin.setRoles(Set.of(adminRole));
			
			userRepo.save(admin);
			System.out.println("Admin is inserted into the database...");
		}
	}
	
	private Role createRoleIfNotFound(String roleName, String description) {
		
		return roleRepo.findByRole(roleName).orElseGet(()->{
			Role role = new Role();
			role.setRole(roleName);
			role.setDescription(description);
			return roleRepo.save(role);
		});
	}

}
