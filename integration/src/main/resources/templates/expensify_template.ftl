<#if addHeader == true>
    Merchant,Amount,Category,Comment,Currency,Invoice ID,Invoice URL,Transaction ID,Report number,Expense number<#lt>
</#if>
<#assign reportNumber = 1>
<#assign expenseNumber = 1>
<#list reports as report>
    <#list report.transactionList as expense>
        <#if expense.modifiedMerchant?has_content>
            <#assign merchant = expense.modifiedMerchant>
        <#else>
            <#assign merchant = expense.merchant>
        </#if>
        <#if expense.convertedAmount?has_content>
            <#assign amount = expense.convertedAmount/100>
        <#elseif expense.modifiedAmount?has_content>
            <#assign amount = expense.modifiedAmount/100>
        <#else>
            <#assign amount = expense.amount/100>
        </#if>
        ${merchant},<#t>
        ${amount},<#t>
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