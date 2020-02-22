package io.pleo.antaeus.core.exceptions

class LackOfFundsException(invoiceId: Int, customerId: Int) :
        Exception("Customer '$customerId' cannot pay invoice '$invoiceId' due to lack of funds")
