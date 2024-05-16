package models;

public class GetOrdersResponse {
    private Object[] orders;
    private Object pageInfo;
    private Object[] availableStations;

    public GetOrdersResponse(Object[] orders, Object pageInfo, Object[] availableStations) {
        this.orders = orders;
        this.pageInfo = pageInfo;
        this.availableStations = availableStations;
    }

    public Object[] getOrders() {
        return orders;
    }

    public void setOrders(Object[] orders) {
        this.orders = orders;
    }

    public Object getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Object pageInfo) {
        this.pageInfo = pageInfo;
    }

    public Object[] getAvailableStations() {
        return availableStations;
    }

    public void setAvailableStations(Object[] availableStations) {
        this.availableStations = availableStations;
    }
}
