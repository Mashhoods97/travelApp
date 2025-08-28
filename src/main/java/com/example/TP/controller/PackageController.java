package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.PackageRequest;
import com.example.TP.payload.response.PackageResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.service.PackageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/packages")
@Log4j2
public class PackageController {
    @Autowired
    private PackageService packageService;

    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody PackageRequest packageRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return packageService.createPackage(packageRequest, userDetails);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> update(@RequestBody PackageRequest packageRequest, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return packageService.updatePackage(packageRequest, id, userDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER_ARCHIVE')")
    public ResponseModel<?> archive(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return packageService.archivePackage(id, userDetails);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('USER_VIEW')")
    public ResponseModel<PageResponse<PackageResponse>> getAllPackages(Pageable pageable,
                                                                   @AuthenticationPrincipal UserDetails userDetails,
                                                                   @RequestParam(name = "name", required = false) String name,
                                                                   @RequestParam(name = "code", required = false) String code) {
        return packageService.getAllPackages(pageable, userDetails, name, code);
    }

    @GetMapping("/get")
    public ResponseModel<?> getPackagesMap(
            @RequestParam(required = false) String name,
            @AuthenticationPrincipal UserDetails userDetails) {
        return packageService.getPackagesIdNameMap(userDetails, name);
    }

    @ExceptionHandler(Exception.class)
    public ResponseModel<?> handleException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage());
        if (e.getClass().getSimpleName().equalsIgnoreCase("AccessDeniedException")) {
            return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), "AccessDenied");
        } else {
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage());
        }
    }
}
