package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.payload.request.QuotationRequest;
import com.example.TP.payload.response.PageResponse;
import com.example.TP.payload.response.QuotationResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.service.QuotationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/quotations")
@Log4j2
public class QuotationController {
    @Autowired
    private QuotationService quotationService;

    @PostMapping
    @PreAuthorize("hasRole('USER_CREATE')")
    public ResponseModel<?> create(@RequestBody QuotationRequest quotationRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return quotationService.createQuotation(quotationRequest, userDetails);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return quotationService.getById(id, userDetails);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER_UPDATE')")
    public ResponseModel<?> update(@RequestBody QuotationRequest quotationRequest, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return quotationService.updateQuotation(quotationRequest, id, userDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER_ARCHIVE')")
    public ResponseModel<?> archive(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return quotationService.archiveQuotation(id, userDetails);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('USER_VIEW')")
    public ResponseModel<PageResponse<QuotationResponse>> getAllQuotations(Pageable pageable,
                                                                           @AuthenticationPrincipal UserDetails userDetails,
                                                                           @RequestParam(name = "name", required = false) String name) {
        return quotationService.getAllQuotations(pageable, userDetails, name);
    }

    @GetMapping("/get")
    public ResponseModel<?> getQuotationsMap(
            @RequestParam(required = false) String name,
            @AuthenticationPrincipal UserDetails userDetails) {
        return quotationService.getQuotationsIdNameMap(userDetails, name);
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
