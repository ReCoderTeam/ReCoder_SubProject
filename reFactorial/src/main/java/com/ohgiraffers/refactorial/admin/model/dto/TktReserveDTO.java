package com.ohgiraffers.refactorial.admin.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TktReserveDTO {

    private String tktReserveId;
    private String tktReserveName;
    private String tktReservePhone;
    private String tktReserveAddress;
    private String tktReserveAccomp;
    private String tktReserveDate;
}
