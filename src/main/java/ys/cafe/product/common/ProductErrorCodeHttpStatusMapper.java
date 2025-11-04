package ys.cafe.product.common;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.ErrorCode;
import ys.cafe.product.exception.errorcode.ProductDomainErrorCode;

/**
 * ErrorCode를 HttpStatus로 매핑하는 전략 클래스
 * Domain/Exception 계층의 Web 의존성 제거를 위해 Web 계층에서 매핑 담당
 */
@Component
public class ProductErrorCodeHttpStatusMapper {

    /**
     * ErrorCode에 따라 적절한 HttpStatus를 매핑
     *
     * @param errorCode 에러 코드
     * @return 매핑된 HTTP 상태 코드
     */
    public HttpStatus resolve(ErrorCode errorCode) {
        // ProductDomainErrorCode의 경우 코드에 따라 다른 상태 코드 반환
        if (errorCode instanceof ProductDomainErrorCode domainErrorCode) {
            return switch (domainErrorCode) {
                default -> HttpStatus.BAD_REQUEST;
            };
        }

        if(errorCode instanceof CommonErrorCode commonErrorCode){
            return switch (commonErrorCode) {
                case NOT_FOUND ->  HttpStatus.NOT_FOUND;
                case ALREADY_EXISTS -> HttpStatus.CONFLICT;
                case BAD_REQUEST ->  HttpStatus.BAD_REQUEST;

            };
        }

        // ProductValidationErrorCode는 모두 BAD_REQUEST
        return HttpStatus.BAD_REQUEST;
    }
}
