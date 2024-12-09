package com.ohgiraffers.refactorial.booking.model.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomDTO {

    public String reservationId;
    public int ConferenceRoomNo;
    public String empId;
    public Date reservationDate;
}
