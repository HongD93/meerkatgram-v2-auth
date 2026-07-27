package com.meerkatgramv2auth.domain.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.StringUtils;

public record RegistrationRequestDTO(
    @Schema(description = "이메일", examples = "test@test.com", nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일은 필수입니다.")
    @Pattern(regexp = "^[0-9a-zA-Z](?!.*?[\\-_.]{2})[a-zA-Z0-9\\-_.]{3,63}@[0-9a-zA-Z](?!.*?[\\-_.]{2})[a-zA-Z0-9\\-_.]{3,63}\\.[a-zA-Z]{2,3}$", message = "허용하지 않는 양식입니다.")
    String email,

    @Schema(description = "비밀번호", examples = "qwer1234", nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^[0-9a-zA-Z!@#$%^&*()]{8,20}$", message = "허용하지 않는 비밀번호 양식입니다.")
    String password,

    @Schema(description = "비밀번호 확인", examples = "qwer1234", nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호 체크는 필수 항목입니다.")
    String confirmPassword,

    @Schema(description = "닉네임", examples = "test", nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "닉네임은 필수 항목입니다.")
    @Pattern(regexp = "^[0-9a-zA-Z_]{2,20}$", message = "허용하지 않는 닉네임 양식입니다.")
    String nick,

    @Schema(description = "프로필", examples = "http://localhost/test/auth/profiles/20260719_2e7d4fa7-4637-488c-b64d-788b1db10b0c.png", nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "프로필은 필수 항목입니다.")
    String profile
) {
    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isConfirmPassword() {
        if(StringUtils.isEmpty(this.password) || StringUtils.isEmpty(this.confirmPassword)) {
            return false;
        }
        return this.password.equals(this.confirmPassword);
    }
}
