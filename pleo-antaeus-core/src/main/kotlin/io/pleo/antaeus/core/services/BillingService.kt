package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider

import io.pleo.antaeus.data.AntaeusDal

import io.pleo.antaeus.models.*

import kotlin.text.*

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val dal: AntaeusDal
)
{

    public fun processAllInvoices()
    {
        val invoices = dal.fetchInvoices()
        invoices.forEach{
            processInvoice(invoice = it)
        }
    }

    private  fun processInvoice(invoice: Invoice) : Boolean
    {
        val status = invoice.status
        if(status == InvoiceStatus.PENDING)
        {
            if(paymentProvider.charge(invoice))
            {
                println("$invoice WAS SUCCESSFULLY PAID")
                dal.setInvoiceStatus(id = invoice.id, newStatus = InvoiceStatus.PAID)
            }
            else{
                println("$invoice COULD NOT BE PAID")
            }
        }

        return true;
    }

}