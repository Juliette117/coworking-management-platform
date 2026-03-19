package org.example.reservationservice.controllers.restClients;

import org.example.reservationservice.dto.MemberDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MemberRestClient {

    private final RestTemplate restTemplate;

    public MemberRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MemberDTO getMemberById(Long id) {
        String url = "http://member-service/api/members/" + id;
        return restTemplate.getForObject(url, MemberDTO.class);
    }
}
