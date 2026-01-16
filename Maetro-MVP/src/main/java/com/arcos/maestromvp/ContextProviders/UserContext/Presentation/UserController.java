package com.arcos.maestromvp.ContextProviders.UserContext.Presentation;

import com.arcos.maestromvp.ContextProviders.UserContext.Presentation.Dto.UserProfileDto;
import com.arcos.maestromvp.ContextProviders.UserContext.Service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
//@CrossOrigin(origins = "http://localhost")
public class UserController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity<Void> createUserProfile(@RequestBody UserProfileDto dto) {
        userProfileService.createUserProfile(dto);
        return ResponseEntity.noContent().build();
    }
}
