package com.aecode.webcoursesback.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aecode.webcoursesback.services.ImageUploadingService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/firebase")
public class ImageUploadingController {

    private final ImageUploadingService imageUploadingService;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile multipartFile) {

        return imageUploadingService.upload(multipartFile, "test/");
    }

}
