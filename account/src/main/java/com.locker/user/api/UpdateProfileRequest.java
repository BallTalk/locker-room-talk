package com.locker.user.api;

import com.locker.user.application.UpdateProfileCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Schema(description = "닉네임", example = "박두산")
        @NotBlank(message = "NICKNAME_REQUIRED")
        @Size(min = 5, max = 20, message = "NICKNAME_LENGTH_INVALID")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/avatar.png")
        @Size(max = 255, message = "PROFILE_IMAGE_URL_TOO_LONG")
        String profileImageUrl,

        @Schema(description = "상태 메시지", example = "두산짱")
        @Size(max = 200, message = "STATUS_MESSAGE_TOO_LONG")
        String statusMessage

) {
    public UpdateProfileCommand toCommand() {
        return new UpdateProfileCommand(
                this.nickname,
                this.profileImageUrl,
                this.statusMessage
        );
    }
}
