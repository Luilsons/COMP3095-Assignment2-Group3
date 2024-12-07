package com.example.roomservice.service;

import com.example.roomservice.model.Room;
import com.example.roomservice.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room updateRoom(Long id, Room updatedRoom) {
        return roomRepository.findById(id)
                .map(room -> {
                    room.setRoomName(updatedRoom.getRoomName());
                    room.setCapacity(updatedRoom.getCapacity());
                    room.setFeatures(updatedRoom.getFeatures());
                    room.setAvailability(updatedRoom.isAvailability());
                    return roomRepository.save(room);
                })
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public boolean isRoomAvailable(Long roomId, String startTime, String endTime) {
        return true;
    }

    @Override
    public List<Room> checkAvailability() {
        return roomRepository.findAll();
    }
}
