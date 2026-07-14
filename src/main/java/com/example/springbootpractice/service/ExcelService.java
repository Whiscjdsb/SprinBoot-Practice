package com.example.springbootpractice.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.example.springbootpractice.dto.UserExcelDTO;
import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 导出所有用户到 Excel
     */
    public void exportUsers(HttpServletResponse response) throws IOException {
        // ① 查数据库
        List<User> users = userMapper.selectList(null);

        // ② User 转 UserExcelDTO
        List<UserExcelDTO> dtoList = new ArrayList<>();
        for (User user : users) {
            UserExcelDTO dto = new UserExcelDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setAge(user.getAge());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            dtoList.add(dto);
        }

        // ③ 设置响应头（告诉浏览器这是 Excel 文件）
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户列表", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // ④ 写入 Excel
        OutputStream outputStream = response.getOutputStream();
        EasyExcel.write(outputStream, UserExcelDTO.class)
                .sheet("用户列表")
                .doWrite(dtoList);

        logger.info("[Excel] 导出成功，共 {} 条用户数据", dtoList.size());
    }

    /**
     * 从 Excel 导入用户
     */
    public String importUsers() throws IOException {
        // 这个方法需要配合 Controller 的文件上传调用
        // 实际逻辑在 Controller 里用 EasyExcel.read() 实现
        return "import";
    }
}
