package com.example.TP.controller;

import com.example.TP.payload.response.ResponseModel;
import com.example.TP.service.RoleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/roles")
@Log4j2
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/get")
    public ResponseModel<?> getRolesMap(
            @RequestParam(required = false) String title,
            @AuthenticationPrincipal UserDetails userDetails) {
        return roleService.getRolesIdTitleMap(userDetails, title);
    }
}
