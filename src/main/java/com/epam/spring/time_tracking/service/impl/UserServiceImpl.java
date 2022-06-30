package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserLoginDto;
import com.epam.spring.time_tracking.dto.user.UserRegisterDto;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserRegisterDto userRegisterDto) {
        User user = modelMapper.map(userRegisterDto, User.class);
        user = userRepo.createUser(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto authUser(UserLoginDto userLoginDto) {
        User user = modelMapper.map(userLoginDto, User.class);
        user = userRepo.getUserByEmail(user.getEmail());
        if (userLoginDto.getPassword().equals(user.getPassword())) {
            return modelMapper.map(user, UserDto.class);
        }
        return null;
    }
}
