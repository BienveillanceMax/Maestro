package com.arcos.maestromvp.Producer;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.ContextProviders.UserContext.UserProfileManager;
import com.arcos.maestromvp.Orchestrators.Orchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TriggerController {

    private final Orchestrator orchestrator;
    private final UserProfileManager userProfileManager;
    private final UserProfile userProfile;

    @Autowired
    public TriggerController(Orchestrator orchestrator,
                             UserProfileManager userProfileManager,
                             UserProfile userProfile) {
        this.orchestrator = orchestrator;
        this.userProfileManager = userProfileManager;
        this.userProfile = userProfile;
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> trigger() {
        System.out.println("Received trigger from Visual Service.");

        // Ensure profile is up to date (logic from main)
        //userProfileManager.completeUserProfile(userProfile);

        // Run the orchestrator logic
        // Note: Ideally this should be async if it takes long time,
        // but for MVP blocking is acceptable or we spawn a thread.
        new Thread(() -> {
            System.out.println("Starting Orchestration via Trigger...");
            orchestrator.run(userProfile);
        }).start();

        return ResponseEntity.ok("Triggered");
    }
}
