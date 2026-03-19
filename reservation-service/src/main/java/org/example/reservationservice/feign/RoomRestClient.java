package org.example.reservationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "room-service")
public interface RoomRestClient {

    @GetMapping("/api/rooms/{id}")
    RoomDTO getRoomById(@PathVariable("id") Long id);

    @PatchMapping("/api/rooms/{id}/availability")
    void updateRoomAvailability(@PathVariable("id") Long id, @RequestParam("available") boolean available);
}
