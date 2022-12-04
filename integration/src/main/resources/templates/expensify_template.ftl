<#if addHeader == true>
    Merchant,Original Amount,Category,Comment,Currency,Invoice ID,Invoice URL,Transaction ID,Report number,Expense number<#lt>
</#if>
<#assign reportNumber = 1>
<#assign expenseNumber = 1>
<#list reports as report>
    <#list report.transactionList as expense>
        ${expense.merchant},<#t>
    <#-- note: expense.amount prints the original amount only -->
        ${expense.amount},<#t>
        ${expense.category},<#t>
        ${expense.comment},<#t>
        ${expense.currency},<#t>
        ${expense.receiptID},<#t>
        ${expense.receiptObject.url },<#t>
        ${expense.transactionID},<#t>
        ${reportNumber},<#t>
        ${expenseNumber}<#lt>
        <#assign expenseNumber = expenseNumber + 1>
    </#list>
    <#assign reportNumber = reportNumber + 1>
</#list>