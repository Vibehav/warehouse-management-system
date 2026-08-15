package com.project.wms.inventory.dto;


import com.project.wms.inventory.facade.InboundPutawayFacade;

import java.util.List;

public record PutawayResponseDto(Long lotId, List<AssignmentDto> assignments) {

    public static PutawayResponseDto from(InboundPutawayFacade.PutawayResult result){
        List<AssignmentDto> assignment = result.balances().stream()
                .map(b-> new AssignmentDto(b.getId(),b.getLocation().getCode(),b.getQuantity()))
                .toList();

        return new PutawayResponseDto(result.lotId(),assignment);
    }


    public record AssignmentDto(Long balanceId, String locationCOde, int quantity){}
}
