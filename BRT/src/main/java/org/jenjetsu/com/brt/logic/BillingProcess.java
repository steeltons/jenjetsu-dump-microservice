package org.jenjetsu.com.brt.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ContentType;
import org.jenjetsu.com.brt.broker.sender.CdrMessageSender;
import org.jenjetsu.com.core.dto.BillingDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class BillingProcess {

    private AtomicBoolean isBillingProcessStart;
    private final CdrMessageSender cdrMessageSender;
    private final ObjectMapper objectMapper;
    private final ConcurrentLinkedQueue<AsyncContext> asyncContextConcurrentLinkedQueue;

    public BillingProcess(CdrMessageSender cdrMessageSender,
                          ObjectMapper objectMapper) {
        this.cdrMessageSender = cdrMessageSender;
        this.objectMapper = objectMapper;
        this.isBillingProcessStart = new AtomicBoolean(false);
        asyncContextConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    }

    public void startBilling(HttpServletRequest request, HttpServletResponse response) {
        if(!isBillingProcessStart.getAcquire()) {
            isBillingProcessStart.set(true);
            cdrMessageSender.sendGenerateCdrFileCommand();
        }
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(2 * 60 * 1000l);
        asyncContextConcurrentLinkedQueue.add(asyncContext);
    }

    public void endBilling(BillingDto billingDto) {
        asyncContextConcurrentLinkedQueue.forEach(asyncContext -> notify(asyncContext, billingDto));
        asyncContextConcurrentLinkedQueue.clear();
        isBillingProcessStart.set(false);
    }

    // TODO rewrite method
    private void notify(AsyncContext asyncContext, BillingDto billingDto) {
        HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
        try {
            String value = objectMapper.writeValueAsString(billingDto);
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(ContentType.APPLICATION_JSON.toString());
            response.getWriter().write(value);
            response.getWriter().flush();
            asyncContext.complete();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            try {
                response.sendError(500, "Error writing json dto");
                log.error("BillingDtoSubscriber: ERROR CONVERTING DTO TO JSON. CHECK MAPPER!");
            } catch (IOException e1) {
            }
        }
    }
}
