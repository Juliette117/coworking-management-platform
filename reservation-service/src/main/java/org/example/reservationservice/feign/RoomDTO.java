package org.example.reservationservice.feign;

public class RoomDTO {
    private Long id;
    private boolean available;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
