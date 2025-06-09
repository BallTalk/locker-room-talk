package com.locker.auth;

import com.locker.auth.application.SmsVerificationService;
import com.locker.auth.infra.sms.RedisSmsCodeRepository;
import com.locker.auth.infra.sms.SmsSender;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 인증번호_발송_성공시_저장소에_코드저장하고_SMS전송_호출한다() {
        // given
        String phone = "01012345678";
        SingleMessageSentResponse res = mock(SingleMessageSentResponse.class);
        when(res.getStatusCode()).thenReturn("2000");
        when(smsSender.sendOne(anyString(), anyString())).thenReturn(res);

        // when
        smsService.sendVerificationCode(phone);

        // then
        verify(codeRepository).saveCode(phoneCaptor.capture(), codeCaptor.capture(), eq(300L));
        assertThat(phoneCaptor.getValue()).isEqualTo(phone);
        assertThat(codeCaptor.getValue()).matches("\\d{6}");

        verify(smsSender).sendOne(eq(phone), eq(codeCaptor.getValue()));
    }

    @Test
    void 인증번호_발송_실패시_IllegalStateException_예외가_발생한다() {
        // given
        SingleMessageSentResponse res = mock(SingleMessageSentResponse.class);
        when(res.getStatusCode()).thenReturn("4000");
        when(smsSender.sendOne(anyString(), anyString())).thenReturn(res);

        // then
        assertThatThrownBy(() -> smsService.sendVerificationCode("01012345678"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SMS 전송 실패");
    }

    @Test
    void 인증번호_검증_성공시_저장소에서_코드를_삭제한다() {
        // given
        String phone = "01012345678", code = "123456";
        when(codeRepository.getCode(phone)).thenReturn(code);

        // when
        smsService.verifyCode(phone, code);

        // then
        verify(codeRepository).deleteCode(phone);
    }

    @Test
    void 인증번호가_존재하지_않거나_만료된_경우_IllegalArgumentException을_던진다() {
        when(codeRepository.getCode("01000000000")).thenReturn(null);

        assertThatThrownBy(() -> smsService.verifyCode("01000000000", "any"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증번호가 없거나 만료되었습니다.");
    }

    @Test
    void 인증번호_검증_불일치_코드인_경우_IllegalArgumentException_예외가_발생한다() {
        String phone = "01012345678";
        when(codeRepository.getCode(phone)).thenReturn("654321");

        assertThatThrownBy(() -> smsService.verifyCode(phone, "000000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증번호가 일치하지 않습니다.");
    }
}