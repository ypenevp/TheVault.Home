package com.lords.server.home.service;

import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.repository.HomeRepository;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final HomeRepository homeRepository;
    private final UserRepository userRepository;

    public HomeService(HomeRepository homeRepository, UserRepository userRepository) {
        this.homeRepository = homeRepository;
        this.userRepository = userRepository;
    }

    public Home createHome(String name, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Home home = new Home();
        home.setName(name);
        home.setOwner(owner);
        home.setTotalSizeInBytes(0L);

        return homeRepository.save(home);
    }

}
