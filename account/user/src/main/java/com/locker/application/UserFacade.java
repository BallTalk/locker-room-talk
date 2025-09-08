package com.locker.application;

import com.locker.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final SmsVerificationService smsVerificationService;

    @Transactional
    public void signUp(SignUpCommand command) {
        VerifySmsCommand smsCmd = new VerifySmsCommand(
                command.phoneNumber(),
                command.verificationCode(),
                SmsPurpose.SIGNUP
        );
        smsVerificationService.verifyCodeWithoutDelete(smsCmd);

        userService.signUp(
                command.loginId(),
                command.password(),
                command.confirmPassword(),
                command.nickname(),
                command.phoneNumber(),
                command.teamCode()
        );

        smsVerificationService.deleteCode(command.phoneNumber(), SmsPurpose.SIGNUP);
    }

    public Boolean exists(String loginId) {
        return userService.existsByLoginId(loginId);
    }

    public ProfileInfo getUserByLoginId(String loginId) {
        return ProfileInfo.from(userService.findByLoginId(loginId));
    }

    public void updateProfile(String loginId, UpdateProfileCommand command) {
        userService.updateProfile(loginId, command.nickname(), command.profileImageUrl(), command.statusMessage());
    }

    public void changePassword(String loginId, ChangePasswordCommand command) {
        userService.changePassword(loginId, command.oldPassword(), command.newPassword(), command.newPasswordConfirm());
    }

    public void withdraw(String loginId) {
        userService.withdraw(loginId);
    }

    @Transactional
    public String findIdWithSms(FindIdCommand command) {
        VerifySmsCommand smsCmd = new VerifySmsCommand(
                command.phoneNumber(),
                command.verificationCode(),
                SmsPurpose.FIND_ID
        );
        smsVerificationService.verifyCodeWithoutDelete(smsCmd);

        String loginId = userService.findLoginIdByPhone(command.phoneNumber());
        smsVerificationService.deleteCode(command.phoneNumber(), SmsPurpose.FIND_ID);
        return loginId;
    }

    @Transactional
    public void resetPasswordWithSms(ResetPasswordCommand command) {
        VerifySmsCommand smsCmd = new VerifySmsCommand(
                command.phoneNumber(),
                command.verificationCode(),
                SmsPurpose.RESET_PW
        );
        smsVerificationService.verifyCodeWithoutDelete(smsCmd);

        userService.resetPasswordByPhone(
                command.phoneNumber(),
                command.newPassword(),
                command.newPasswordConfirm()
        );

        smsVerificationService.deleteCode(command.phoneNumber(), SmsPurpose.RESET_PW);
    }
}
