package org.example.reservationservice.feign;

public class MemberDTO {
    private Long id;
    private boolean suspended;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }
}
