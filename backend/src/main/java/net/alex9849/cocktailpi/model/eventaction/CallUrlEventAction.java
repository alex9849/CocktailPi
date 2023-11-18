package net.alex9849.cocktailpi.model.eventaction;

import jakarta.persistence.DiscriminatorValue;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@DiscriminatorValue("CallUrl")
public class CallUrlEventAction extends EventAction {
    private RequestMethod requestMethod;
    private String url;

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
    public String getDescription() {
        return "Call URL (" + this.requestMethod.name() + "): " + url;
    }

    @Override
    public void trigger(RunningAction runningAction) {
        HttpURLConnection con = null;

        try {
            URL url = new URL(this.url);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestMethod.name());
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.close();
            con.getResponseCode();

        } catch (IOException e) {
            e.printStackTrace();
            runningAction.addLog(e);
        }
    }
}
