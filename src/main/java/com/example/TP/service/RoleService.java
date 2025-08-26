package com.example.TP.service;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.model.Role;
import com.example.TP.model.User;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.repository.RoleRepo;
import com.example.TP.repository.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RoleService {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;

    public RoleService(RoleRepo roleRepo, UserRepo userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    public ResponseModel<?> getRolesIdTitleMap(UserDetails userDetails, String titleSearch) {
        try {
            // 1. Validate requesting user exists
            User creator = userRepo.findOptionalByUsernameAndArchive(userDetails.getUsername(), false)
                    .orElseThrow(() -> {
                        log.error("Access Denied - User not found: {}", userDetails.getUsername());
                        return new SecurityException("Access Denied - User not found");
                    });

            List<Role> roles;

            if (titleSearch != null && !titleSearch.trim().isEmpty()) {
                roles = roleRepo.findByBusinessIdAndTitleContainingIgnoreCaseAndArchiveFalse(creator.getBusinessId(), titleSearch.trim());
            } else {
                roles = roleRepo.findByBusinessIdAndArchiveFalse(creator.getBusinessId());
            }

            // Always skip role with ID = 1
            Map<Long, String> rolesMap = roles.stream()
                    .filter(role -> !role.getId().equals(1L)) // Hardcoded skip ID 1
                    .collect(Collectors.toMap(
                            Role::getId,
                            Role::getTitle,
                            (existing, replacement) -> existing
                    ));

            return new ResponseModel<>().success(ResponseEnum.CREATED.getStatus(), rolesMap);

        } catch (Exception e) {
            log.error("Error retrieving roles map: {}", e.getMessage(), e);
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), "Failed to retrieve roles");
        }
    }

}
