package com.example.springbootpractice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootpractice.common.PageResult;
import com.example.springbootpractice.dto.ChangePasswordRequest;
import com.example.springbootpractice.dto.CreateUserRequest;
import com.example.springbootpractice.dto.UpdateUserRequest;
import com.example.springbootpractice.dto.UserQueryRequest;
import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.exception.BusinessException;
import com.example.springbootpractice.mapper.UserMapper;
import com.example.springbootpractice.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    public List<User> getUsers() {
        return userMapper.selectList(null);
    }
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getName, username);
        return userMapper.selectOne(query);
    }
    public User addUser(User user) {
        userMapper.insert(user);
        return user;
    }
    @Cacheable(value = "user", key = "#id")
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return UserVO.fromEntity(user);
    }
    @Transactional
    public UserVO addUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setEmail(request.getEmail());

        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userMapper.insert(user);

        return UserVO.fromEntity(user);
    }
    public UserVO updateUser(Long id, UpdateUserRequest request) {
        User user = userMapper.selectById(id);

        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setName(request.getName());
        user.setAge(request.getAge());

        userMapper.updateById(user);

        return UserVO.fromEntity(user);
    }
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        userMapper.deleteById(id);

    }
    public List< User> getAllUsers(String name) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.like(User::getName, name);
        return userMapper.selectList(query);
    }
    public List<User> getUsersOlderThan(Integer age) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.gt(User::getAge, age);
        return userMapper.selectList(query);
    }
    public List<User> searchUserByName(String name) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.like(User::getName, name);
        return userMapper.selectList(query);
    }
    public List<User> searchUsers(String name, Integer age) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.like(name != null&& !name.isBlank(),User::getName, name);
        query.gt(age != null,User::getAge, age);

        query.orderByDesc(User::getId);
        return userMapper.selectList(query);
    }
    public PageResult<UserVO> pageUsers(UserQueryRequest request) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();

        query.like(request.getName() != null && !request.getName().isBlank(), User::getName, request.getName());
        query.gt(request.getAge() != null, User::getAge, request.getAge());
        query.orderByDesc(User::getId);

        Page<User> pageObj = new Page<>(request.getPage(), request.getSize());

        Page<User> result = userMapper.selectPage(pageObj, query);
        List<UserVO> list = result.getRecords().stream().map(UserVO::fromEntity).collect(Collectors.toList());
        return PageResult.of(
                list,
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        );
    }

    public void updateAvatar(Long id, String avatarPath) {
        User user = userMapper.selectById(id);
        user.setAvatar(avatarPath);
        userMapper.updateById(user);
    }
    public void changePassword(Long id, @Valid ChangePasswordRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }
            user.setPassword(request.getNewPassword());
        userMapper.updateById(user);
    }

}
