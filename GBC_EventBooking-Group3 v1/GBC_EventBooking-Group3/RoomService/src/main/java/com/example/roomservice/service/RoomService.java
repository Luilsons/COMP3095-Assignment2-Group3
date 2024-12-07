package com.example.roomservice.service;

import com.example.roomservice.model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomService {

    Room createRoom(Room room);

    List<Room> getAllRooms();

    Optional<Room> getRoomById(Long id);

    Room updateRoom(Long id, Room updatedRoom);

    void deleteRoom(Long id);

    boolean isRoomAvailable(Long roomId, String startTime, String endTime);

    List<Room> checkAvailability();
}
