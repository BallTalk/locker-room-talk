package com.locker.auth.infra.sms;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsSender {
    // 현재 개발환경이라 ip 전체 허용이고,
    // 발신번호도 내 번호임. 운영시 문자인증 한다면 가상 번호를 임대하거나 해야함

    @Value("${COOLSMS_API_KEY}")
    private String apiKey;

    @Value("${COOLSMS_API_SECRET}")
    private String apiSecret;

    @Value("${COOLSMS_URL}")
    private String apiUrl;

    @Value("${COOLSMS_SENDER_PHONE_NUMBER}")
    private String fromNumber;

    private DefaultMessageService messageService;


    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, apiUrl);
    }

    public SingleMessageSentResponse sendOne(String to, String verificationCode) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(to);
        message.setText("[locker-room-talk]\n 인증번호는" + verificationCode + "입니다");

        return this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

}
