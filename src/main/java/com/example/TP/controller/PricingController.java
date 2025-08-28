package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.PricingRequest;
import com.example.TP.payload.response.PricingResponse;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.service.PricingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/pricing")
@Log4j2
public class PricingController {
    @Autowired
    private PricingService pricingService;

    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody PricingRequest pricingRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return pricingService.createPricing(pricingRequest, userDetails);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> update(@RequestBody PricingRequest pricingRequest, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return pricingService.updatePricing(pricingRequest, id, userDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER_ARCHIVE')")
    public ResponseModel<?> archive(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return pricingService.archivePricing(id, userDetails);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('USER_VIEW')")
    public ResponseModel<PageResponse<PricingResponse>> getAllPricing(Pageable pageable,
                                                                           @AuthenticationPrincipal UserDetails userDetails,
                                                                           @RequestParam(name = "packageId", required = false) Long packageId) {
        return pricingService.getAllPricing(pageable, userDetails, packageId);
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
