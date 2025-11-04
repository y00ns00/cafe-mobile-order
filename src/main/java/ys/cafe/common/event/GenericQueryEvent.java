package ys.cafe.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * 모듈 간 직접 의존을 피하기 위한 범용 쿼리 이벤트
 * 진짜 직렬화: JSON 문자열로 payload와 result를 전달
 */
public class GenericQueryEvent extends ApplicationEvent {

    private final String eventType;
    private final String payloadJson;
    private String resultJson;

    public GenericQueryEvent(Object source, String eventType, String payloadJson) {
        super(source);
        this.eventType = eventType;
        this.payloadJson = payloadJson;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }
}
