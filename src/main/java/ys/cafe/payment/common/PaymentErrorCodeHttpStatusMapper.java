package ys.cafe.payment.common;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.ErrorCode;

/**
 * ErrorCode를 HTTP 상태 코드로 매핑
 */
@Component
public class PaymentErrorCodeHttpStatusMapper {

    public HttpStatus resolve(ErrorCode errorCode) {
        if (errorCode instanceof CommonErrorCode) {
            return switch ((CommonErrorCode) errorCode) {
                case NOT_FOUND -> HttpStatus.NOT_FOUND;
                case ALREADY_EXISTS -> HttpStatus.CONFLICT;
                case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            };
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
