package androidRecyclerView;

/**
 * Created by eruvaka on 15-07-2017.
 */

public class DeviceIds {

    protected String hex_id;
    protected String status;

    public DeviceIds(String hex_id, String status) {
        this.hex_id = hex_id;
        this.status = status;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String senderName) {
        this.status = senderName;
    }

    public String gethex_id() {
        return hex_id;
    }

    public void sethex_id(String message) {
        this.hex_id = message;
    }


}
