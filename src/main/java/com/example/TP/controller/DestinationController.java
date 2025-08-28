package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.DestinationRequest;
import com.example.TP.payload.response.DestinationResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.service.DestinationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/destinations")
@Log4j2
public class DestinationController {

    @Autowired
    private DestinationService destinationService;

    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody DestinationRequest destinationRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return destinationService.createDestination(destinationRequest, userDetails);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return destinationService.getById(id, userDetails);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> update(@RequestBody DestinationRequest destinationRequest, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return destinationService.updateDestination(destinationRequest, id, userDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER_ARCHIVE')")
    public ResponseModel<?> archive(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return destinationService.archiveDestination(id, userDetails);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('USER_VIEW')")
    public ResponseModel<PageResponse<DestinationResponse>> getAllDestinations(Pageable pageable,
                                                                               @AuthenticationPrincipal UserDetails userDetails,
                                                                               @RequestParam(name = "name", required = false) String name,
                                                                               @RequestParam(name = "country", required = false) String country,
                                                                               @RequestParam(name = "slug", required = false) String slug) {
        return destinationService.getAllDestinations(pageable, userDetails, name, country, slug);
    }

    @GetMapping("/get")
    public ResponseModel<?> getDestinationsMap(
            @RequestParam(required = false) String name,
            @AuthenticationPrincipal UserDetails userDetails) {
        return destinationService.getDestinationsIdNameMap(userDetails, name);
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
