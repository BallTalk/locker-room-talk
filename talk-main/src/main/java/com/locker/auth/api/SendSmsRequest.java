package com.locker.auth.api;

import com.locker.auth.application.SendSmsCommand;
import com.locker.auth.application.SmsPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "SMS 인증번호 발송 요청")
public record SendSmsRequest(
        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
        @NotBlank(message = "PHONE_NUMBER_REQUIRED")
        @Pattern(
                regexp  = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                message = "PHONE_NUMBER_PATTERN_INVALID"
        )
        String phoneNumber,

        @Schema(description = "SMS 인증 용도", example = "SIGNUP")
        @NotNull(message = "SMS_PURPOSE_REQUIRED")
        SmsPurpose purpose
) {
        public SendSmsCommand toCommand() {
                return new SendSmsCommand(phoneNumber, purpose);
        }
}
