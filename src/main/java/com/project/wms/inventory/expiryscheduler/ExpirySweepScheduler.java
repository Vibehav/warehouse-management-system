package com.project.wms.inventory.expiryscheduler;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.domain.state.InventoryLotStateResolver;
import com.project.wms.inventory.enums.LotState;
import com.project.wms.inventory.repository.InventoryLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Nightly sweep: any AVAILABLE lot past its expiryDate is transitioned to
 * EXPIRED — through the State pattern, not a raw UPDATE. Existing reservations
 * are deliberately retained for manual exception handling; an EXPIRED state
 * cannot transition to SHIPPED, so a future shipment flow must reject it.
 */
@Component
@RequiredArgsConstructor
public class ExpirySweepScheduler {

    private final InventoryLotRepository lotRepository;
    private final InventoryLotStateResolver stateResolver;


    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void sweepExpiredLots() {
        List<InventoryLot> expiredLots = lotRepository.findByStateAndExpiryDateLessThanEqual(
                LotState.AVAILABLE, LocalDate.now()
        );

        for (InventoryLot lot : expiredLots) {
            stateResolver.resolve(lot).expire(lot);
            lotRepository.save(lot);
        }
    }
}
