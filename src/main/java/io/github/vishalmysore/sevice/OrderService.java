package io.github.vishalmysore.sevice;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.detect.ActionState;
import io.github.vishalmysore.common.A2AActionCallBack;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Log
@Service
@Agent(groupName = "order support", groupDescription = "actions related to order support")
public class OrderService {
    private A2AActionCallBack callBack;

    @Action(description = "Process a new order")
    public String processNewOrder(String orderId, String status, String amount) {
         log.info("Processing new order: " + orderId);
         if(callBack!= null) {
           callBack.sendtStatus("Processed your order Order ID: " + orderId + ", Status: " + status + ", Amount: " + amount, ActionState.COMPLETED);
         }
        return "Processed your order Order ID: " + orderId + ", Status: " + status + ", Amount: " + amount;
    }
}
