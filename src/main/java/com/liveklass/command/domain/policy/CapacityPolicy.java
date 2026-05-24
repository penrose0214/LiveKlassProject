package com.liveklass.command.domain.policy;

import com.liveklass.command.domain.entity.Lecture;
import org.springframework.stereotype.Component;

@Component
public class CapacityPolicy {

    public boolean hasAvailableSeat(Lecture lecture, long occupiedCount) {
        return occupiedCount < lecture.getCapacity();
    }
}
