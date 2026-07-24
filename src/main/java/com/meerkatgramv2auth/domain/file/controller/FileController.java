package com.meerkatgramv2auth.domain.file.controller;

import com.meerkatgramv2auth.domain.file.response.FileResponseDTO;
import com.meerkatgramv2auth.domain.file.service.FileService;
import com.meerkatgramv2auth.global.config.openapi.CustomApiResponse;
import com.meerkatgramv2auth.global.response.GlobalResponseDTO;
import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "인증 파일 API", description = "파일 업로드 관련")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/files")
public class FileController {
    private final FileService fileService;

    @Operation(summary = "프로필 업로드 처리")
    @SecurityRequirements
    @CustomApiResponse(value = {
        CustomResponseCode.FILE_MANAGED_ERROR,
        CustomResponseCode.SYSTEM_ERROR
    })
    @PostMapping("/profiles")
    public ResponseEntity<GlobalResponseDTO<FileResponseDTO>> uploadProfile(
        @ModelAttribute MultipartFile file
    ) {
        return ResponseEntity.ok(GlobalResponseDTO.success(fileService.uploadProfile(file)));
    }
}
