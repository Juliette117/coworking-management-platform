package org.example.roomservice.services;

import org.example.roomservice.entities.Room;
import org.example.roomservice.kafka.RoomProducer;
import org.example.roomservice.repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomProducer roomProducer;

    public RoomService(RoomRepository roomRepository, RoomProducer roomProducer) {
        this.roomRepository = roomRepository;
        this.roomProducer = roomProducer;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room updatedRoom) {
        Room existingRoom = getRoomById(id);
        existingRoom.setName(updatedRoom.getName());
        existingRoom.setCity(updatedRoom.getCity());
        existingRoom.setCapacity(updatedRoom.getCapacity());
        existingRoom.setType(updatedRoom.getType());
        existingRoom.setHourlyRate(updatedRoom.getHourlyRate());
        existingRoom.setAvailable(updatedRoom.isAvailable());
        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
        
        // Envoi d'un événement Kafka pour notifier de la suppression de la salle
        roomProducer.sendRoomDeletedEvent(id);
    }
    
    public Room updateRoomAvailability(Long id, boolean isAvailable) {
        Room room = getRoomById(id);
        room.setAvailable(isAvailable);
        return roomRepository.save(room);
    }
}
