package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.services.BillingService
import java.time.LocalDateTime

class ScheduleService(
    private val dayOfMonth: Int,
    private val billingService: BillingService
)
{
    public fun schedule()
    {
        while(true) {
            if (LocalDateTime.now().getDayOfMonth() == dayOfMonth)
            {
                billingService.processUnpaidInvoices()
            }
        }
    }
}