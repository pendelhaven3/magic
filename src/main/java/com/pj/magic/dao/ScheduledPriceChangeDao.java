package com.pj.magic.dao;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.ScheduledPriceChange;

public interface ScheduledPriceChangeDao {

    void save(ScheduledPriceChange scheduledPriceChange);

    List<ScheduledPriceChange> findAllByEffectiveDateAndApplied(Date date, boolean applied);

    void markAsApplied(ScheduledPriceChange scheduledPriceChange);

    List<ScheduledPriceChange> findAllByEffectiveDateGreaterThanOrEqual(Date date);

    void delete(ScheduledPriceChange scheduledPriceChange);
    
}
