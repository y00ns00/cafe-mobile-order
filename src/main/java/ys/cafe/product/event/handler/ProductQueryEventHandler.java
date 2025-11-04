package ys.cafe.product.event.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ys.cafe.product.event.Event;
import ys.cafe.product.event.EventHandler;
import ys.cafe.product.event.EventType;
import ys.cafe.product.event.payload.ProductQueryPayload;
import ys.cafe.product.service.ProductService;

@Component
@RequiredArgsConstructor
public class ProductQueryEventHandler implements EventHandler<ProductQueryPayload> {

    private final ProductService productService;


    @Override
    @EventListener
    public void handleEvent(Event<ProductQueryPayload> event) {

    }

    @Override
    public boolean supportsEventType(Event<ProductQueryPayload> event) {
        if(EventType.PRODUCT_QUERY.equals(event.getType())) {
            return true;
        }
        return false;
    }
}
