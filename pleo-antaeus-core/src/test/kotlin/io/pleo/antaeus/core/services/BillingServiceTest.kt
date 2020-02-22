package io.pleo.antaeus.core.services

import java.math.BigDecimal
import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.*
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import io.pleo.antaeus.core.exceptions.LackOfFundsException
import io.pleo.antaeus.core.external.PaymentProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals

class BillingServiceTest {
    private val unpayableInvoice = Invoice(id = 404, customerId = 404, amount = Money(value = BigDecimal(100), currency = Currency.EUR), status = InvoiceStatus.PENDING)
    private val payableInvoice = Invoice(id = 405, customerId = 404, amount = Money(value = BigDecimal(100), currency = Currency.EUR), status = InvoiceStatus.PENDING)
    private val paidInvoice = Invoice(id = 405, customerId = 404, amount = Money(value = BigDecimal(100), currency = Currency.EUR), status = InvoiceStatus.PAID)

    private val dal = mockk<AntaeusDal> {
        every { fetchInvoice(payableInvoice.id) } returns payableInvoice
        every { fetchInvoice(unpayableInvoice.id) } returns unpayableInvoice
        every { setInvoiceStatus(payableInvoice, InvoiceStatus.PAID)} returns paidInvoice
    }
    private val pp = mockk<PaymentProvider> {
        every { charge(unpayableInvoice) } returns false
        every { charge(payableInvoice)} returns true
    }

    private val billingService = BillingService(paymentProvider = pp, dal = dal)

    @Test
    fun `will throw if invoice cannot be paid` (){
        assertThrows<LackOfFundsException> {
            billingService.processInvoice(unpayableInvoice)
        }
    }

    @Test
    fun `will check if status is set` (){
        assertEquals(payableInvoice.status, InvoiceStatus.PENDING)
        assertEquals(billingService.processInvoice(payableInvoice)?.status, InvoiceStatus.PAID)
    }
}