package io.github.vishalmysore.sevice;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.detect.ActionState;
import io.github.vishalmysore.common.A2AActionCallBack;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Log
@Service
@Agent(groupName = "alert support", groupDescription = "actions related to system alerts")
public class AlertService {
    private A2AActionCallBack callBack;

    @Action(description = "Process system alert")
    public String processAlert(String alertId, String type, String severity) {
        log.info("Processing alert: " + alertId);
        if(callBack != null) {
            callBack.sendtStatus("Processed alert ID: " + alertId + ", Type: " + type + ", Severity: " + severity, ActionState.COMPLETED);
        }
        return "Processed alert ID: " + alertId + ", Type: " + type + ", Severity: " + severity;
    }
}