package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider

import io.pleo.antaeus.data.AntaeusDal

import io.pleo.antaeus.models.*

import java.time.LocalDateTime

import kotlin.text.*

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val dal: AntaeusDal
)
{
    init {
        val PAYMENT_DAY = 16
        val day = LocalDateTime.now().getDayOfMonth()

        if (day == PAYMENT_DAY)
        {
            val invoices = dal.fetchInvoices()
            invoices.forEach{
                processInvoice(invoice = it)
            }
        }

    }

    private  fun processInvoice(invoice: Invoice) : Boolean
    {
        val status = invoice.status
        if(status == InvoiceStatus.PENDING)
        {
            println("processing invoce: $invoice")
        }

        return true;
    }

}