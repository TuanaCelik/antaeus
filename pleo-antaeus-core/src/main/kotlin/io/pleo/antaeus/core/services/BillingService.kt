package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.exceptions.*
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*

import kotlin.text.*

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val dal: AntaeusDal
)
{
    public fun processUnpaidInvoices()
    {
        val unpaidInvoices = dal.fetchUnpaidInvoices()
        unpaidInvoices.forEach{
            try{
                processInvoice(invoice = it)
            } catch (e :LackOfFundsException) {
                println(e)
            }
        }
    }

    public  fun processInvoice(invoice: Invoice): Invoice?
    {
        val status = invoice.status
        val customer = invoice.customerId
        val invoiceId = invoice.id

        if(status == InvoiceStatus.PENDING)
        {
            if (paymentProvider.charge(invoice)) {
                println("CUSTOMER $customer PAID INVOICE $invoiceId SUCCESSFULLY..")
                return dal.setInvoiceStatus(invoice = invoice, newStatus = InvoiceStatus.PAID)
            } else {
                throw LackOfFundsException(invoiceId, customer)
            }
        }
        return invoice
    }

}