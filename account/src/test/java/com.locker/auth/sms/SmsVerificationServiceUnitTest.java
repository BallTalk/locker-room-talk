package com.locker.auth.sms;

import com.locker.auth.application.SendSmsCommand;
import com.locker.auth.application.SmsPurpose;
import com.locker.auth.application.SmsVerificationService;
import com.locker.auth.application.VerifySmsCommand;
import com.locker.auth.infra.RedisSmsCodeRepository;
import com.locker.auth.infra.SmsSender;
import com.locker.auth.application.AuthException;
import com.locker.exception.model.ErrorCode;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SmsVerificationServiceUnitTest {

    @Mock
    private SmsSender smsSender;

    @Mock
    private RedisSmsCodeRepository codeRepository;

    @InjectMocks
    private SmsVerificationService smsService;

    @Captor
    private ArgumentCaptor<String> phoneCaptor;

    @Captor
    private ArgumentCaptor<String> codeCaptor;

    @Captor
    private ArgumentCaptor<SmsPurpose> purposeCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 인증번호_발송_성공시_저장소에_코드를_저장하고_SMS전송을_호출한다() {
        // given
        String phone = "01012345678";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        SingleMessageSentResponse res = mock(SingleMessageSentResponse.class);
        when(res.getStatusCode()).thenReturn("2000");
        when(smsSender.sendOne(anyString(), anyString())).thenReturn(res);

        // when: SendSmsCommand로 호출
        SendSmsCommand cmd = new SendSmsCommand(phone, purpose);
        smsService.sendVerificationCode(cmd);

        // then
        // saveCode(phone, purpose, code, ttl)
        verify(codeRepository).saveCode(
                phoneCaptor.capture(),
                purposeCaptor.capture(),
                codeCaptor.capture(),
                eq(300L)
        );
        assertThat(phoneCaptor.getValue()).isEqualTo(phone);
        assertThat(purposeCaptor.getValue()).isEqualTo(purpose);
        assertThat(codeCaptor.getValue()).matches("\\d{6}");

        verify(smsSender).sendOne(eq(phone), eq(codeCaptor.getValue()));
    }

    @Test
    void 인증번호_발송_실패시_AuthException_sendFailed_예외가_발생하고_저장된코드_삭제한다() {
        // given
        String phone = "01012345678";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        SingleMessageSentResponse res = mock(SingleMessageSentResponse.class);
        when(res.getStatusCode()).thenReturn("4000");
        when(smsSender.sendOne(anyString(), anyString())).thenReturn(res);

        // when / then
        SendSmsCommand command = new SendSmsCommand(phone, purpose);
        AuthException ex = assertThrows(
                AuthException.class,
                () -> smsService.sendVerificationCode(command)
        );
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.SMS_SEND_FAILED);
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.SMS_SEND_FAILED.getMessage());

        verify(codeRepository).saveCode(eq(phone), eq(purpose), anyString(), eq(300L));
        verify(codeRepository).deleteCode(eq(phone), eq(purpose));
    }

    @Test
    void 인증번호_검증_성공시_deleteCode_호출되지_않는다() {
        // given
        String phone = "01012345678";
        String code = "123456";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        when(codeRepository.getCode(phone, purpose)).thenReturn(code);

        // when: VerifySmsCommand로 호출
        VerifySmsCommand command = new VerifySmsCommand(phone, code, purpose);
        smsService.verifyCodeWithoutDelete(command);

        // then: deleteCode는 수행되지 않음
        verify(codeRepository, never()).deleteCode(anyString(), any());
    }

    @Test
    void 인증번호가_존재하지_않거나_만료된_경우_AuthException_codeExpired_예외가_발생한다() {
        // given
        String phone = "01000000000";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        when(codeRepository.getCode(phone, purpose)).thenReturn(null);

        // when / then
        VerifySmsCommand command = new VerifySmsCommand(phone, "any", purpose);
        AuthException ex = assertThrows(
                AuthException.class,
                () -> smsService.verifyCodeWithoutDelete(command)
        );
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.SMS_CODE_EXPIRED);
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.SMS_CODE_EXPIRED.getMessage());

        verify(codeRepository, never()).deleteCode(anyString(), any());
    }

    @Test
    void 인증번호_검증_불일치_코드인_경우_AuthException_codeMismatch_예외가_발생한다() {
        // given
        String phone = "01012345678";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        when(codeRepository.getCode(phone, purpose)).thenReturn("654321");

        // when / then
        VerifySmsCommand command = new VerifySmsCommand(phone, "000000", purpose);
        AuthException ex = assertThrows(
                AuthException.class,
                () -> smsService.verifyCodeWithoutDelete(command)
        );
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.SMS_CODE_MISMATCH);
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.SMS_CODE_MISMATCH.getMessage());

        verify(codeRepository, never()).deleteCode(anyString(), any());
    }

    @Test
    void 인증번호_검증_코드가_null인_경우_AuthException_codeMismatch_예외가_발생한다() {
        // given
        String phone = "01012345678";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        when(codeRepository.getCode(phone, purpose)).thenReturn("123456");

        // when / then
        VerifySmsCommand command = new VerifySmsCommand(phone, null, purpose);
        AuthException ex = assertThrows(
                AuthException.class,
                () -> smsService.verifyCodeWithoutDelete(command)
        );
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.SMS_CODE_MISMATCH);
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.SMS_CODE_MISMATCH.getMessage());

        verify(codeRepository, never()).deleteCode(anyString(), any());
    }

    @Test
    void deleteCode_호출시_repository_가_호출된다() {
        // given
        String phone = "01099998888";
        SmsPurpose purpose = SmsPurpose.SIGNUP;

        // when
        smsService.deleteCode(phone, purpose);

        // then
        verify(codeRepository).deleteCode(phone, purpose);
    }
}