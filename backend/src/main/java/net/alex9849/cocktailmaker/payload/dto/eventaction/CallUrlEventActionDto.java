package net.alex9849.cocktailmaker.payload.dto.eventaction;

import net.alex9849.cocktailmaker.model.eventaction.CallUrlEventAction;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CallUrlEventActionDto extends EventActionDto {
    @NotNull
    private RequestMethod requestMethod;
    @NotNull
    @Size(max = 255)
    private String url;

    public CallUrlEventActionDto() {}

    public CallUrlEventActionDto(CallUrlEventAction eventAction) {
        BeanUtils.copyProperties(eventAction, this);
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getType() {
        return "callUrl";
    }
}
