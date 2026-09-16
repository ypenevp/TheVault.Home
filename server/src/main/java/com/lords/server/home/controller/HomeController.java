package com.lords.server.home.controller;

import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.auth.entity.User;
import com.lords.server.home.dto.request.CreateHomeRequest;
import com.lords.server.home.dto.request.HomeUpdateRequest;
import com.lords.server.home.dto.request.HomeUpdateStorage;
import com.lords.server.home.dto.request.ManageMemberRequest;
import com.lords.server.home.dto.response.HomeResponse;
import com.lords.server.home.service.HomeService;
import com.lords.server.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @PostMapping
    public ResponseEntity<HomeResponse> createHome(@Valid @RequestBody CreateHomeRequest request, @CurrentUser User currentUser) {
        HomeResponse response = homeService.createHome(request.name(), currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{homeId}")
    public ResponseEntity<HomeResponse> getHomeDetails(@PathVariable Long homeId, @CurrentUser User currentUser) {
        HomeResponse response = homeService.getHome(homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{homeId}")
    public ResponseEntity<HomeResponse> updateHome(@PathVariable Long homeId, @Valid @RequestBody HomeUpdateRequest request, @CurrentUser User currentUser) {
        HomeResponse response = homeService.updateHome(homeId, currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{homeId}/storage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HomeResponse> updateHomeStorage(@PathVariable Long homeId, @Valid @RequestBody HomeUpdateStorage request, @CurrentUser User currentUser) {
        HomeResponse response = homeService.updateStorage(homeId, currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{homeId}")
    public ResponseEntity<Void> deleteHome(@PathVariable Long homeId, @CurrentUser User currentUser) {
        homeService.deleteHome(homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{homeId}/members")
    public ResponseEntity<Void> addMember(@PathVariable Long homeId, @Valid @RequestBody ManageMemberRequest request, @CurrentUser User currentUser) {
        homeService.addMember(homeId, currentUser.getId(), request.username());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{homeId}/members")
    public ResponseEntity<Void> kickMember(@PathVariable Long homeId, @Valid @RequestBody ManageMemberRequest request, @CurrentUser User currentUser) {
        homeService.kickMember(homeId, currentUser.getId(), request.username());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{homeId}/members")
    public ResponseEntity<List<UserDetailsResponse>> viewHomeMembers(@PathVariable Long homeId, @CurrentUser User currentUser) {
        List<UserDetailsResponse> response = homeService.getMembers(homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{homeId}/owner")
    public ResponseEntity<Void> changeOwner(@PathVariable Long homeId, @Valid @RequestBody ManageMemberRequest request, @CurrentUser User currentUser) {
        homeService.changeOwner(homeId, currentUser.getId(), request.username());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
