package com.example.springbootpractice.service;

import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.exception.BusinessException;
import com.example.springbootpractice.mapper.UserMapper;
import com.example.springbootpractice.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("张三");
        testUser.setAge(25);
        testUser.setEmail("zhangsan@qq.com");
        testUser.setRole("user");
    }

    @Test
    @DisplayName("根据ID查询用户 - 用户存在")
    void getUserById_Success() {
        // 1. 准备假数据
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // 2. 调用要测试的方法
        UserVO result = userService.getUserById(1L);

        // 3. 验证结果
        assertNotNull(result);
        assertEquals("张三", result.getName());
        assertEquals(25, result.getAge());
    }

    @Test
    @DisplayName("根据ID查询用户 - 用户不存在")
    void getUserById_NotFound() {
        // 1. 准备假数据：查不到用户返回 null
        when(userMapper.selectById(999L)).thenReturn(null);

        // 2. 验证：应该抛出 BusinessException
        assertThrows(BusinessException.class, () -> {
            userService.getUserById(999L);
        });
    }
}
