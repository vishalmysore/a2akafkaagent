package io.github.vishalmysore.sevice;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.detect.ActionState;
import io.github.vishalmysore.common.A2AActionCallBack;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Log
@Service
@Agent(groupName = "payment support", groupDescription = "actions related to payment processing")
public class PaymentService {
    private A2AActionCallBack callBack;

    @Action(description = "Process a payment")
    public String processPayment(String paymentId, String status, String amount) {
        log.info("Processing payment: " + paymentId);
        if(callBack != null) {
            callBack.sendtStatus("Processed payment ID: " + paymentId + ", Status: " + status + ", Amount: " + amount, ActionState.COMPLETED);
        }
        return "Processed payment ID: " + paymentId + ", Status: " + status + ", Amount: " + amount;
    }
}
