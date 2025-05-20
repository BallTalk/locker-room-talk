package com.locker.configs.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locker.common.exception.handler.GlobalExceptionHandler;
import com.locker.common.exception.model.ErrorCode;
import com.locker.common.exception.specific.UserException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = GlobalExceptionHandlerTest.TestController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @RestController
    static class TestController {
        @GetMapping("/test/custom")
        public void custom() {
            throw UserException.loginIdBlank();
        }

        @PostMapping("/test/validate")
        public void validate(@Validated @RequestBody Dto dto) { }

        @GetMapping("/test/unknown")
        public void unknown() {
            throw new RuntimeException("boom");
        }
    }

    static class Dto {
        @NotBlank
        private String name;

        @Size(min = 5)
        private String code;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    @Nested
    @DisplayName("비즈니스(커스텀 예외) 처리")
    class BusinessExceptionTests {
        @Test
        void 커스텀_예외가_발생하면_설정된_상태와_메시지를_반환한다() throws Exception {
            mockMvc.perform(get("/test/custom"))
                    .andExpect(status().is(ErrorCode.LOGIN_ID_REQUIRED.getStatus().value()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", is(ErrorCode.LOGIN_ID_REQUIRED.getStatus().name())))
                    .andExpect(jsonPath("$.message", is(ErrorCode.LOGIN_ID_REQUIRED.getMessage())))
                    .andExpect(jsonPath("$.errors").doesNotExist());
        }
    }

    @Nested
    @DisplayName("DTO 검증 예외 처리")
    class ValidationExceptionTests {
        @Test
        void DTO_기본_검증_실패_시_STATUS_400과_기본_메시지를_반환한다() throws Exception {
            Dto dto = new Dto();
            dto.setName("");
            dto.setCode("123");

            mockMvc.perform(post("/test/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(ErrorCode.INVALID_REQUEST.getStatus().name())))
                    .andExpect(jsonPath("$.message", is(ErrorCode.INVALID_REQUEST.getMessage())))
                    .andExpect(jsonPath("$.errors", hasSize(2)))
                    .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("name", "code")))
                    .andExpect(jsonPath("$.errors[?(@.field=='name')].errorMessage", hasItem("must not be blank")))
                    .andExpect(jsonPath("$.errors[?(@.field=='code')].errorMessage", hasItem(startsWith("size must be between"))));
        }
    }

    @Nested
    @DisplayName("알 수 없는 예외 처리")
    class UnknownExceptionTests {
        @Test
        void 예기치_않은_예외_발생시_STATUS_500을_반환한다() throws Exception {
            mockMvc.perform(get("/test/unknown"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status", is(ErrorCode.INTERNAL_ERROR.getStatus().name())))
                    .andExpect(jsonPath("$.message", is(ErrorCode.INTERNAL_ERROR.getMessage())))
                    .andExpect(jsonPath("$.errors").doesNotExist());
        }
    }
}