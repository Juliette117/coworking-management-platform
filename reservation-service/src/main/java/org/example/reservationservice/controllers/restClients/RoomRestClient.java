package org.example.reservationservice.controllers.restClients;

import org.example.reservationservice.dto.RoomDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RoomRestClient {

    private final RestTemplate restTemplate;

    public RoomRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RoomDTO getRoomById(Long id) {
        // L'URL utilise "room-service" (le nom enregistré dans Eureka) au lieu d'une IP en dur
        String url = "http://room-service/api/rooms/" + id;
        return restTemplate.getForObject(url, RoomDTO.class);
    }

    public void updateRoomAvailability(Long id, boolean available) {
        String url = "http://room-service/api/rooms/" + id + "/availability?available=" + available;
        // On utilise patchForObject. On passe null en body car le paramètre est dans l'URL.
        restTemplate.patchForObject(url, null, Void.class);
    }
}
