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
            processInvoice(invoice = it)
        }
    }

    private  fun processInvoice(invoice: Invoice)
    {
        val status = invoice.status
        val customer = invoice.customerId
        val invoiceId = invoice.id

        if(status == InvoiceStatus.PENDING)
        {
            try {
                if (paymentProvider.charge(invoice)) {
                    println("CUSTOMER $customer PAID INVOICE $invoiceId SUCCESSFULLY..")
                    dal.setInvoiceStatus(id = invoice.id, newStatus = InvoiceStatus.PAID)
                } else {
                    println("CUSTOMER $customer COULD NOT PAY INVOICE $invoiceId DUE TO LACK OF FUNDS, TRYING AGAIN..")
                }
            }
            catch(e: CustomerNotFoundException)
            {
                println("CustomerNotFoundException: $e")
            }
            catch(e: CurrencyMismatchException)
            {
                println("CurrencyMismatchException: $e")
            }
            catch(e: NetworkException)
            {
                println("NetworkException: $e")
            }
        }
    }

}