package org.garden.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.garden.com.converter.UserMapper;
import org.garden.com.dto.CreateUserDto;
import org.garden.com.dto.EditUserDto;
import org.garden.com.dto.UserDto;
import org.garden.com.entity.User;
import org.garden.com.exceptions.UserInvalidArgumentException;
import org.garden.com.exceptions.UserNotFoundException;
import org.garden.com.security.model.JwtAuthenticationResponse;
import org.garden.com.security.model.SignInRequest;
import org.garden.com.security.AuthenticationService;
import org.garden.com.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@Tag(name="User Controller", description="Handles operations related to users")
@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private AuthenticationService authenticationService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user based on the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created user"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> create(@RequestBody CreateUserDto createUserDto) {
        log.debug("Received request to create user: {}", createUserDto);
        User user = mapper.createUserDtoToUser(createUserDto);
        User createdUser = service.create(user);
        UserDto createdUserDto = mapper.userToUserDto(createdUser);
        log.debug("User created: {}", createdUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    }

    @PostMapping("/login")
    @ResponseBody
    public JwtAuthenticationResponse login(@RequestBody SignInRequest request) {
        return authenticationService.authenticate(request);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved  users"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping()
    public ResponseEntity<List<UserDto>> getAll() {
        log.debug("Received request to get all users");
        List<User> users = service.getAll();
        List<UserDto> userDtos = users.stream()
                .map(user -> mapper.userToUserDto(user))
                .collect(Collectors.toList());
        log.debug("Found {} users", users.size());
        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }

    @Operation(
            summary = "Update a user",
            description = "Updates an existing user with the provided ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated user"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<EditUserDto> editById(@PathVariable("id") long id, @RequestBody EditUserDto editUserDto) {
        log.debug("Received request to update user with ID {}: {}", id, editUserDto);
        User user = mapper.editUserDtoToUser(editUserDto);
        service.edit(id, user);
        EditUserDto updatedUserDto = mapper.userToEditUserDto(user);
        log.debug("User updated: {}", updatedUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserDto);
    }

    @Operation(
            summary = "Get a user by ID",
            description = "Retrieves a user by its unique identifier",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") long id) {
        log.debug("Received request to get user with ID: {}", id);
        User user = service.getById(id);
       UserDto userDto = mapper.userToUserDto(user);
        log.debug("Found user: {}", userDto);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }
    @Operation(
            summary = "Delete a user by ID",
            description = "Deletes a user with the provided ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") long id) {
        log.debug("Received request to delete user with ID: {}", id);
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ExceptionHandler({UserInvalidArgumentException.class, UserNotFoundException.class})
    public ResponseEntity<String> handleUserException(Exception exception) {
        HttpStatus status = (exception instanceof UserInvalidArgumentException)
                ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
        log.error("Error occurred: {}", exception.getMessage());
        return ResponseEntity.status(status).body(exception.getMessage());
    }
}
