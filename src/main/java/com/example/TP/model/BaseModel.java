package com.example.TP.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@ToString
public class BaseModel implements Serializable {

    private long businessId;
    private boolean active;
    private boolean archive;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    //    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivedAt;

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;

    @LastModifiedBy
    private Long archivedBy;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = new Date();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof UserPrincipal userImpl) {
                this.updatedBy = userImpl.getId();
            }
        }
    }

    public void onArchive() {
        this.archivedAt = new Date();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof UserPrincipal userImpl) {
                this.archivedBy = userImpl.getId();
                this.archive = true;
            }
        }
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof UserPrincipal userImpl) {
                this.createdBy = userImpl.getId();
                this.businessId = userImpl.getBusinessId();
            }
        }
    }
    // to save businessid of that business in user which is registered with user of type maintenance head
    public void onCreateWithoutBusinessId() {
        this.createdAt = new Date();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof UserPrincipal userImpl) {
                this.createdBy = userImpl.getId();
                this.createdAt = new Date();
            }
        }
    }
}
