package ys.cafe.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.CommonException;
import ys.cafe.payment.common.PaymentErrorCodeHttpStatusMapper;
import ys.cafe.payment.common.PaymentGlobalExceptionHandler;
import ys.cafe.payment.service.PaymentService;
import ys.cafe.payment.service.dto.PaymentCancelRequest;
import ys.cafe.payment.service.dto.PaymentInfoResponse;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentController 테스트")
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        PaymentErrorCodeHttpStatusMapper mapper = new PaymentErrorCodeHttpStatusMapper();
        PaymentGlobalExceptionHandler exceptionHandler = new PaymentGlobalExceptionHandler(mapper);

        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(exceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("결제 정보 조회 성공")
    void getPaymentInfo_Success() throws Exception {
        // given
        String paymentKey = "test-payment-key";
        PaymentInfoResponse response = PaymentInfoResponse.of(
                1L,
                1L,
                "5000",
                "SUCCESS"
        );

        given(paymentService.getPaymentInfo(paymentKey)).willReturn(response);

        // when & then
        mockMvc.perform(get("/payments/{paymentKey}", paymentKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.amount").value("5000"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService).getPaymentInfo(paymentKey);
    }

    @Test
    @DisplayName("결제 정보 조회 실패 - 결제 정보 없음")
    void getPaymentInfo_NotFound() throws Exception {
        // given
        String paymentKey = "invalid-key";

        given(paymentService.getPaymentInfo(paymentKey))
                .willThrow(new CommonException(CommonErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다"));

        // when & then
        mockMvc.perform(get("/payments/{paymentKey}", paymentKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(paymentService).getPaymentInfo(paymentKey);
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment_Success() throws Exception {
        // given
        Long orderId = 1L;
        PaymentCancelRequest request = PaymentCancelRequest.of(orderId);
        PaymentInfoResponse response = PaymentInfoResponse.of(
                orderId,
                1L,
                "5000",
                "CANCELED"
        );

        given(paymentService.cancelPayment(orderId)).willReturn(response);

        // when & then
        mockMvc.perform(post("/payments/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.amount").value("5000"))
                .andExpect(jsonPath("$.status").value("CANCELED"));

        verify(paymentService).cancelPayment(orderId);
    }

    @Test
    @DisplayName("결제 취소 실패 - 결제 정보 없음")
    void cancelPayment_NotFound() throws Exception {
        // given
        Long orderId = 999L;
        PaymentCancelRequest request = PaymentCancelRequest.of(orderId);

        given(paymentService.cancelPayment(orderId))
                .willThrow(new CommonException(CommonErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다"));

        // when & then
        mockMvc.perform(post("/payments/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(paymentService).cancelPayment(orderId);
    }

    @Test
    @DisplayName("사용자 결제 목록 조회 성공")
    void getUserPayments_Success() throws Exception {
        // given
        Long memberId = 1L;
        List<PaymentInfoResponse> responses = List.of(
                PaymentInfoResponse.of(1L, memberId, "5000", "SUCCESS"),
                PaymentInfoResponse.of(2L, memberId, "10000", "SUCCESS"),
                PaymentInfoResponse.of(3L, memberId, "3000", "CANCELED")
        );

        given(paymentService.getUserPayments(memberId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/payments/members/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].memberId").value(memberId))
                .andExpect(jsonPath("$[0].amount").value("5000"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].amount").value("10000"))
                .andExpect(jsonPath("$[2].orderId").value(3))
                .andExpect(jsonPath("$[2].status").value("CANCELED"));

        verify(paymentService).getUserPayments(memberId);
    }

    @Test
    @DisplayName("사용자 결제 목록 조회 - 결제 내역 없음")
    void getUserPayments_EmptyList() throws Exception {
        // given
        Long memberId = 999L;
        List<PaymentInfoResponse> responses = List.of();

        given(paymentService.getUserPayments(memberId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/payments/members/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(paymentService).getUserPayments(memberId);
    }
}
