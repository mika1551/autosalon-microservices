package infrastructure.web.controller;

import application.service.UserService;
import domain.enums.Role;
import domain.model.User;
import infrastructure.web.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService service;
    private final ModelMapper mapper;

    public UserController(UserService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable UUID id) {
        return mapper.map(service.getById(id), UserDto.class);
    }
    @GetMapping
    public List<UserDto> getAll(@RequestParam(required = false) Role role){
        var list = (role == null) ? service.getAll() : service.getByRole(role);
                return list.stream().map(u -> mapper.map(u, UserDto.class)).toList();

    }

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        User user = mapper.map(dto, User.class);
        return mapper.map(service.add(user), UserDto.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
