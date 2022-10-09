package com.shuke.reggie.controller;


import com.shuke.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}") // 此处应导 spring 的注解包
    private String basePath;


    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){ // 参数名需与前端对应
        log.info(file.toString());

        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 截取后缀（包括 ‘.'）
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 生成 UUID 拼接使文件名唯一
        String fileName = UUID.randomUUID().toString() + suffix;

        // 若路径目录不存在，需要创建
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try{
            // 将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        }catch(IOException e){
            e.printStackTrace();;
        }
        return R.success(fileName);
    }

    /**
     * 下载文件
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

       try {
           // 通过输入流读取文件
           FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

           // 通过输出流将文件发送至请求端
           ServletOutputStream outputStream = response.getOutputStream();
           response.setContentType("image/jpeg");

           // 字节流输出文件
           int len = 0;
           byte[] bytes = new byte[2048];
           while ((len = fileInputStream.read(bytes)) != -1){
               outputStream.write(bytes,0,len);
               outputStream.flush();
           }

           //关闭资源
           outputStream.close();;
           fileInputStream.close();

       }catch(Exception e){
           e.printStackTrace();
       }
    }

}
